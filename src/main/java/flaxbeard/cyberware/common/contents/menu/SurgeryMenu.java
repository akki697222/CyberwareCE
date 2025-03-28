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
    private final Inventory playerInventory;
    private final Container surgeryContainer;
    private final Container playerContainer;
    private final ContainerData containerData;
    private SurgeryPage currentPage;

    public SurgeryMenu(int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(CyberwareMenuTypes.SURGERY.get(), containerId);

        Cyberware.logger.debug("Menu Created!");

        this.playerInventory = playerInventory;
        this.surgeryContainer = container;
        this.playerContainer = new SimpleContainer(CyberwareConstants.WARE_SLOT_NUM);
        this.containerData = containerData;

        Player player = playerInventory.player;

        if (!player.level().isClientSide()) {
            CyberwareAPI.updateData(player);
        }

        syncPlayerContainer(player);

        for (SurgeryPage page : SurgeryPage.values()) {
            if (page != SurgeryPage.MAIN && page != SurgeryPage.INDEX) {
                int offset = 0;
                for (int col = 0; col < 8; col++) {
                    int slotIndex = col + (page.getOffset() * 10);
                    addSlot(new FilteredSlot(surgeryContainer,
                            slotIndex,
                            9 + (col * 18 + offset),
                            109,
                            CyberwareItem.class) {
                        @Override
                        public boolean isActive() {
                            return containerData.get(1) == page.getId();
                        }
                    });
                    addSlot(new PlayerWareSlot(
                            playerContainer,
                            slotIndex,
                            9 + (col * 18 + offset),
                            83) {
                        @Override
                        public boolean isActive() {
                            return containerData.get(1) == page.getId();
                        }
                    });
                    offset += 2;
                }
            }
        }

        addPlayerSlots();

        changePage(SurgeryPage.MAIN);

        addDataSlots(containerData);
    }

    public void onClickPlayerSlot(int slot) {
        containerData.set(2, slot);
        if (playerContainer.getItem(slot).isEmpty()) {
            NonNullList<ItemStack> wares = getPlayerWares(playerInventory.player);

            ItemStack stack = wares.get(slot);

            if (!stack.isEmpty()) {
                playerContainer.setItem(slot, stack);
            }
        } else {
            playerContainer.setItem(slot, ItemStack.EMPTY);
        }
        broadcastChanges();
    }

    private void syncPlayerContainer(Player player) {
        int index = 0;
        NonNullList<ItemStack> wares = getPlayerWares(player);
        for (ItemStack playerWare : wares) {
            playerContainer.setItem(index, playerWare);
            index++;
        }

        System.out.println(wares);
    }

    private NonNullList<ItemStack> getPlayerWares(Player player) {
        CyberwareUserData data = player.getData(CyberwareAttachments.CYBERWARE_USER_DATA);
        NonNullList<ItemStack> wares = NonNullList.withSize(CyberwareConstants.WARE_SLOT_NUM, ItemStack.EMPTY);
        int index = 0;
        for (ICyberware.BodyRegion bodyRegion : ICyberware.BodyRegion.values()) {
            NonNullList<ItemStack> installedWares = data.getInstalledCyberware(bodyRegion);
            for (ItemStack installedWare : installedWares) {
                wares.set(index, installedWare);
                index++;
            }
        }
        return wares;
    }

    public SurgeryMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CyberwareConstants.WARE_SLOT_NUM), new SimpleContainerData(3));
    }

    private void addPlayerSlots() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }
    }

    public void changePage(SurgeryPage page) {
        this.containerData.set(1, page.getId());
        this.currentPage = page;
        broadcastChanges();
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

    public int getCurrentPageById() {
        return currentPage.getId();
    }

    public Container getPlayerContainer() {
        return playerContainer;
    }

    public enum SurgeryPage {
        MAIN(0, 0),
        INDEX(1, 0),
        EYES(2, 0),
        BRAIN(3, 1),
        HEART(4, 2),
        LUNGS(5, 3),
        STOMACH(6, 4),
        SKIN(7, 5),
        MUSCLE(8, 6),
        BONE(9, 7),
        ARM(10, 8),
        HAND(11, 9),
        LEG(12, 10),
        FOOT(13, 11);

        final int id;
        final int offset;

        SurgeryPage(int id, int offset) {
            this.id = id;
            this.offset = offset;
        }

        public int getId() {
            return id;
        }

        public int getOffset() {
            return offset;
        }

        @Nonnull
        public static SurgeryPage byId(int id) {
            for (SurgeryPage page : SurgeryPage.values()) {
                if (page.getId() == id) {
                    return page;
                }
            }
            throw new IndexOutOfBoundsException(id + " out of 13");
        }
    }
}
