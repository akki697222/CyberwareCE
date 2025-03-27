package flaxbeard.cyberware.client;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.client.screen.surgery.SurgeryScreen;
import flaxbeard.cyberware.common.CyberwareMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Cyberware.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class CyberwareClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(CyberwareMenuTypes.SURGERY.get(), SurgeryScreen::new);
    }
}
