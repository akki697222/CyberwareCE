package flaxbeard.cyberware.common.contents.item.cyberware.eye;

import flaxbeard.cyberware.api.item.CyberwareData;
import flaxbeard.cyberware.common.CyberwareAttributes;
import flaxbeard.cyberware.common.CyberwareComponents;
import flaxbeard.cyberware.common.contents.item.cyberware.EyeCyberware;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

public class Cybereye extends EyeCyberware {
    public Cybereye() {
        super(new Properties().component(CyberwareComponents.CYBERWARE_DATA, new CyberwareData()));
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
        return EnumCategory.EYES;
    }
}
