package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.item.ICyberware.BodyRegion;
import flaxbeard.cyberware.api.item.ICyberwareTabItem.EnumCategory;
import flaxbeard.cyberware.common.contents.item.CyberwareComponent;
import flaxbeard.cyberware.common.contents.item.CyberwareItem;
import flaxbeard.cyberware.common.contents.item.cyberware.Bodyparts;
import flaxbeard.cyberware.common.contents.item.cyberware.eye.HudLens;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CyberwareItems {
    public static final List<DeferredHolder<Item, ? extends Item>> itemList = new ArrayList<>();
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(Cyberware.MOD_ID);

    /*
        Components
     */
    public static final DeferredHolder<Item, CyberwareComponent> COMPONENT_ACTUATOR = registerComponent("actuator");
    public static final DeferredHolder<Item, CyberwareComponent> COMPONENT_REACTOR = registerComponent("reactor");
    public static final DeferredHolder<Item, CyberwareComponent> COMPONENT_TITANIUM = registerComponent("titanium");
    public static final DeferredHolder<Item, CyberwareComponent> COMPONENT_SSC = registerComponent("ssc");
    public static final DeferredHolder<Item, CyberwareComponent> COMPONENT_PLATING = registerComponent("plating");
    public static final DeferredHolder<Item, CyberwareComponent> COMPONENT_FIBEROPTICS = registerComponent("fiberoptics");
    public static final DeferredHolder<Item, CyberwareComponent> COMPONENT_FULLERENE = registerComponent("fullerene");
    public static final DeferredHolder<Item, CyberwareComponent> COMPONENT_SYNTHNERVES = registerComponent("synthnerves");
    public static final DeferredHolder<Item, CyberwareComponent> COMPONENT_STORAGE = registerComponent("storage");
    public static final DeferredHolder<Item, CyberwareComponent> COMPONENT_MICROELECTRIC = registerComponent("microelectric");
    /*
        Eye Cyberwares
     */
    public static final DeferredHolder<Item, HudLens> WARE_HUD_LENS  = REGISTER.register("hud_lens", HudLens::new);

    /*
        Bodyparts
     */
    public static final DeferredHolder<Item, Bodyparts> EYES = registerBodyparts("eyes", BodyRegion.EYES, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> BRAIN = registerBodyparts("brain", BodyRegion.CRANIUM, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> HEART = registerBodyparts("heart", BodyRegion.HEART, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> LUNGS = registerBodyparts("lungs", BodyRegion.LUNGS, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> STOMACH = registerBodyparts("stomach", BodyRegion.LOWER_ORGANS, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> SKIN = registerBodyparts("skin", BodyRegion.SKIN, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> MUSCLE = registerBodyparts("muscle", BodyRegion.MUSCLE, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> BONE = registerBodyparts("bone", BodyRegion.BONE, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> LEFT_ARM = registerBodyparts("arm_left", BodyRegion.ARM, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> RIGHT_ARM = registerBodyparts("arm_right", BodyRegion.ARM, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> LEFT_LEG = registerBodyparts("leg_left", BodyRegion.LEG, EnumCategory.BODYPARTS);
    public static final DeferredHolder<Item, Bodyparts> RIGHT_LEG = registerBodyparts("leg_right", BodyRegion.LEG, EnumCategory.BODYPARTS);

    public static DeferredHolder<Item, CyberwareComponent> registerComponent(String name) {
        DeferredHolder<Item, CyberwareComponent> i = REGISTER.register("component_" + name, CyberwareComponent::new);
        itemList.add(i);
        return i;
    }

    public static DeferredHolder<Item, Bodyparts> registerBodyparts(String name, BodyRegion bodyRegion, EnumCategory category) {
        DeferredHolder<Item, Bodyparts> i = REGISTER.register("bodypart_" + name, () -> new Bodyparts(bodyRegion, category));
        itemList.add(i);
        return i;
    }

    public static <T extends Item> DeferredHolder<Item, T> registerItem(String name, Supplier<T> item) {
        DeferredHolder<Item, T> i = REGISTER.register(name, item);
        itemList.add(i);
        return i;
    }

    public static DeferredHolder<Item, BlockItem> registerBlockItem(DeferredHolder<Block, ? extends Block> blockHolder) {
        DeferredHolder<Item, BlockItem> i = REGISTER.registerSimpleBlockItem(blockHolder);
        itemList.add(i);
        return i;
    }
}
