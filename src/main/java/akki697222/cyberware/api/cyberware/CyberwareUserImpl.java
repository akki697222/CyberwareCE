package akki697222.cyberware.api.cyberware;

import net.minecraft.world.item.ItemStack;

public class CyberwareUserImpl implements CyberwareUser {
    @Override
    public boolean installCyberware(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canInstallCyberware(ItemStack itemStack) {
        return false;
    }

    @Override
    public int getPlayerEssence() {
        return 0;
    }

    @Override
    public int getPlayerMaxEssence() {
        return 0;
    }
}
