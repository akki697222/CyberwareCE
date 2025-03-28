package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CyberwareCreativeTabs {
    public static DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Cyberware.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CYBERWARE_MAIN = REGISTER.register(Cyberware.MOD_ID, () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("gui." + Cyberware.MOD_ID + ".tab"))
                    .icon(() -> new ItemStack(CyberwareItems.WARE_CYBER_EYES.get()))
                    .displayItems((p, o) -> {
                        CyberwareItems.itemList.forEach(i -> {
                            o.accept(i.get().getDefaultInstance());
                        });
                    }).build());
}
