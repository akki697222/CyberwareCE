package flaxbeard.cyberware.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.hud.HudData;
import flaxbeard.cyberware.api.item.CyberwareData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberware.*;
import flaxbeard.cyberware.api.item.ICyberware.ISidedLimb.Side;
import flaxbeard.cyberware.api.item.IHudjack;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.api.util.NonNullListUtil;
import flaxbeard.cyberware.common.CyberwareAttributes;
import flaxbeard.cyberware.common.CyberwareComponents;
import flaxbeard.cyberware.common.CyberwareConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;

import javax.annotation.Nonnull;
import java.util.*;

public class CyberwareUserData implements ICyberwareUserData {
    private final EnumMap<BodyRegion, NonNullList<ItemStack>> cyberwaresBySlot;
    private final boolean[] missingEssentials;
    private int powerStored;
    private int powerProduction;
    private int powerLastProduction;
    private int powerConsumption;
    private int powerLastConsumption;
    private int powerCapacity;
    private Map<ItemStack, Integer> powerBuffer;
    private Map<ItemStack, Integer> powerLastBuffer;
    private NonNullList<ItemStack> powerOutages;
    private List<Integer> ticksPowerOutages;
    private int missingEssence;
    private NonNullList<ItemStack> specialBatteries;
    private NonNullList<ItemStack> activeItems;
    private NonNullList<ItemStack> hudjackItems;
    private Map<Integer, ItemStack> hotkeys;
    private HudData hudData;
    private boolean hasOpenedRadialMenu;
    private int hudColor;
    private float[] hudColorFloat;
    private boolean isImmune;

    public CyberwareUserData() {
        this.cyberwaresBySlot = new EnumMap<>(BodyRegion.class);
        for (BodyRegion slot : BodyRegion.values()) {
            NonNullList<ItemStack> nnl = NonNullList.withSize(CyberwareConstants.WARE_PER_SLOT, ItemStack.EMPTY);
            cyberwaresBySlot.put(slot, nnl);
        }
        this.missingEssentials = new boolean[BodyRegion.values().length * 2];
        this.powerStored = 0;
        this.powerProduction = 0;
        this.powerLastProduction = 0;
        this.powerConsumption = 0;
        this.powerLastConsumption = 0;
        this.powerCapacity = 0;
        this.powerBuffer = new HashMap<>();
        this.powerLastBuffer = new HashMap<>();
        this.powerOutages = NonNullList.create();
        this.ticksPowerOutages = new ArrayList<>();
        this.missingEssence = 0;
        this.specialBatteries = NonNullList.create();
        this.activeItems = NonNullList.create();
        this.hudjackItems = NonNullList.create();
        this.hotkeys = new HashMap<>();
        this.hudData = new HudData();
        this.hasOpenedRadialMenu = false;
        this.hudColor = 0x00FFFF;
        this.hudColorFloat = new float[]{0.0F, 1.0F, 1.0F};
        this.isImmune = false;
        resetWare(null);
    }

    @Override
    public NonNullList<ItemStack> getInstalledCyberware(BodyRegion slot) {
        return cyberwaresBySlot.getOrDefault(slot, NonNullList.withSize(CyberwareConstants.WARE_PER_SLOT, ItemStack.EMPTY));
    }

    @Override
    public void setInstalledCyberware(LivingEntity livingEntity, BodyRegion slot, List<ItemStack> cyberwaresToInstall) {
        while (cyberwaresToInstall.size() > CyberwareConstants.WARE_PER_SLOT) {
            cyberwaresToInstall.remove(cyberwaresToInstall.size() - 1);
        }
        while (cyberwaresToInstall.size() < CyberwareConstants.WARE_PER_SLOT) {
            cyberwaresToInstall.add(ItemStack.EMPTY);
        }
        setInstalledCyberware(livingEntity, slot, NonNullListUtil.fromArray(cyberwaresToInstall.toArray(new ItemStack[0])));
    }

