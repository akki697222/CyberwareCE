package flaxbeard.cyberware.client.screen.surgery;

import flaxbeard.cyberware.common.contents.menu.SurgeryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SurgeryScreen extends AbstractContainerScreen<SurgeryMenu> {
    public static final ResourceLocation SURGERY_GUI_TEXTURES = ResourceLocation.fromNamespaceAndPath("cyberware", "textures/gui/surgery.png");
    public static final ResourceLocation GRAY_TEXTURES = ResourceLocation.fromNamespaceAndPath("cyberware", "textures/gui/greypx.png");
    public static final ResourceLocation BLUE_TEXTURES = ResourceLocation.fromNamespaceAndPath("cyberware", "textures/gui/bluepx.png");

    private Entity skeleton;
    private ModelPart part;



    public record Coordinates2D(int x, int y) {}

    public SurgeryScreen(SurgeryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float v, int i, int i1) {

    }
}
