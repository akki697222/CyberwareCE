package flaxbeard.cyberware;

import com.mojang.logging.LogUtils;
import flaxbeard.cyberware.common.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Cyberware.MOD_ID)
public class Cyberware {
    public static final String MOD_ID = "cyberware";
    public static final Logger logger = LogUtils.getLogger();

    public Cyberware(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, CyberwareConfig.SPEC);
        CyberwareAttributes.REGISTER.register(modEventBus);
        CyberwareComponents.REGISTER.register(modEventBus);
        CyberwareItems.REGISTER.register(modEventBus);
    }
}
