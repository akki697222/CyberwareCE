package flaxbeard.cyberware.common.contents.block.entity;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareSurgeryEvent;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.BodyRegion;
import flaxbeard.cyberware.client.menu.SurgeryMenu;
import flaxbeard.cyberware.common.*;
import flaxbeard.cyberware.common.contents.block.SurgeryChamberBlock;
import flaxbeard.cyberware.common.contents.item.CyberwareItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static flaxbeard.cyberware.api.CyberwareConstants.WARE_PER_SLOT;
import static flaxbeard.cyberware.api.CyberwareConstants.WARE_SLOT_NUM;
import static flaxbeard.cyberware.common.contents.block.SurgeryChamberBlock.DOOR;

public class SurgeryBlockEntity extends BlockEntity implements BlockEntityTicker<SurgeryBlockEntity>, MenuProvider, Container {
    private ItemStackHandler playerWares = new ItemStackHandler(WARE_SLOT_NUM);
    private ItemStackHandler wares = new ItemStackHandler(WARE_SLOT_NUM);
    private ContainerData containerData = new SimpleContainerData(0);
    private final List<Boolean> discards;
    private final List<Boolean> essentialMissing;
    private int essence = 0;
    private int maxEssence = 0;
    private int slotIncompatible = 0;
    private int cooldownAfterSurgery = 0;
    private int lastEntityId = -1;
    private boolean inProgress = false;
    private int progress = 0;
    private LivingEntity target = null;
    private boolean isEnergyInsufficient = false;

    public SurgeryBlockEntity(BlockPos pos, BlockState blockState) {
        super(CyberwareBlockEntities.SURGERY.get(), pos, blockState);

        this.discards = new ArrayList<>(Collections.nCopies(WARE_SLOT_NUM, false));
        this.essentialMissing = new ArrayList<>(Collections.nCopies(BodyRegion.values().length * 2, false));
    }

    public boolean canUseByPlayer(Player player) {
        BlockPos pos = getBlockPos();
        Level level = getLevel();
        if (level != null) {
            return level.getBlockEntity(pos) == this
                    && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
        }
        return false;
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);

