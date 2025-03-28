package flaxbeard.cyberware.client.screen.surgery.component;

import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.Cyberware;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReturnButton extends AbstractButton {
    private final Runnable onPress;

    public static final ResourceLocation RETURN;
    public static final ResourceLocation RETURN_BG;

    static {
        RETURN = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/element/return_button.png");
        RETURN_BG = ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "textures/gui/element/return_button_bg.png");
    }

    public ReturnButton(int x, int y, Runnable onPress) {
        super(x, y, 18, 10, Component.empty());

        this.onPress = onPress;
        setTooltip(Tooltip.create(Component.translatable("gui.cyberware.surgery.button.return")));
    }

    @Override
    public void onPress() {
        if (!active) return;
        onPress.run();
    }

    @Override
    public @Nullable Tooltip getTooltip() {
        return active ? super.getTooltip() : null;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!active) return;

        Minecraft.getInstance().getTextureManager().bindForSetup(RETURN);
        Minecraft.getInstance().getTextureManager().bindForSetup(RETURN_BG);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, isHovered() ? 0.3F : 0.2F);
        guiGraphics.blit(RETURN,
                this.getX(),
                this.getY(),
                0, 0,
                this.width, this.height,
                this.width, this.height);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, isHovered() ? 0.6F : 0.5F);
        guiGraphics.blit(RETURN_BG,
                this.getX(),
                this.getY(),
                0, 0,
                this.width, this.height,
                this.width, this.height);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}
