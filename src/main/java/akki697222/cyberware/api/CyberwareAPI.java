package akki697222.cyberware.api;

import akki697222.cyberware.common.items.CyberwareItem;
import net.minecraft.world.item.ItemStack;

public final class CyberwareAPI {
    public static boolean isItemStackCyberware(ItemStack stack) {
        return stack.getItem() instanceof CyberwareItem;
    }

    public static CyberwareItem getCyberwareItemFromStack(ItemStack stack) {
        if (stack.getItem() instanceof CyberwareItem cyberwareItem) {
            return cyberwareItem;
        } else {
            return null;
        }
    }

    public static CyberwareItem getCyberwareItemFromStackOrThrow(ItemStack stack) {
        if (stack.getItem() instanceof CyberwareItem cyberwareItem) {
            return cyberwareItem;
        } else {
            throw new IllegalArgumentException(stack + " is not cyberware");
        }
    }
}
