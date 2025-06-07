package flaxbeard.cyberware.client.gui;

import flaxbeard.cyberware.api.CyberwareAPI; // Assuming path
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot; // Assuming path
import flaxbeard.cyberware.common.block.entity.SurgeryBlockEntity; // Assuming path
import flaxbeard.cyberware.common.lib.LibConstants; // Assuming path
import flaxbeard.cyberware.common.registry.CWMenuTypes; // Assuming a new registry class for MenuTypes

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraftforge.items.IItemHandler;
// No longer need direct SlotItemHandler for surgery slots, SlotSurgery extends it.
import net.minecraft.world.level.block.entity.BlockEntity;

public class SurgeryMenu extends AbstractContainerMenu {

    public final SurgeryBlockEntity blockEntity;
    private final Inventory playerInventory;
    private final Level level;

    // Data mirrored from BlockEntity for easier access by Screen, potentially refactor later
    public IItemHandler slotsPlayer;
    public IItemHandler slotsSurgery;
    public boolean[] discardSlots;
    public boolean[] isEssentialMissing;
    public int essence;
    public int maxEssence;
    public int wrongSlot = -1;
    public int ticksWrong = 0;
    public boolean missingPower = false;

    private final int numSurgerySlots;

    // Server-side constructor
    public SurgeryMenu(int windowId, Inventory playerInventory, SurgeryBlockEntity blockEntity) {
        super(CWMenuTypes.SURGERY_MENU.get(), windowId);
        this.playerInventory = playerInventory;
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();
        this.numSurgerySlots = EnumSlot.values().length * LibConstants.WARE_PER_SLOT;

        if (blockEntity != null) {
            this.slotsPlayer = blockEntity.getSlotsPlayer();
            this.slotsSurgery = blockEntity.getSlotsSurgery();
            this.discardSlots = blockEntity.getDiscardSlots();
            this.isEssentialMissing = blockEntity.getIsEssentialMissing();
            this.essence = blockEntity.getEssence();
            this.maxEssence = blockEntity.getMaxEssence();
            this.missingPower = blockEntity.isMissingPower();
        } else {
            // Initialize with defaults if blockEntity is null to prevent NPEs, though this is an error state
            this.slotsPlayer = null; // Or a dummy empty handler
            this.slotsSurgery = null; // Or a dummy empty handler
            this.discardSlots = new boolean[this.numSurgerySlots];
            this.isEssentialMissing = new boolean[EnumSlot.values().length * 2]; // TODO: Check size, EnumSlot.values().length might be enough if BE uses that size
            this.essence = 0;
            this.maxEssence = 0;
            this.missingPower = false;
        }


        // Add surgery slots
        int indexContainerSlot = 0;
        for (EnumSlot slotType : EnumSlot.values()) {
            for (int i = 0; i < LibConstants.WARE_PER_SLOT; i++) { // i is indexInSlotType
                // First 8 slots visible, rest hidden by x,y coords
                int x = (i < 8) ? (9 + 20 * i) : Integer.MIN_VALUE;
                int y = (i < 8) ? 109 : Integer.MIN_VALUE;

                // Ensure slotsSurgery and slotsPlayer are not null before creating SlotSurgery
                if (this.slotsSurgery != null && this.slotsPlayer != null) {
                    this.addSlot(new SlotSurgery(this, this.slotsSurgery, this.slotsPlayer, indexContainerSlot, x, y, slotType, i));
                } else {
                    // Add a dummy slot or handle error if item handlers are null
                    // For now, adding a non-functional basic slot to prevent crash during construction
                    // This needs a valid inventory, using playerInventory temporarily for a slot that should not be visible/interactable.
                    this.addSlot(new Slot(playerInventory, 0, Integer.MIN_VALUE, Integer.MIN_VALUE));
                }
                indexContainerSlot++;
            }
        }

        // Add player inventory slots
        int playerInvX = 8;
        int playerInvY = 140;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, playerInvX + col * 18, playerInvY + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, playerInvX + col * 18, playerInvY + 58));
        }
    }

    // Client-side constructor
    public SurgeryMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data));
        // Client side values will be synced by calling updateClientSyncedValues from the screen
    }

    private static SurgeryBlockEntity getBlockEntity(Inventory playerInventory, FriendlyByteBuf data) {
        if (data == null) return null;
        BlockPos pos = data.readBlockPos();
        Level level = playerInventory.player.level();
        if (level == null) return null;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SurgeryBlockEntity) {
            return (SurgeryBlockEntity) be;
        }
        return null;
    }

    public void updateClientSyncedValues() {
        if (this.blockEntity != null && this.level.isClientSide) {
            this.isEssentialMissing = this.blockEntity.getIsEssentialMissing();
            this.essence = this.blockEntity.getEssence();
            this.maxEssence = this.blockEntity.getMaxEssence();
            this.missingPower = this.blockEntity.isMissingPower();
            this.wrongSlot = this.blockEntity.getWrongSlot();
            this.ticksWrong = this.blockEntity.getTicksWrong();
            this.discardSlots = this.blockEntity.getDiscardSlots();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.blockEntity == null) {
            return false;
        }
        return this.blockEntity.isUsableByPlayer(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            int playerMainInvStart = this.numSurgerySlots;
            int playerHotbarStart = playerMainInvStart + 27;
            int endContainerIndex = playerHotbarStart + 9;

            if (index < this.numSurgerySlots) { // Clicked in a surgery slot
                if (!this.moveItemStackTo(itemstack1, playerMainInvStart, endContainerIndex, true)) {
                    return ItemStack.EMPTY;
                }
                // original logic in ContainerSurgery was slot.onSlotChange(itemstack, itemstack1);
                // For SlotItemHandler based slots, onQuickCraft is more appropriate if specific logic is needed on shift-click itself.
                // SlotSurgery's onTake will be called if items are successfully moved.
                // If SlotSurgery needs to react to the *attempt* or specific quick move context, it can override onQuickCraft.
                 if (slot instanceof SlotSurgery) { // Ensure it's our slot
                    ((SlotSurgery) slot).onQuickCraft(itemstack, itemstack1); // Call with old and new stack (which is now in player inv)
                }

            } else if (index >= playerMainInvStart) { // Clicked in player inventory
                boolean movedToSurgery = false;
                if (CyberwareAPI.isCyberware(itemstack1)) {
                    if (this.moveItemStackTo(itemstack1, 0, this.numSurgerySlots, false)) {
                        movedToSurgery = true;
                    }
                }

                if (!movedToSurgery) {
                    if (index >= playerMainInvStart && index < playerHotbarStart) {
                        if (!this.moveItemStackTo(itemstack1, playerHotbarStart, endContainerIndex, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= playerHotbarStart && index < endContainerIndex) {
                        if (!this.moveItemStackTo(itemstack1, playerMainInvStart, playerHotbarStart, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    public boolean doesItemConflict(ItemStack stack, EnumSlot slotType, int indexSlotToCheck) {
        if (this.blockEntity == null) return false;
        return this.blockEntity.doesItemConflict(stack, slotType, indexSlotToCheck, this.slotsSurgery, this.slotsPlayer, this.discardSlots);
    }

    public boolean areRequirementsFulfilled(ItemStack stack, EnumSlot slotType, int indexSlotToCheck) {
        if (this.blockEntity == null) return true;
        return this.blockEntity.areRequirementsFulfilled(stack, slotType, indexSlotToCheck, this.slotsSurgery, this.slotsPlayer, this.discardSlots);
    }

    public boolean canDisableItem(ItemStack stack, EnumSlot slotType, int indexSlotToCheck) {
        if (this.blockEntity == null) return true;
        return this.blockEntity.canDisableItem(stack, slotType, indexSlotToCheck, this.slotsSurgery, this.slotsPlayer, this.discardSlots);
    }

    public void disableDependants(ItemStack stack, EnumSlot slotType, int indexSlotToCheck) {
        if (this.blockEntity != null) this.blockEntity.disableDependants(stack, slotType, indexSlotToCheck, this.slotsSurgery, this.slotsPlayer, this.discardSlots);
    }

    public void enableDependsOn(ItemStack stack, EnumSlot slotType, int indexSlotToCheck) {
         if (this.blockEntity != null) this.blockEntity.enableDependsOn(stack, slotType, indexSlotToCheck, this.slotsSurgery, this.slotsPlayer, this.discardSlots);
    }

    public void callUpdateEssentialOnBE(EnumSlot slotType) {
        if (this.blockEntity != null) {
            this.blockEntity.updateEssential(slotType, this.slotsSurgery, this.slotsPlayer, this.discardSlots);
            this.isEssentialMissing = this.blockEntity.getIsEssentialMissing();
        }
    }

    public void callUpdateEssenceAndPowerOnBE() {
        if (this.blockEntity != null) {
            this.blockEntity.updateEssence(this.slotsSurgery, this.slotsPlayer, this.discardSlots);
            this.essence = this.blockEntity.getEssence();
            this.missingPower = this.blockEntity.isMissingPower();
        }
    }

    public void callSetWrongSlotOnBE(int index) {
        if (this.blockEntity != null) {
            this.blockEntity.setWrongSlot(index);
            this.wrongSlot = this.blockEntity.getWrongSlot();
            this.ticksWrong = this.blockEntity.getTicksWrong();
        }
    }
}
