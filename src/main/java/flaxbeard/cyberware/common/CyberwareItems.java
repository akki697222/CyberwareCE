package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.contents.item.CyberwareComponent;
import flaxbeard.cyberware.common.contents.item.CyberwareItem;
import flaxbeard.cyberware.common.contents.item.cyberware.eye.HudLens;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CyberwareItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.createItems(Cyberware.MOD_ID);

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

    public static DeferredHolder<Item, CyberwareComponent> registerComponent(String name) {
        return REGISTER.register("component_" + name, CyberwareComponent::new);
    }
}
