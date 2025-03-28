package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.contents.menu.SurgeryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CyberwareMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, Cyberware.MOD_ID);

    public static final Supplier<MenuType<SurgeryMenu>> SURGERY = REGISTER.register("surgery", () -> new MenuType<>(SurgeryMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
