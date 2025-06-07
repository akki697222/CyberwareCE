package flaxbeard.cyberware.client.gui;

import flaxbeard.cyberware.api.item.ICyberware.EnumSlot; // For later use with slots
import flaxbeard.cyberware.client.gui.SurgeryMenu; // Ensure this is correct
// import flaxbeard.cyberware.common.CyberwareConfig; // For CRITICAL_ESSENCE later
import flaxbeard.cyberware.common.lib.LibConstants; // For WARE_PER_SLOT later

import com.mojang.blaze3d.systems.RenderSystem; // For RenderSystem calls
import com.mojang.blaze3d.vertex.PoseStack; // For GuiGraphics matrix stack

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth; // For MathHelper/Mth

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.ChatFormatting; // For TextFormatting

// For later, when porting item rendering and tooltips
// import net.minecraft.client.renderer.entity.ItemRenderer;
// import net.minecraft.world.item.TooltipFlag;


public class SurgeryScreen extends AbstractContainerScreen<SurgeryMenu> {

    private static final ResourceLocation SURGERY_GUI_TEXTURES = new ResourceLocation("cyberware", "textures/gui/surgery.png");
    // private static final ResourceLocation GREY_TEXTURE = new ResourceLocation("cyberware", "textures/gui/greypx.png"); // Define if used
    // private static final ResourceLocation BLUE_TEXTURE = new ResourceLocation("cyberware", "textures/gui/bluepx.png"); // Define if used

    // Fields from GuiSurgery
    private float partialTicksStore; // Store partialTicks from render method

    // Page and animation related fields
    private PageConfiguration[] configs = new PageConfiguration[25];
    private PageConfiguration currentConfig;
    private PageConfiguration targetConfig;
    private PageConfiguration easeConfig;

    private List<SlotSurgery> visibleSlots = new ArrayList<>(); // Will be populated later

    private int currentPage = 0; // 0 for main, 9 for index, others for body parts
    private int parentPage = 0;  // To store the previous page when going to a sub-page

    private float transitionStartTime = 0;
    private float operationTime = 0;
    private float amountDone = 1;
    private float openTime = 0; // For initial opening animation

    // Rotation and mouse drag fields
    private float modelRotation = 0; // Current visual rotation
    private float targetRotation = 0; // Target rotation after drag/animation
    private float addedRotate = 0; // For continuous rotation effect
    private float oldRotate = 0; // Rotation at mouse down
    private boolean isDragging = false;
    private int dragStartX;
    private float[] lastDragX = new float[5];
    private float rotationVelocity = 0;
    private float lastRenderTicks = 0;

    // Buttons (placeholders, will be proper widgets)
    // private Button backButton;
    // private Button indexButton;
    // private Button[] bodyPartButtons = new Button[7];
    // ... other button arrays

