package flaxbeard.cyberware.common.contents.item.cyberware;

import flaxbeard.cyberware.common.contents.item.CyberwareItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class Bodyparts extends CyberwareItem {
    private final EnumCategory category;

    public Bodyparts(BodyRegion bodyRegion, EnumCategory category) {
        super(new Properties(), bodyRegion);
        this.category = category;
    }

    @Override
    public List<String> getInfo() {
        return List.of();
    }

    @Override
    public int getEssenceCost() {
        return 0;
    }

    @Override
    public EnumCategory getCategory() {
        return category;
    }
}