        wares.deserializeNBT(registries, tag.getCompound("Inventory"));
        playerWares.deserializeNBT(registries, tag.getCompound("PlayerInventory"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("Inventory", wares.serializeNBT(registries));
        tag.put("PlayerInventory", playerWares.serializeNBT(registries));
    }

    public void updatePlayerWares(LivingEntity livingEntity, ICyberwareUserData cyberwareUserData) {
        setChanged();

        if (cyberwareUserData != null) {
            if (livingEntity.getId() != lastEntityId) {
                Collections.fill(discards, false);
                lastEntityId = livingEntity.getId();
            }
            maxEssence = cyberwareUserData.getMaxTolerance(livingEntity);

            for (BodyRegion bodyRegion : BodyRegion.values()) {
                NonNullList<ItemStack> wares = cyberwareUserData.getInstalledCyberware(bodyRegion);
                System.out.println(wares);
                for (int i = 0; i < WARE_PER_SLOT; i++) {
                    ItemStack toPut = wares.get(i).copy();

                    if (!ItemStack.isSameItem(toPut, playerWares.getStackInSlot(bodyRegion.ordinal() * WARE_PER_SLOT + i))) {
                        discards.set(bodyRegion.ordinal() * WARE_PER_SLOT + i, doesItemConflict(toPut, bodyRegion, i));
                    }
                    playerWares.setStackInSlot(bodyRegion.ordinal() * WARE_PER_SLOT + i, toPut);
                }
                updateEssential(bodyRegion);
            }

            boolean needToCheck = true;
            while (needToCheck) {
                needToCheck = false;
                for (BodyRegion bodyRegion : BodyRegion.values()) {
                    for (int i = 0; i < WARE_PER_SLOT; i++) {
                        int index = bodyRegion.ordinal() * WARE_PER_SLOT + i;

                        ItemStack stack = wares.getStackInSlot(index);
                        if (!stack.isEmpty() && !areRequirementsFulfilled(stack, i)) {
                            addItemStack(livingEntity, stack);
                            wares.setStackInSlot(index, ItemStack.EMPTY);
                            needToCheck = true;
                        }
                    }
                }
            }

            updateEssence();
        } else {
            playerWares = new ItemStackHandler(WARE_SLOT_NUM);
            maxEssence = CyberwareConfig.essence;
            for (BodyRegion bodyRegion : BodyRegion.values()) {
                updateEssential(bodyRegion);
            }
        }
        slotIncompatible = -1;
    }

    public void updateEssential(BodyRegion bodyRegion) {
        if (bodyRegion.hasEssential()) {
            byte ans = isEssential(bodyRegion);
            boolean foundFirst = (ans & 1) > 0;
            boolean foundSecond = (ans & 2) > 0;
            essentialMissing.set(bodyRegion.ordinal() * 2, !foundFirst);
            essentialMissing.set(bodyRegion.ordinal() * 2 + 1, !foundSecond);
        } else {
            essentialMissing.set(bodyRegion.ordinal() * 2, false);
            essentialMissing.set(bodyRegion.ordinal() * 2 + 1, false);
        }
    }

    private byte isEssential(BodyRegion bodyRegion) {
        byte r = 0;

        for (int indexSlot = 0; indexSlot < WARE_PER_SLOT; indexSlot++) {
            int index = bodyRegion.ordinal() * WARE_PER_SLOT + indexSlot;
            ItemStack slotStack = wares.getStackInSlot(index);
            ItemStack playerStack = playerWares.getStackInSlot(index);

            ItemStack stack = !slotStack.isEmpty() ? slotStack :
                    (discards.get(index) ? ItemStack.EMPTY : playerStack);

            if (!stack.isEmpty()) {
                ICyberware ware = CyberwareAPI.getCyberware(stack);
                if (ware.isEssential()) {
                    if (bodyRegion.isSided() && ware instanceof ICyberware.ISidedLimb sidedLimb) {
                        if (sidedLimb.getSide() == ICyberware.ISidedLimb.Side.LEFT && (r & 1) == 0) {
                            r += 1;
                        } else if ((r & 2) == 0) {
                            r += 2;
                        }
                    } else {
                        return 3;
                    }
                }
            }
        }
        return r;
    }

    public void updateEssence() {
        essence = maxEssence;
        boolean hasConsume = false;
        boolean hasProduce = false;

        for (BodyRegion bodyRegion : BodyRegion.values()) {
            for (int indexSlot = 0; indexSlot < WARE_PER_SLOT; indexSlot++) {
                int index = bodyRegion.ordinal() * WARE_PER_SLOT + indexSlot;
                ItemStack slotStack = wares.getStackInSlot(index);
                ItemStack playerStack = playerWares.getStackInSlot(index);

                ItemStack stack = !slotStack.isEmpty() ? slotStack :
                        (discards.get(index) ? ItemStack.EMPTY : playerStack);

                if (!stack.isEmpty()) {
                    ItemStack returns = stack.copy();
                    if (!slotStack.isEmpty()
                            && !returns.isEmpty()
                            && !playerStack.isEmpty()
                            && CyberwareAPI.areCyberwareStacksEqual(playerStack, returns)) {
                        int maxSize = CyberwareAPI.getCyberware(returns).getMaxInstalls();

                        if (returns.getCount() < maxSize) {
                            int toShift = Math.min(maxSize - returns.getCount(), playerStack.getCount());
                            returns.grow(toShift);
                        }
                    }

                    ICyberware ware = CyberwareAPI.getCyberware(returns);
                    essence -= ware.getEssenceCost();

                    hasConsume = ware instanceof CyberwareItem cyberwareItem && cyberwareItem.getEnergyConsumption() > 0;
                    hasProduce = ware instanceof CyberwareItem cyberwareItem && cyberwareItem.getEnergyProduction() > 0;
                }
            }
        }

        isEnergyInsufficient = hasConsume && !hasProduce;
    }

    private void addItemStack(LivingEntity livingEntity, ItemStack stack) {
        boolean flag = true;
        if (livingEntity instanceof Player player) {
            flag = !player.getInventory().add(stack);
        }

        if (flag && CyberwareAPI.checkServer(level)) {
            ItemEntity item = new ItemEntity(level, getBlockPos().getX() + .5F, getBlockPos().getY() - 2F, getBlockPos().getZ() + .5F, stack);
            level.addFreshEntity(item);
        }
    }

    public boolean areRequirementsFulfilled(ItemStack stack, int indexSlotToCheck) {
        if (!stack.isEmpty()) {
            ICyberware ware = CyberwareAPI.getCyberware(stack);
            for (ItemStack wareRequires : ware.requiredCyberwares()) {
                boolean found = false;

                outer:
                for (int row = 0; row < BodyRegion.values().length; row++) {
                    for (int indexSlot = 0; indexSlot < WARE_PER_SLOT; indexSlot++) {
                        if (indexSlot != indexSlotToCheck) {
                            int index = row * WARE_PER_SLOT + indexSlot;
                            ItemStack slotStack = wares.getStackInSlot(index);
                            ItemStack playerStack = playerWares.getStackInSlot(index);

                            ItemStack otherStack = !slotStack.isEmpty() ? slotStack :
                                    (discards.get(index) ? ItemStack.EMPTY : playerStack);

                            if (ItemStack.isSameItem(otherStack, wareRequires)) {
                                found = true;
                                break outer;
                            }
                        }
                    }
                }
                if (!found) return false;
            }
        }

        return true;
    }

    public boolean canOpenChamberDoor() {
        return !inProgress && cooldownAfterSurgery <= 0;
    }

    public void notifyChange() {
        if (level == null) return;
        BlockPos pos = getBlockPos();
        BlockState state = getBlockState();

        BlockPos chamberPos = pos.below();
        BlockState chamberState = level.getBlockState(chamberPos);

        if (!(chamberState.getBlock() instanceof SurgeryChamberBlock)) return;

        boolean opened = chamberState.getValue(DOOR);

        if (!opened) {
            List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class,
                    new AABB(pos.getX(), pos.getY() - 2F, pos.getZ(), pos.getX() + 1F, pos.getY(), pos.getZ() + 1F));

            if (livingEntities.size() == 1) {
                LivingEntity livingEntity = livingEntities.getFirst();
                CyberwareSurgeryEvent.Pre preSurgeryEvent = new CyberwareSurgeryEvent.Pre(livingEntity, playerWares, wares);

                if (!NeoForge.EVENT_BUS.post(preSurgeryEvent).isCanceled()) {
                    this.inProgress = true;
                    this.progress = 0;
                    this.target = livingEntity;
                } else {
                    ((SurgeryChamberBlock) state.getBlock()).toggleDoor(level, pos, state);
                }
            }
        }
    }

