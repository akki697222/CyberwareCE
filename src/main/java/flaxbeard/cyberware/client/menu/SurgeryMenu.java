package flaxbeard.cyberware.client.menu;

import flaxbeard.cyberware.api.CyberwareConstants;
import flaxbeard.cyberware.common.CyberwareMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SurgeryMenu extends AbstractContainerMenu {
    private final Inventory playerInventory;
    private final Container surgeryContainer;
    private final ContainerData containerData;
    private SurgeryPage currentPage = SurgeryPage.MAIN;

    public SurgeryMenu(int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(CyberwareMenuTypes.SURGERY.get(), containerId);

        this.playerInventory = playerInventory;
        this.surgeryContainer = container;
        this.containerData = containerData;

        addPlayerSlots();
    }

    public SurgeryMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CyberwareConstants.WARE_SLOT_NUM), new SimpleContainerData(0));
    }

    private void addPlayerSlots() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public Inventory getPlayerInventory() {
        return playerInventory;
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    public Container getSurgeryContainer() {
        return surgeryContainer;
    }

    public SurgeryPage getCurrentPage() {
        return currentPage;
    }

    public enum SurgeryPage {
        MAIN,
        EYES,
        BRAIN,
        HEART,
        LUNGS,
        STOMACH,
        SKIN,
        MUSCLE,
        BONE,
        LEFT_ARM,
        RIGHT_ARM,
        LEFT_HAND,
        RIGHT_HAND,
        LEFT_LEG,
        RIGHT_LEG,
        LEFT_FOOT,
        RIGHT_FOOT
    }
}
