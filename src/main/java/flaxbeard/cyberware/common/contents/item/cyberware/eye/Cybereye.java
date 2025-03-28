package flaxbeard.cyberware.common.contents.item.cyberware.eye;

import flaxbeard.cyberware.api.CyberwareConstants;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.contents.item.cyberware.EyeCyberware;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;

public class Cybereye extends EyeCyberware {
    public Cybereye() {
        super(new Properties());
    }

    @Override
    public boolean isIncompatible(ItemStack comparison) {
        return comparison.getItem() instanceof HudLens;
    }

    @Override
    public boolean isEssential() {
        return true;
    }

    @Override
    public List<Component> getInfo() {
        return List.of(Component.translatable("tooltip.cyberware.desc.cyber_eyes"));
    }

    @Override
    public int getEssenceCost() {
        return 8;
    }

    @Override
    public int getEnergyConsumption() {
        return CyberwareConstants.CYBEREYES_CONSUMPTION;
    }

    @Override
    public EnumCategory getCategory() {
        return EnumCategory.EYES;
    }
}
