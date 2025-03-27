package flaxbeard.cyberware.client.screen.surgery.component;

import flaxbeard.cyberware.client.menu.SurgeryMenu;
import flaxbeard.cyberware.client.screen.surgery.SurgeryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ButtonInterface extends Button {
    private final int x;
    private final int y;
    private final int texX;
    private final int texY;
    private final int texWidth;
    private final int texHeight;
    protected ButtonInterface(OnPress onPress, int x, int y, int texX, int texY, int texWidth, int texHeight) {
        super(x, y, texWidth, texHeight, Component.empty(), onPress, new CreateNarration() {
            @Override
            public @NotNull MutableComponent createNarrationMessage(@NotNull Supplier<MutableComponent> supplier) {
                return Component.empty();
            }
        });
        this.x = x;
        this.y = y;
        this.texX = texX;
        this.texY = texY;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft.getInstance().getTextureManager().bindForSetup(SurgeryScreen.SURGERY_GUI_TEXTURES);
        guiGraphics.blit(
                SurgeryScreen.SURGERY_GUI_TEXTURES,
                x, y,
                texX, texY,
                texWidth, texHeight,
                this.width, this.height);
    }


}
