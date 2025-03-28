package flaxbeard.cyberware.client.screen.surgery;

import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.client.screen.surgery.component.IndexButton;
import flaxbeard.cyberware.client.screen.surgery.component.ReturnButton;
import flaxbeard.cyberware.common.contents.item.CyberwareItem;
import flaxbeard.cyberware.common.contents.menu.FilteredSlot;
import flaxbeard.cyberware.common.contents.menu.SurgeryMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SurgeryScreen extends AbstractContainerScreen<SurgeryMenu> {
    public static final ResourceLocation SURGERY_GUI_TEXTURES;
    public static final ResourceLocation SLOT_BLUE;
    public static final ResourceLocation SLOT_RED;
    public static final ResourceLocation SLOT_BLUE_BG;
    public static final ResourceLocation SLOT_RED_BG;
    public static final ResourceLocation ARROW;
    public static final ResourceLocation TOLERANCE_BAR_FILL;
    public static final ResourceLocation TOLERANCE_BAR_BG;

    public static final boolean DEBUG = true;

    static {
        SURGERY_GUI_TEXTURES = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/surgery.png");
        SLOT_BLUE = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/element/slot_blue.png");
        SLOT_RED = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/element/slot_red.png");
        SLOT_BLUE_BG = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/element/slot_blue_bg.png");
        SLOT_RED_BG = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/element/slot_red_bg.png");
        ARROW = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/element/arrow.png");
        TOLERANCE_BAR_FILL = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/element/tolerance_bar.png");
        TOLERANCE_BAR_BG = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/element/tolerance_bar_bg.png");
    }

    public static final int GUI_TEXT_COLOR = 0x1DA9C1;

    private boolean isPageTransitioning;
    private IndexButton indexButton;
    private ReturnButton returnButton;

    public record Coordinates2D(int x, int y) {}

    public SurgeryScreen(SurgeryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        this.indexButton = new IndexButton(
                leftPos + imageWidth - 22,
                topPos + 5,
                () -> {
                    if (menu.getCurrentPage() == SurgeryMenu.SurgeryPage.MAIN) {
                        menu.changePage(SurgeryMenu.SurgeryPage.INDEX);
                    } else if (menu.getCurrentPage() == SurgeryMenu.SurgeryPage.INDEX) {
                        menu.changePage(SurgeryMenu.SurgeryPage.MAIN);
                    }
                }
        );
        this.returnButton = new ReturnButton(
                leftPos + imageWidth - 25,
                topPos + 5,
                () -> {
                    menu.changePage(SurgeryMenu.SurgeryPage.MAIN);
                }
        );
        this.addRenderableWidget(indexButton);
        this.addRenderableWidget(returnButton);
        // Index change button for debug
        if (!DEBUG) return;
        this.addRenderableWidget(Button.builder(
                Component.literal("+"),
                (button) -> {
                    menu.changePage(SurgeryMenu.SurgeryPage.byId(Math.min(13, menu.getCurrentPageById() + 1)));
                })
                .pos(0, 0)
                .size(16, 16)
                .build());
        this.addRenderableWidget(Button.builder(
                        Component.literal("-"),
                        (button) -> {
                            menu.changePage(SurgeryMenu.SurgeryPage.byId(Math.max(0, menu.getCurrentPageById() - 1)));
                        })
                .pos(18, 0)
                .size(16, 16)
                .build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        registerDefaultTextures();

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        indexButton.active = !isPageTransitioning && menu.getCurrentPageById() == 0;
        returnButton.active = !isPageTransitioning && menu.getCurrentPageById() != 0;

        if (menu.getCurrentPage() == SurgeryMenu.SurgeryPage.MAIN) {
            if (isPageTransitioning) return;

            /*
                Draw player name to gui.
             */

            String name = "_" + player.getName().getString().toUpperCase();
            int textWidth = font.width(name);
            int xPos = leftPos + (imageWidth - textWidth) / 2;
            int yPos = topPos + 115;

            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.75F);
            guiGraphics.drawString(font, name, xPos, yPos, GUI_TEXT_COLOR, false);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawDefaultGuiElements(guiGraphics);
        } else if (menu.getCurrentPage() == SurgeryMenu.SurgeryPage.INDEX) {
            /*
                Drawing index page's slots(8x5)
             */
            drawIndexGuiElements(guiGraphics);
        } else {
            /*
                If page is not main or indexes, drawing default gui elements.
             */
            drawDefaultGuiElements(guiGraphics);
        }

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public void drawIndexGuiElements(@NotNull GuiGraphics guiGraphics) {
        int offsetY = 0;
        for (int row = 0; row < 5; row++) {
            int offsetX = 0;
            for (int col = 0; col < 8; col++) {
                drawSlot(guiGraphics,
                        new Coordinates2D(
                                leftPos + (8 + col * 18 + offsetX),
                                topPos + (23 + row * 18 + offsetY)),
                        false);
                offsetX += 2;
            }
            offsetY += 2;
        }
        guiGraphics.drawString(font,
                Component.translatable("gui." + Cyberware.MOD_ID + ".surgery.label.after_install"),
                leftPos + 8,
                topPos + 9,
                0x1DA9C1,
                false);
    }

    public void drawDefaultGuiElements(@NotNull GuiGraphics guiGraphics) {
        if (menu.getCurrentPageById() != 0) {
            int offset = 0;
            for (int col = 0; col < 8; col++) {
                drawSlot(guiGraphics,
                        new Coordinates2D(leftPos + (8 + (col * 18 + offset)), topPos + 108),
                        false);
                drawSlot(guiGraphics,
                        new Coordinates2D(leftPos + (8 + (col * 18 + offset)), topPos + 82),
                        true);
                drawArrow(guiGraphics,
                        new Coordinates2D(leftPos + (8 + (col * 18 + offset)), topPos + 100));
                offset += 2;
            }
        }
        if (menu.getCurrentPageById() != 1) {
            int essence = menu.getContainerData().get(0);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.75F);
            guiGraphics.drawString(font,
                    essence + " / 100",
                    leftPos + 18,
                    topPos + 6,
                    0x1DA9C1,
                    false
            );

            float essencePercentage = essence / 100.0F;
            int gaugeHeight = (int)(essencePercentage * 49);

            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            int x = leftPos + 5;
            int y = topPos + 5;
            guiGraphics.blit(
                    TOLERANCE_BAR_FILL,
                    x, y + (49 - gaugeHeight),
                    0, 49 - gaugeHeight,
                    9, gaugeHeight,
                    9, 49
            );
            RenderSystem.disableBlend();
        }
    }

    private void registerDefaultTextures() {
        TextureManager tm = Minecraft.getInstance().getTextureManager();
        tm.bindForSetup(SURGERY_GUI_TEXTURES);
        tm.bindForSetup(SLOT_BLUE);
        tm.bindForSetup(SLOT_RED);
        tm.bindForSetup(SLOT_BLUE_BG);
        tm.bindForSetup(SLOT_RED_BG);
        tm.bindForSetup(ARROW);
        tm.bindForSetup(TOLERANCE_BAR_FILL);
        tm.bindForSetup(TOLERANCE_BAR_BG);
    }

    public void drawArrow(@NotNull GuiGraphics guiGraphics, Coordinates2D coordinate) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.5F);
        guiGraphics.blit(
                ARROW,
                coordinate.x(),
                coordinate.y(),
                0,
                0,
                18,
                7,
                18,
                7
        );
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    public void drawSlot(@NotNull GuiGraphics guiGraphics, Coordinates2D coordinate, boolean isSlotRed) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.5F);
        guiGraphics.blit(
                isSlotRed ? SLOT_RED : SLOT_BLUE,
                coordinate.x(),
                coordinate.y(),
                0,
                0,
                18,
                18,
                18,
                18
        );
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.2F);
        guiGraphics.blit(
                isSlotRed ? SLOT_RED_BG : SLOT_BLUE_BG,
                coordinate.x(),
                coordinate.y(),
                0,
                0,
                18,
                18,
                18,
                18
        );
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float v, int i, int i1) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(SURGERY_GUI_TEXTURES, x, y, 0, 0, imageWidth, imageHeight);
    }
}
