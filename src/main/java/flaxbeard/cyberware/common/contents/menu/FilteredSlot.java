package flaxbeard.cyberware.common.contents.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FilteredSlot extends Slot {
    protected Class<? extends Item> filterItem;
    public FilteredSlot(Container container, int slot, int x, int y, Class<? extends Item> filterItem) {
        super(container, slot, x, y);

        this.filterItem = filterItem;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return filterItem.isInstance(stack.getItem());
    }
}