    @Override
    public void setInstalledCyberware(LivingEntity livingEntity, BodyRegion slot, NonNullList<ItemStack> cyberwaresToInstall) {
        if (cyberwaresToInstall.size() != cyberwaresBySlot.get(slot).size()) {
            Cyberware.logger.error("Invalid number of cyberware to install: found {}, expecting {}", cyberwaresToInstall.size(), cyberwaresBySlot.get(slot).size());
        }
        NonNullList<ItemStack> cyberwaresInstalled = cyberwaresBySlot.get(slot);
        if (livingEntity != null) {
            for (ItemStack itemStackInstalled : cyberwaresInstalled) {
                if (!CyberwareAPI.isCyberware(itemStackInstalled)) continue;
                boolean found = false;
                for (ItemStack itemStackToInstall : cyberwaresToInstall) {
                    if (CyberwareAPI.areCyberwareStacksEqual(itemStackToInstall, itemStackInstalled) &&
                            itemStackToInstall.getCount() == itemStackInstalled.getCount()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    CyberwareAPI.getCyberware(itemStackInstalled).onRemoved(livingEntity, itemStackInstalled);
                }
            }
            for (ItemStack itemStackToInstall : cyberwaresToInstall) {
                if (!CyberwareAPI.isCyberware(itemStackToInstall)) continue;
                boolean found = false;
                for (ItemStack oldWare : cyberwaresInstalled) {
                    if (CyberwareAPI.areCyberwareStacksEqual(itemStackToInstall, oldWare) &&
                            itemStackToInstall.getCount() == oldWare.getCount()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    CyberwareAPI.getCyberware(itemStackToInstall).onAdded(livingEntity, itemStackToInstall);
                }
            }
        }
        cyberwaresBySlot.put(slot, cyberwaresToInstall);
    }

    @Override
    public boolean isCyberwareInstalled(ItemStack cyberware) {
        return getCyberwareRank(cyberware) > 0;
    }

    @Override
    public int getCyberwareRank(ItemStack cyberwareTemplate) {
        ItemStack cyberwareFound = getCyberware(cyberwareTemplate);
        return !cyberwareFound.isEmpty() ? cyberwareFound.getCount() : 0;
    }

    @Override
    public ItemStack getCyberware(ItemStack cyberware) {
        for (ItemStack itemStack : getInstalledCyberware(CyberwareAPI.getCyberware(cyberware).getBodyRegion())) {
            if (!itemStack.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, cyberware)) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void updateCapacity() {
        powerCapacity = 0;
        specialBatteries = NonNullList.create();
        activeItems = NonNullList.create();
        hudjackItems = NonNullList.create();
        hotkeys = new HashMap<>();
        for (BodyRegion slot : BodyRegion.values()) {
            for (ItemStack itemStackCyberware : getInstalledCyberware(slot)) {
                if (CyberwareAPI.isCyberware(itemStackCyberware)) {
                    ICyberware cyberware = CyberwareAPI.getCyberware(itemStackCyberware);
                    if (cyberware instanceof IMenuItem && ((IMenuItem) cyberware).hasMenu(itemStackCyberware)) {
                        activeItems.add(itemStackCyberware);
                        CyberwareData data = itemStackCyberware.get(CyberwareComponents.CYBERWARE_DATA);
                        int hotkey = -1;
                        if (data != null) {
                            hotkey = data.getHotkey();
                        }
                        if (hotkey != -1) {
                            hotkeys.put(hotkey, itemStackCyberware);
                        }
                    }
                    if (cyberware instanceof IHudjack) {
                        hudjackItems.add(itemStackCyberware);
                    }
                    if (cyberware instanceof ISpecialBattery) {
                        specialBatteries.add(itemStackCyberware);
                    } else {
                        powerCapacity += cyberware.getEnergyCapacity();
                    }
                }
            }
        }
        powerStored = Math.min(powerStored, powerCapacity);
    }

    @Override
    public void resetBuffer() {
        canGiveOut = true;
        storePower(powerLastBuffer);
        powerLastBuffer = powerBuffer;
        powerBuffer = new HashMap<>(powerBuffer.size());
        isImmune = false;
        powerLastConsumption = powerConsumption;
        powerLastProduction = powerProduction;
        powerProduction = 0;
        powerConsumption = 0;
    }

    @Override
    public void addPower(int amount, ItemStack inputter) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive!");
        ItemStack stack = ItemStack.EMPTY;
        if (!inputter.isEmpty()) {
            if (!inputter.getComponents().isEmpty() || inputter.getCount() != 1) {
                stack = new ItemStack(inputter.getItem(), 1);
            } else {
                stack = inputter;
            }
        }
        powerBuffer.put(stack, powerBuffer.getOrDefault(stack, 0) + amount);
        powerProduction += amount;
    }

    @Override
    public boolean isAtCapacity(ItemStack stack) {
        return isAtCapacity(stack, 0);
    }

    @Override
    public boolean isAtCapacity(ItemStack stack, int buffer) {
        int leftOverSpaceNormal = powerCapacity - powerStored;
        if (leftOverSpaceNormal > buffer) return false;
        int leftOverSpaceSpecial = 0;
        for (ItemStack batteryStack : specialBatteries) {
            ISpecialBattery battery = (ISpecialBattery) CyberwareAPI.getCyberware(batteryStack);
            int spaceInThisSpecial = battery.add(batteryStack, stack, buffer + 1, true);
            leftOverSpaceSpecial += spaceInThisSpecial;
            if (leftOverSpaceNormal + leftOverSpaceSpecial > buffer) return false;
        }
        return true;
    }

    @Override
    public float getPercentFull() {
        return getCapacity() == 0 ? -1F : (float) getStoredPower() / getCapacity();
    }

    @Override
    public int getCapacity() {
        int specialCap = 0;
        for (ItemStack item : specialBatteries) {
            ISpecialBattery battery = (ISpecialBattery) CyberwareAPI.getCyberware(item);
            specialCap += battery.getCapacity(item);
        }
        return powerCapacity + specialCap;
    }

    @Override
    public int getStoredPower() {
        int specialStored = 0;
        for (ItemStack item : specialBatteries) {
            ISpecialBattery battery = (ISpecialBattery) CyberwareAPI.getCyberware(item);
            specialStored += battery.getStoredEnergy(item);
        }
        return powerStored + specialStored;
    }

    @Override
    public int getProduction() {
        return powerLastProduction;
    }

    @Override
    public int getConsumption() {
        return powerLastConsumption;
    }

    @Override
    public boolean usePower(ItemStack stack, int amount) {
        return usePower(stack, amount, true);
    }

    private int computeSum(@Nonnull Map<ItemStack, Integer> map) {
        return map.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void subtractFromBufferLast(int amount) {
        for (Map.Entry<ItemStack, Integer> entry : powerLastBuffer.entrySet()) {
            int get = entry.getValue();
            int amountToSubtract = Math.min(get, amount);
            amount -= amountToSubtract;
            entry.setValue(get - amountToSubtract);
            if (amount <= 0) break;
        }
    }

    private boolean canGiveOut = true;

    @Override
    public boolean usePower(ItemStack stack, int amount, boolean isPassive) {
        if (isImmune) return true;
        if (!canGiveOut) {
            if (FMLEnvironment.dist == Dist.CLIENT) setOutOfPower(stack);
            return false;
        }
        powerConsumption += amount;
        int sumPowerBufferLast = computeSum(powerLastBuffer);
        int amountAvailable = powerStored + sumPowerBufferLast;
        int amountAvailableSpecial = 0;
        if (amountAvailable < amount) {
            int amountMissing = amount - amountAvailable;
            for (ItemStack batteryStack : specialBatteries) {
                ISpecialBattery battery = (ISpecialBattery) CyberwareAPI.getCyberware(batteryStack);
                int extract = battery.extract(batteryStack, amountMissing, true);
                amountMissing -= extract;
                amountAvailableSpecial += extract;
                if (amountMissing <= 0) break;
            }
            if (amountAvailableSpecial + amountAvailable >= amount) {
                amountMissing = amount - amountAvailable;
                for (ItemStack batteryStack : specialBatteries) {
                    ISpecialBattery battery = (ISpecialBattery) CyberwareAPI.getCyberware(batteryStack);
                    int extract = battery.extract(batteryStack, amountMissing, false);
                    amountMissing -= extract;
                    if (amountMissing <= 0) break;
                }
                amount -= amountAvailableSpecial;
            }
        }
        if (amountAvailable < amount) {
            if (FMLEnvironment.dist == Dist.CLIENT) setOutOfPower(stack);
            if (isPassive) canGiveOut = false;
            return false;
        }
        int leftAfterBuffer = Math.max(0, amount - sumPowerBufferLast);
        subtractFromBufferLast(amount);
        powerStored -= leftAfterBuffer;
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void setOutOfPower(ItemStack stack) {
        var entityPlayer = Minecraft.getInstance().player;
        if (entityPlayer != null && !stack.isEmpty()) {
            int indexFound = -1;
            for (int i = 0; i < powerOutages.size(); i++) {
                ItemStack stackExisting = powerOutages.get(i);
                if (!stackExisting.isEmpty() && stackExisting.getItem() == stack.getItem() &&
                        stackExisting.getDamageValue() == stack.getDamageValue()) {
                    indexFound = i;
                    break;
                }
            }
            if (indexFound != -1) {
                powerOutages.remove(indexFound);
                ticksPowerOutages.remove(indexFound);
            }
            powerOutages.add(stack);
            ticksPowerOutages.add(entityPlayer.tickCount);
            if (powerOutages.size() >= 8) {
                powerOutages.removeFirst();
                ticksPowerOutages.removeFirst();
            }
        }
    }

    @Override
    public List<ItemStack> getPowerOutages() {
        return powerOutages;
    }

    @Override
    public List<Integer> getPowerOutageTimes() {
        return ticksPowerOutages;
    }

    @Override
    public void setImmune() {
        isImmune = true;
    }

    @Override
    public boolean hasEssential(BodyRegion slot) {
        return !missingEssentials[slot.ordinal() * 2];
    }

    @Override
    public boolean hasEssential(BodyRegion slot, Side side) {
        return !missingEssentials[slot.ordinal() * 2 + (side == Side.LEFT ? 0 : 1)];
    }

    @Override
    public void setHasEssential(BodyRegion slot, boolean hasLeft, boolean hasRight) {
        missingEssentials[slot.ordinal() * 2] = !hasLeft;
        missingEssentials[slot.ordinal() * 2 + 1] = !hasRight;
    }

    @Override
    public void resetWare(LivingEntity livingEntity) {
        for (NonNullList<ItemStack> nnlCyberwaresInSlot : cyberwaresBySlot.values()) {
            for (ItemStack item : nnlCyberwaresInSlot) {
                if (CyberwareAPI.isCyberware(item)) {
                    CyberwareAPI.getCyberware(item).onRemoved(livingEntity, item);
                }
            }
        }
        missingEssence = 0;
        for (BodyRegion slot : BodyRegion.values()) {
            NonNullList<ItemStack> nnlCyberwaresInSlot = NonNullList.create();
            /*
            NonNullList<ItemStack> startItems = CyberwareConfig.getStartingItems(slot);
            for (ItemStack startItem : startItems) {
                nnlCyberwaresInSlot.add(startItem.copy());
            }
             */
            cyberwaresBySlot.put(slot, nnlCyberwaresInSlot);
        }
        Arrays.fill(missingEssentials, false);
        updateCapacity();
    }

    @Override
    public int getNumActiveItems() {
        return activeItems.size();
    }

    @Override
    public List<ItemStack> getActiveItems() {
        return activeItems;
    }

    @Override
    public void removeHotkey(int i) {
        hotkeys.remove(i);
    }

    @Override
    public void addHotkey(int i, ItemStack stack) {
        hotkeys.put(i, stack);
    }

    @Override
    public ItemStack getHotkey(int i) {
        return hotkeys.getOrDefault(i, ItemStack.EMPTY);
    }

    @Override
    public Iterable<Integer> getHotkeys() {
        return hotkeys.keySet();
    }

    @Override
    public List<ItemStack> getHudjackItems() {
        return hudjackItems;
    }

    @Override
    public void setHudData(HudData tagCompound) {
        this.hudData = tagCompound;
    }

    @Override
    public HudData getHudData() {
        return hudData;
    }

    @Override
    public boolean hasOpenedRadialMenu() {
        return hasOpenedRadialMenu;
    }

    @Override
    public void setOpenedRadialMenu(boolean hasOpenedRadialMenu) {
        this.hasOpenedRadialMenu = hasOpenedRadialMenu;
    }

    @Override
    public void setHudColor(int hexVal) {
        float r = ((hexVal >> 16) & 0x0000FF) / 255F;
        float g = ((hexVal >> 8) & 0x0000FF) / 255F;
        float b = ((hexVal) & 0x0000FF) / 255F;
        setHudColor(new float[]{r, g, b});
    }

    @Override
    public void setHudColor(float[] color) {
        hudColorFloat = color;
        int ri = Math.round(color[0] * 255);
        int gi = Math.round(color[1] * 255);
        int bi = Math.round(color[2] * 255);
        hudColor = (ri << 16) | (gi << 8) | bi;
    }

    @Override
    public int getHudColorHex() {
        return hudColor;
    }

    @Override
    public float[] getHudColor() {
        return hudColorFloat;
    }

    @Override
    public int getMaxTolerance(@Nonnull LivingEntity livingEntity) {
        AttributeInstance attribute = livingEntity.getAttribute(CyberwareAttributes.TOLERANCE);
        if (attribute != null) {
            return (int) attribute.getValue();
        } else {
            throw new IllegalArgumentException("The specified LivingEntity has no tolerance attributes.");
        }
    }

    @Override
    public int getTolerance(@Nonnull LivingEntity livingEntity) {
        return getMaxTolerance(livingEntity) - missingEssence;
    }

    @Override
    public void setTolerance(@Nonnull LivingEntity livingEntity, int amount) {
        missingEssence = getMaxTolerance(livingEntity) - amount;
    }

    @Override
    @Deprecated
    public int getEssence() {
        return getMaxEssence() - missingEssence;
    }

    @Override
    @Deprecated
    public int getMaxEssence() {
        return CyberwareConfig.essence;
    }

    @Override
    @Deprecated
    public void setEssence(int essence) {
        missingEssence = getMaxEssence() - essence;
    }

    private void storePower(Map<ItemStack, Integer> map) {
        for (ItemStack itemStackSpecialBattery : specialBatteries) {
            ISpecialBattery specialBattery = (ISpecialBattery) CyberwareAPI.getCyberware(itemStackSpecialBattery);
            for (Map.Entry<ItemStack, Integer> entryBuffer : map.entrySet()) {
                int amountBuffer = entryBuffer.getValue();
                int amountTaken = specialBattery.add(itemStackSpecialBattery, entryBuffer.getKey(), amountBuffer, false);
                entryBuffer.setValue(amountBuffer - amountTaken);
            }
        }
        powerStored = Math.min(powerCapacity, powerStored + computeSum(map));
    }

    public static class PowerData {
        private final int powerStored;
        private final int powerProduction;
        private final int powerLastProduction;
        private final int powerConsumption;
        private final int powerLastConsumption;
        private final int powerCapacity;
        private final Map<ItemStack, Integer> powerBuffer;
        private final Map<ItemStack, Integer> powerLastBuffer;

        public PowerData(int powerStored, int powerProduction, int powerLastProduction, int powerConsumption,
                         int powerLastConsumption, int powerCapacity, Map<ItemStack, Integer> powerBuffer,
                         Map<ItemStack, Integer> powerLastBuffer) {
            this.powerStored = powerStored;
            this.powerProduction = powerProduction;
            this.powerLastProduction = powerLastProduction;
            this.powerConsumption = powerConsumption;
            this.powerLastConsumption = powerLastConsumption;
            this.powerCapacity = powerCapacity;
            this.powerBuffer = powerBuffer;
            this.powerLastBuffer = powerLastBuffer;
        }

        public static final Codec<PowerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("power_stored").forGetter(d -> d.powerStored),
                Codec.INT.fieldOf("power_production").forGetter(d -> d.powerProduction),
                Codec.INT.fieldOf("power_last_production").forGetter(d -> d.powerLastProduction),
                Codec.INT.fieldOf("power_consumption").forGetter(d -> d.powerConsumption),
                Codec.INT.fieldOf("power_last_consumption").forGetter(d -> d.powerLastConsumption),
                Codec.INT.fieldOf("power_capacity").forGetter(d -> d.powerCapacity),
                Codec.unboundedMap(ItemStack.CODEC, Codec.INT).fieldOf("power_buffer").forGetter(d -> d.powerBuffer),
                Codec.unboundedMap(ItemStack.CODEC, Codec.INT).fieldOf("power_last_buffer").forGetter(d -> d.powerLastBuffer)
        ).apply(instance, PowerData::new));
    }

    public static final Codec<CyberwareUserData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.simpleMap(
                    BodyRegion.CODEC,
                    NonNullList.codecOf(ItemStack.CODEC),
                    Keyable.forStrings(() -> Arrays.stream(BodyRegion.values()).map(Enum::name))
            ).fieldOf("cyberwares_by_slot").forGetter(cyberwareUserData -> cyberwareUserData.cyberwaresBySlot),
            Codec.BOOL.listOf().xmap(
                    list -> {
                        boolean[] booleans = new boolean[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            booleans[i] = list.get(i);
                        }
                        return booleans;
                    },
                    array -> {
                        List<Boolean> booleans = new ArrayList<>();
                        for (int i = 0; i < array.length; i++) {
                            booleans.add(array[i]);
                        }
                        return booleans;
                    }
            ).fieldOf("missing_essentials").forGetter(d -> d.missingEssentials),
            PowerData.CODEC.fieldOf("power_data").forGetter(d -> new PowerData(
                    d.powerStored,
                    d.powerProduction,
                    d.powerLastProduction,
                    d.powerConsumption,
                    d.powerLastConsumption,
                    d.powerCapacity,
                    d.powerBuffer,
                    d.powerLastBuffer
            )),
            Codec.list(ItemStack.CODEC).fieldOf("power_outages").forGetter(d -> d.powerOutages),
            Codec.INT.listOf().fieldOf("ticks_power_outages").forGetter(d -> d.ticksPowerOutages),
            Codec.INT.fieldOf("missing_essence").forGetter(d -> d.missingEssence),
            Codec.list(ItemStack.CODEC).fieldOf("special_batteries").forGetter(d -> d.specialBatteries),
            Codec.list(ItemStack.CODEC).fieldOf("active_items").forGetter(d -> d.activeItems),
            Codec.list(ItemStack.CODEC).fieldOf("hudjack_items").forGetter(d -> d.hudjackItems),
            Codec.unboundedMap(Codec.INT, ItemStack.CODEC).fieldOf("hotkeys").forGetter(d -> d.hotkeys),
            HudData.CODEC.fieldOf("hud_data").forGetter(d -> d.hudData),
            Codec.BOOL.fieldOf("has_opened_radial_menu").forGetter(d -> d.hasOpenedRadialMenu),
            Codec.INT.fieldOf("hud_color").forGetter(d -> d.hudColor),
            Codec.FLOAT.listOf().xmap(
                    list -> new float[]{list.get(0), list.get(1), list.get(2)},
                    array -> List.of(array[0], array[1], array[2])
            ).fieldOf("hud_color_float").forGetter(d -> d.hudColorFloat),
            Codec.BOOL.fieldOf("is_immune").forGetter(d -> d.isImmune)
    ).apply(instance, CyberwareUserData::new));

