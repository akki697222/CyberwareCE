package flaxbeard.cyberware.common.contents.menu;

import flaxbeard.cyberware.Cyberware;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerWareSlot extends Slot {
    public PlayerWareSlot(Container playerContainer, int slot, int x, int y) {
        super(playerContainer, slot, x, y);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return false;
    }
}
