package flaxbeard.cyberware;

import com.mojang.logging.LogUtils;
import flaxbeard.cyberware.api.CyberwareUserData;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.*;
import flaxbeard.cyberware.common.network.CyberwareSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
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
        CyberwareBlocks.REGISTER.register(modEventBus);
        CyberwareBlockEntities.REGISTER.register(modEventBus);
        CyberwareMenuTypes.REGISTER.register(modEventBus);
        CyberwareAttachments.REGISTER.register(modEventBus);
        CyberwareCreativeTabs.REGISTER.register(modEventBus);
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    private static final class GameListener {
        @SubscribeEvent
        public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            Player player = event.getEntity();
            ICyberwareUserData data = player.getData(CyberwareAttachments.CYBERWARE_USER_DATA);

            logger.debug(data.getInstalledCyberware(ICyberware.BodyRegion.EYES).toString());
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            ICyberwareUserData data = player.getData(CyberwareAttachments.CYBERWARE_USER_DATA);

            logger.debug(data.getInstalledCyberware(ICyberware.BodyRegion.EYES).toString());
        }
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    private static final class Listener {
        @SubscribeEvent
        public static void registerNetwork(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar(MOD_ID);
            registrar.playToClient(
                    CyberwareSyncPacket.TYPE,
                    CyberwareSyncPacket.CODEC,
                    CyberwareSyncPacket::handle
            );
        }
    }
}