    private CyberwareUserData(
            Map<BodyRegion, NonNullList<ItemStack>> cyberwaresBySlot,
            boolean[] missingEssentials,
            PowerData powerData,
            List<ItemStack> powerOutages,
            List<Integer> ticksPowerOutages,
            Integer missingEssence,
            List<ItemStack> specialBatteries,
            List<ItemStack> activeItems,
            List<ItemStack> hudjackItems,
            Map<Integer, ItemStack> hotkeys,
            HudData hudData,
            Boolean hasOpenedRadialMenu,
            Integer hudColor,
            float[] hudColorFloat,
            Boolean isImmune) {
        this.cyberwaresBySlot = (EnumMap<BodyRegion, NonNullList<ItemStack>>) cyberwaresBySlot;
        this.missingEssentials = missingEssentials;
        this.powerStored = powerData.powerStored;
        this.powerProduction = powerData.powerProduction;
        this.powerLastProduction = powerData.powerLastProduction;
        this.powerConsumption = powerData.powerConsumption;
        this.powerLastConsumption = powerData.powerLastConsumption;
        this.powerCapacity = powerData.powerCapacity;
        this.powerBuffer = powerData.powerBuffer;
        this.powerLastBuffer = powerData.powerLastBuffer;
        this.powerOutages = (NonNullList<ItemStack>) powerOutages;
        this.ticksPowerOutages = ticksPowerOutages;
        this.missingEssence = missingEssence;
        this.specialBatteries = (NonNullList<ItemStack>) specialBatteries;
        this.activeItems = (NonNullList<ItemStack>) activeItems;
        this.hudjackItems = (NonNullList<ItemStack>) hudjackItems;
        this.hotkeys = hotkeys;
        this.hudData = hudData;
        this.hasOpenedRadialMenu = hasOpenedRadialMenu;
        this.hudColor = hudColor;
        this.hudColorFloat = hudColorFloat;
        this.isImmune = isImmune;
    }
}