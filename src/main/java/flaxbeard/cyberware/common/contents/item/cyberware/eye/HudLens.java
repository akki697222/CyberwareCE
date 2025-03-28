package flaxbeard.cyberware.common.contents.item.cyberware.eye;

import flaxbeard.cyberware.common.CyberwareItems;
import flaxbeard.cyberware.common.contents.item.cyberware.EyeCyberware;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HudLens extends EyeCyberware {
    public HudLens() {
        super(new Properties());
    }

    @Override
    public List<Component> getInfo() {
        return List.of();
    }

    @Override
    public int getEssenceCost() {
        return 1;
    }

    @Override
    public EnumCategory getCategory() {
        return EnumCategory.EYES;
    }
}
