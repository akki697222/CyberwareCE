package flaxbeard.cyberware.common.contents.item;

import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.ICyberwareTabItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class CyberwareItem extends AbstractCyberwareItem implements ICyberware, ICyberwareTabItem {
    private final BodyRegion bodyRegion;
    private NonNullList<NonNullList<ItemStack>> craftComponents;

    public CyberwareItem(Properties properties, BodyRegion bodyRegion) {
        super(properties);
        this.bodyRegion = bodyRegion;
        this.craftComponents = NonNullList.create();
    }

    @Override
    public int getMaxInstalls() {
        return 1;
    }

    @Override
    public BodyRegion getBodyRegion() {
        return bodyRegion;
    }

    @Override
    public NonNullList<ItemStack> requiredCyberwares() {
        return NonNullList.create();
    }

    @Override
    public boolean isIncompatible(ItemStack comparison) {
        return false;
    }

    @Override
    public boolean isEssential() {
        return false;
    }

    @Override
    public abstract List<String> getInfo();

    @Override
    public int getEnergyCapacity() {
        return 0;
    }

    @Override
    public boolean canHoldQuality(Quality quality) {
        return true;
    }

    @Override
    public void onAdded(LivingEntity livingEntity) {

    }

    @Override
    public void onRemoved(LivingEntity livingEntity) {

    }

    public int getEnergyConsumption() {
        return 0;
    }

    public int getEnergyProduction() {
        return 0;
    }

    @Override
    public abstract int getEssenceCost();

    @Override
    public abstract EnumCategory getCategory();

    public NonNullList<NonNullList<ItemStack>> getCraftComponents() {
        return craftComponents;
    }

    public void setCraftComponents(NonNullList<NonNullList<ItemStack>> craftComponents) {
        this.craftComponents = craftComponents;
    }
}
