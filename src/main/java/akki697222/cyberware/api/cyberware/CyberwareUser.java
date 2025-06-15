package akki697222.cyberware.api.cyberware;

import akki697222.cyberware.common.items.CyberwareItem;
import net.minecraft.world.item.ItemStack;

public interface CyberwareUser {
    boolean installCyberware(ItemStack itemStack);
    boolean canInstallCyberware(ItemStack itemStack);
    int getPlayerEssence();
    int getPlayerMaxEssence();
}