    public SurgeryScreen(SurgeryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;

        // Initialize PageConfigurations (same as GuiSurgery)
        configs[0] = new PageConfiguration(0, 0, 0, 50, 35, 35, -50, 10);
        configs[1] = new PageConfiguration(50, 0, 210, 150, 0, 0, -150, 0); // Head
        configs[2] = new PageConfiguration(15, 0, 100, 130, 0, 0, -150, 0); // Torso
        configs[3] = new PageConfiguration(-50, 0, 100, 130, 0, 0, -150, 0); // Left Arm
        configs[4] = new PageConfiguration(50, 0, 100, 130, 0, 0, -150, 0);  // Right Arm (placeholder, might be same as left)
        configs[5] = new PageConfiguration(-70, 0, 10, 130, 0, 0, -150, 0);  // Left Leg
        configs[6] = new PageConfiguration(70, 0, 10, 130, 0, 0, -150, 0);   // Right Leg (placeholder)
        configs[7] = new PageConfiguration(0, 0, 0, 50, 170, 125, 0, 0);   // Cross-section Box

        configs[11] = new PageConfiguration(160, 0, 300, 200); // Head - Eye
        configs[12] = new PageConfiguration(5, 0, 330, 220);   // Head - Cranium L
        configs[13] = new PageConfiguration(5, 0, 330, 220);   // Head - Cranium R
        configs[14] = new PageConfiguration(-20, 0, 220, 210); // Torso - Heart
        configs[15] = new PageConfiguration(0, 0, 180, 180);   // Torso - Lungs L
        configs[16] = new PageConfiguration(0, 0, 180, 180);   // Torso - Lungs R
        configs[17] = new PageConfiguration(0, 0, 125, 180);   // Torso - Lower Organs
        configs[18] = new PageConfiguration(0, 0, 0, 50, 190, 180, 0, 0); // Cross-section Skin
        configs[19] = new PageConfiguration(0, 0, 0, 50, 170, 180, 0, 0); // Cross-section Muscle
        configs[20] = new PageConfiguration(0, 0, 0, 50, 170, 180, 0, 0); // Cross-section Bone

        configs[21] = new PageConfiguration(-70, 0, 180, 200); // Arm - Cyberlimb
        configs[22] = new PageConfiguration(-70, 0, 120, 220); // Arm - Hand

        configs[23] = new PageConfiguration(10, 0, 20, 200);   // Leg - Cyberlimb
        configs[24] = new PageConfiguration(10, 0, -30, 220);  // Leg - Foot

        currentConfig = easeConfig = targetConfig = configs[0].copy();
        this.modelRotation = currentConfig.rotation; // Initialize modelRotation
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.menu.updateClientSyncedValues(); // Get latest values from BE via menu
        // Button initialization will go here later
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.partialTicksStore = partialTicks;
        //this.renderBackground(guiGraphics); // AbstractContainerScreen calls this already
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY); // Render tooltips for slots
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //RenderSystem.setShaderTexture(0, SURGERY_GUI_TEXTURES);

        guiGraphics.blit(SURGERY_GUI_TEXTURES, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        // Logic from old drawGuiContainerBackgroundLayer - animations, model rendering etc. will go here
        // For now, just the background.
        // The complex part involving GL11.glScissorTest, entity rendering, model rendering,
        // page transitions, and animations will be ported iteratively.

        // Update animation states (simplified from original)
        float gameTime = ticksExisted() + partialTicks;
        lastRenderTicks = gameTime; // Store for other calculations if needed

        // TODO: Port the rest of drawGuiContainerBackgroundLayer, including:
        // - Animation logic (openTime, transitionStartTime, easeConfig updates)
        // - Mouse drag rotation logic (isDragging, rotationVelocity)
        // - Scissor testing (guiGraphics.enableScissor, guiGraphics.disableScissor)
        // - Rendering of entities (player, skeleton) and custom models (ModelBox)
        // - Rendering of dynamic elements like scan lines, boxes, connectors
        // - Updating and drawing location buttons
        // - Calling drawSlots()
    }

    protected void drawSlots(GuiGraphics guiGraphics) {
        // Ported from GuiSurgery#drawSlots
        // This will render custom slot borders, essence bar, etc.
        // Needs menu.essence, menu.maxEssence, menu.isEssentialMissing, menu.missingPower, menu.wrongSlot
        // menu.criticalEssence (from config or LibConstants)

        // Example for essence bar (simplified)
        int essenceDisplay = (int) (((float)menu.essence / menu.maxEssence) * 49);
        // Placeholder for critical essence, assuming LibConstants.WARNING_ESSENCE can be used
        // int criticalDisplay = (int) ((LibConstants.WARNING_ESSENCE / (float)menu.maxEssence) * 49);

        // guiGraphics.blit(SURGERY_GUI_TEXTURES, leftPos + 5, topPos + 5 + (49 - essenceDisplay), 176, 61 + (49 - essenceDisplay), 9, essenceDisplay);
        // ... more slot drawing logic
    }


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Text from old drawGuiContainerForegroundLayer
        // Ensure menu object has fresh data by calling menu.updateClientSyncedValues() if needed,
        // though it's usually called in init() or if BE sends an update packet.

        if (currentPage == 0 && transitionStartTime == 0) { // Main page, not transitioning
            String playerName = Minecraft.getInstance().player.getName().getString().toUpperCase();
            guiGraphics.drawString(this.font, "_" + playerName,this.imageWidth / 2 - this.font.width("_" + playerName) / 2, 115, 0x1DA9C1, false);
        }

