package flaxbeard.cyberware.client.screen.surgery;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.client.menu.SurgeryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SurgeryScreen extends AbstractContainerScreen<SurgeryMenu> {
    public static final ResourceLocation SURGERY_GUI_TEXTURES = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/surgery.png");

    public SurgeryScreen(SurgeryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float v, int i, int i1) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(SURGERY_GUI_TEXTURES, x, y, 0, 0, imageWidth, imageHeight);
    }
}
