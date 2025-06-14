package flaxbeard.cyberware.common.contents.menu;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareConstants;
import flaxbeard.cyberware.api.CyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.CyberwareAttachments;
import flaxbeard.cyberware.common.CyberwareMenuTypes;
import flaxbeard.cyberware.common.contents.item.CyberwareItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class SurgeryMenu extends AbstractContainerMenu {
    public SurgeryMenu(int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(CyberwareMenuTypes.SURGERY.get(), containerId);


    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