        if (currentPage == 9) { // Index page
            guiGraphics.drawString(this.font, Component.translatable("cyberware.gui.installed"), 8, 9, 0x1DA9C1, false);
        }

        if (currentPage != 9) { // Not index page
            String essenceText = menu.essence + " / " + menu.maxEssence;
            guiGraphics.drawString(this.font, essenceText, 18, 6, 0x1DA9C1, false);
        }

        // TODO: Port rendering of item stacks in custom slots (from drawGuiContainerForegroundLayer)
        // This involves iterating `visibleSlots` and using `guiGraphics.renderItem`
        // Also, tooltips for these items and other GUI elements.
    }

    // Helper for game ticks
    private float ticksExisted() {
        return this.minecraft.player != null ? this.minecraft.player.tickCount : 0;
    }

    // Animation easing function (from GuiSurgery)
    private static float ease(float percent, float startValue, float endValue) {
        endValue -= startValue;
        // Using Mth.sin for a smooth ease-in-out like effect, common in modern Minecraft GUIs
        // Or stick to original if specific feel is desired. Original:
        // float total = 100; float elapsed = percent * total;
        // if ((elapsed /= total / 2) < 1) return endValue / 2 * elapsed * elapsed + startValue;
        // return -endValue / 2 * ((--elapsed) * (elapsed - 2) - 1) + startValue;

        // Simpler quadratic ease in-out:
        percent = Mth.clamp(percent, 0.0F, 1.0F);
        float t = percent < 0.5f ? 2.0f * percent * percent : 1.0f - (float)Math.pow(-2.0f * percent + 2.0f, 2.0f) / 2.0f;
        return startValue + endValue * t;
    }

    private static PageConfiguration interpolate(float amountDone, PageConfiguration start, PageConfiguration end) {
        return new PageConfiguration(
                ease(amountDone, start.rotation, end.rotation),
                ease(amountDone, start.x, end.x),
                ease(amountDone, start.y, end.y),
                ease(amountDone, start.scale, end.scale),
                ease(amountDone, start.boxWidth, end.boxWidth),
                ease(amountDone, start.boxHeight, end.boxHeight),
                ease(amountDone, start.boxX, end.boxX),
                ease(amountDone, start.boxY, end.boxY)
        );
    }

    // Inner class PageConfiguration (from GuiSurgery)
    private static class PageConfiguration {
        public float rotation;
        public float x;
        public float y;
        public float scale;
        public float boxWidth;
        public float boxHeight;
        public float boxX;
        public float boxY;

        public PageConfiguration(float rotation, float x, float y, float scale) {
            this(rotation, x, y, scale, 0, 0, 0, 0);
        }

        public PageConfiguration(float rotation, float x, float y, float scale, float boxWidth, float boxHeight, float boxX, float boxY) {
            this.rotation = rotation;
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.boxWidth = boxWidth;
            this.boxHeight = boxHeight;
            this.boxX = boxX;
            this.boxY = boxY;
        }

        public PageConfiguration copy() {
            return new PageConfiguration(rotation, x, y, scale, boxWidth, boxHeight, boxX, boxY);
        }
    }

    // Inner enum Type for buttons (from GuiSurgery) - for InterfaceButton later
    private enum ButtonType {
        BACK(176, 111, 18, 10),
        INDEX(176, 122, 12, 11);

        public final int u;
        public final int v;
        public final int width;
        public final int height;

        ButtonType(int u, int v, int width, int height) {
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
        }
    }

    // TODO: Port mouse handling methods (mouseClicked, mouseReleased, mouseDragged if needed)
    // TODO: Port button action handling (actionPerformed -> onPress for widgets)
    // TODO: Port methods for updating slot visibility (updateSurgerySlotsVisibility)
    // TODO: Port methods for rendering entities and models (renderEntity, renderModel)
    // TODO: Port tooltip rendering (renderToolTip, getTooltipFromItem)
    // TODO: Port network packet sending (handleMouseClick for SlotSurgery interaction)
}
