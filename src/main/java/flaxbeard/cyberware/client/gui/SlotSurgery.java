package flaxbeard.cyberware.client.gui;

import flaxbeard.cyberware.api.CyberwareAPI; // Assuming path
import flaxbeard.cyberware.api.item.ICyberware.EnumSlot; // Assuming path
import flaxbeard.cyberware.common.lib.LibConstants; // Assuming path

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotSurgery extends SlotItemHandler {

    public final int savedXPosition;
    public final int savedYPosition;
    public final EnumSlot slotType; // Renamed from 'slot' to avoid confusion with Slot class methods
    private final int indexInSlotType; // The 0-LibConstants.WARE_PER_SLOT index within this EnumSlot
    private final SurgeryMenu menu;
    private final IItemHandler playerFistuleCyberware; // The corresponding IItemHandler from SurgeryBlockEntity for player's equipped items

    public SlotSurgery(SurgeryMenu menu, IItemHandler itemHandlerSurgery, IItemHandler itemHandlerPlayer, int slotIndexGlobal, int xPosition, int yPosition, EnumSlot slotType, int indexInSlotType) {
        super(itemHandlerSurgery, slotIndexGlobal, xPosition, yPosition);
        this.menu = menu;
        this.savedXPosition = xPosition;
        this.savedYPosition = yPosition;
        this.slotType = slotType;
        this.indexInSlotType = indexInSlotType; // This is the index % LibConstants.WARE_PER_SLOT
        this.playerFistuleCyberware = itemHandlerPlayer;
    }

    public ItemStack getPlayerStack() {
        // slotNumber is the global index for the surgery slots.
        // We need to ensure this correctly maps to the player's cyberware inventory if their layout is different.
        // Assuming player's cyberware inventory (slotsPlayer in BE) has the same global indexing for now.
        if (playerFistuleCyberware != null && getSlotIndex() < playerFistuleCyberware.getSlots()) {
            return playerFistuleCyberware.getStackInSlot(getSlotIndex());
        }
        return ItemStack.EMPTY;
    }

    public boolean isSlotDiscarded() {
        if (menu.blockEntity == null || menu.discardSlots == null || getSlotIndex() >= menu.discardSlots.length) return false;
        return menu.discardSlots[getSlotIndex()];
    }

    public void setSlotDiscarded(boolean discarded) {
        if (menu.blockEntity != null && menu.discardSlots != null && getSlotIndex() < menu.discardSlots.length) {
            menu.discardSlots[getSlotIndex()] = discarded;
            // Notify BlockEntity about the change
            menu.blockEntity.setDiscardSlot(getSlotIndex(), discarded);
            menu.callUpdateEssentialOnBE(this.slotType);
            menu.callUpdateEssenceAndPowerOnBE();
        }
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        ItemStack playerStack = getPlayerStack();
        if (!playerStack.isEmpty() && !menu.canDisableItem(playerStack, slotType, indexInSlotType)) {
            return false;
        }

        if (!(CyberwareAPI.isCyberware(stack) && CyberwareAPI.getCyberware(stack).getSlot(stack) == slotType)) {
            return false;
        }

        if (CyberwareAPI.areCyberwareStacksEqual(stack, playerStack)) {
            int stackSize = CyberwareAPI.getCyberware(stack).installedStackSize(stack);
            if (playerStack.getCount() >= stackSize) return false; // Use >= to prevent overstacking through placement
        }

        return !menu.doesItemConflict(stack, slotType, indexInSlotType)
                && menu.areRequirementsFulfilled(stack, slotType, indexInSlotType);
    }

    @Override
    public void set(@Nonnull ItemStack stack) {
        // Called when an item is placed into the slot (e.g., by player click or shift-click)
        if (mayPlace(stack)) {
            // If we are replacing an existing player item (that wasn't discarded)
            // we need to ensure its dependants are correctly handled.
            ItemStack playerStack = getPlayerStack();
            if (!playerStack.isEmpty() && !isSlotDiscarded()) {
                 menu.disableDependants(playerStack, slotType, indexInSlotType);
            }
            super.set(stack); // Actually place the item
            // After placing, if the slot was previously marked for discard, it no longer is (implicitly)
            // However, the actual discard state is tied to the item *currently in the player*.
            // This slot now holds the *new* item being installed.
            // The discardSlots array in SurgeryBlockEntity should reflect the state of *player-installed* items.
        }
        // Always update BE regardless of mayPlace, as set can be called for empty stacks too.
        // This ensures that if an item is removed, the BE state is also updated.
        menu.callUpdateEssentialOnBE(this.slotType);
        menu.callUpdateEssenceAndPowerOnBE();
        if (menu.blockEntity != null) menu.blockEntity.setChanged(); // Mark BE dirty
    }

    @Override
    public void onQuickCraft(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack) {
        // Called when stack is changed by shift-click or other quick move actions
        super.onQuickCraft(oldStack, newStack);
        // Similar logic to set() might be needed if this path is used for installing/removing
        menu.callUpdateEssentialOnBE(this.slotType);
        menu.callUpdateEssenceAndPowerOnBE();
        if (menu.blockEntity != null) menu.blockEntity.setChanged();
    }


    @Override
    public boolean mayPickup(Player player) {
        // This refers to picking up from the *surgery* slot (the "to be installed" item)
        // The old canTakeStack referred to disabling an *installed* item.
        // For items in the surgery chamber slots, they can always be picked up if present.
        return true;
    }

    @Override
    public void onTake(Player player, @Nonnull ItemStack stack) {
        // Called when an item is taken from this surgery slot
        super.onTake(player, stack);
        menu.callUpdateEssentialOnBE(this.slotType);
        menu.callUpdateEssenceAndPowerOnBE();
        if (menu.blockEntity != null) menu.blockEntity.setChanged();
    }


    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !CyberwareAPI.isCyberware(stack)) {
            return super.getMaxStackSize(stack); // Usually 64 for empty, or default for non-cyberware
        }

        ItemStack playerStack = getPlayerStack();
        int installedStackSize = CyberwareAPI.getCyberware(stack).installedStackSize(stack);

        if (CyberwareAPI.areCyberwareStacksEqual(playerStack, stack)) {
            // If the item being stacked is the same as what's in the player,
            // allow stacking up to installedStackSize, minus what's already in player.
            // However, this slot *is* the surgery slot, not the player's equipped slot.
            // The item in this slot represents what *will be* installed or added.
            // So, it can hold up to `installedStackSize`. If it's the same as player's,
            // it means we are adding more to an existing stack.
            // The actual combined count is handled during installation.
            return installedStackSize;
        }
        // If it's a different item, it can also be stacked up to its own installedStackSize.
        return installedStackSize;
    }
}