    @Override
    public int getContainerSize() {
        return wares.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < wares.getSlots(); i++) {
            if (!wares.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return wares.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return wares.extractItem(slot, amount, false);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = wares.getStackInSlot(slot);
        wares.setStackInSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        wares.setStackInSlot(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return canUseByPlayer(player);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < wares.getSlots(); i++) {
            wares.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public boolean doesItemConflict(@Nonnull ItemStack stack, BodyRegion bodyRegion, int indexSlotToCheck) {
        int row = bodyRegion.ordinal();
        if (!stack.isEmpty()) {
            for (int i = 0; i < WARE_PER_SLOT; i++) {
                if (i != indexSlotToCheck) {
                    int index = row * WARE_PER_SLOT + i;
                    ItemStack slotStack = wares.getStackInSlot(index);
                    ItemStack playerStack = playerWares.getStackInSlot(index);
                    ItemStack otherStack = !slotStack.isEmpty() ? slotStack
                            : discards.get(index) ? ItemStack.EMPTY : playerStack;

                    if (ItemStack.isSameItem(otherStack, stack)) {
                        slotIncompatible = index;
                        return true;
                    }

                    if (!otherStack.isEmpty() &&
                            (CyberwareAPI.getCyberware(otherStack).isIncompatible(stack)
                            || CyberwareAPI.getCyberware(stack).isIncompatible(otherStack))) {
                        slotIncompatible = index;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void resetSurgery() {
        inProgress = false;
        progress = 0;
        target = null;
        if (level == null) return;
        BlockPos pos = getBlockPos();
        if (level.getBlockState(pos.below()).getBlock() instanceof SurgeryChamberBlock surgeryChamberBlock) {
            surgeryChamberBlock.toggleDoor(level, pos.below(), level.getBlockState(pos.below()));
        }
    }

    @Override
    public void tick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState blockState, @NotNull SurgeryBlockEntity surgeryBlockEntity) {
        if (level.isClientSide()) return;

        if (inProgress && progress < 80) {
            ICyberwareUserData cyberwareUserData = target.getData(CyberwareAttachments.CYBERWARE_USER_DATA);

            if (target != null && !target.isDeadOrDying()) {
                if (progress > 20 && progress < 60) {
                    target.setPos(pos.getX() + .5F, target.getY(), pos.getZ() + .5F);
                }

                if (progress >= 20 && progress <= 60 && progress % 5 == 0) {
                    target.hurt(CyberwareDamageTypes.surgery(level), 2F);
                }

                if (progress == 60) {
                    processTick(cyberwareUserData);
                }

                progress++;
            } else {
                resetSurgery();
            }
        } else if (inProgress) {
            resetSurgery();
        }

        if (cooldownAfterSurgery > 0) {
            cooldownAfterSurgery--;
        }
    }

    public void processTick(ICyberwareUserData cyberwareUserData) {
        updatePlayerWares(target, cyberwareUserData);

        for (int indexWareSlot = 0; indexWareSlot < BodyRegion.values().length; indexWareSlot++) {
            BodyRegion bodyRegion = BodyRegion.valueOf(indexWareSlot);

            NonNullList<ItemStack> toInstall = NonNullList.create();
            for (int indexSlot = 0; indexSlot < WARE_PER_SLOT; indexSlot++) {
                toInstall.add(ItemStack.EMPTY);
            }

            int indexToInstall = 0;
            for (int indexWare = indexWareSlot * WARE_PER_SLOT; indexWare < (indexWareSlot + 1) * WARE_PER_SLOT; indexWare++) {
                ItemStack stackToInstall = wares.getStackInSlot(indexWare);
                ItemStack stackPlayer = playerWares.getStackInSlot(indexWare);

                if (!stackToInstall.isEmpty()) {
                    ItemStack stackToSet = stackToInstall.copy();

                    if (CyberwareAPI.areCyberwareStacksEqual(stackToSet, stackPlayer)) {
                        int maxSize = CyberwareAPI.getCyberware(stackToSet).getMaxInstalls();

                        if (stackToSet.getCount() < maxSize) {
                            int toShift = Math.min(maxSize - stackToSet.getCount(), stackPlayer.getCount());
                            stackPlayer.shrink(toShift);
                            stackToSet.grow(toShift);
                        }
                    }

                    if (!stackPlayer.isEmpty()) {
                        CyberwareAPI.sanitize(stackPlayer);
                        addItemStack(target, stackPlayer);
                    }

                    toInstall.set(indexToInstall, stackToSet);
                    indexToInstall++;
                } else if (!stackPlayer.isEmpty()) {
                    if (discards.get(indexWare)) {
                        CyberwareAPI.sanitize(stackPlayer);
                        addItemStack(target, stackPlayer);
                    } else {
                        toInstall.set(indexToInstall, playerWares.getStackInSlot(indexWare).copy());
                        indexToInstall++;
                    }
                }
            }
            if (CyberwareAPI.checkServer(level)) {
                cyberwareUserData.setInstalledCyberware(target, bodyRegion, toInstall);
                Cyberware.logger.debug("Cyberware installed to BodyRegion: {}.\nInstalled:{} \nTarget: {}", bodyRegion, toInstall, target);
            }
            cyberwareUserData.setHasEssential(bodyRegion, !essentialMissing.get(indexWareSlot * 2), !essentialMissing.get(indexWareSlot * 2 + 1));
        }
        cyberwareUserData.setTolerance(target, essence);
        cyberwareUserData.updateCapacity();
        cyberwareUserData.setImmune();
        if (CyberwareAPI.checkServer(level)) {
            CyberwareAPI.updateData(target);
        }
        wares = new ItemStackHandler(WARE_SLOT_NUM);

        CyberwareSurgeryEvent.Post postSurgeryEvent = new CyberwareSurgeryEvent.Post(target);
        NeoForge.EVENT_BUS.post(postSurgeryEvent);
    }

    public boolean isEnergyInsufficient() {
        return isEnergyInsufficient;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new SurgeryMenu(i, inventory, this, containerData);
    }
}
