package flaxbeard.cyberware.mixin;

import flaxbeard.cyberware.common.CyberwareMenuTypes;
import flaxbeard.cyberware.common.contents.menu.PlayerWareSlot;
import flaxbeard.cyberware.common.contents.menu.SurgeryMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Shadow public abstract MenuType<?> getType();

    @Inject(method = "doClick", at = @At("HEAD"))
    private void onDoClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        if (menu instanceof SurgeryMenu surgeryMenu) {
            Slot slot = slotId >= 0 && slotId < menu.slots.size() ? menu.getSlot(slotId) : null;
            if (slot != null) {
                if (slot instanceof PlayerWareSlot && clickType == ClickType.PICKUP) {
                    surgeryMenu.onClickPlayerSlot(slot.getContainerSlot());
                }
            }
        }
    }
}
