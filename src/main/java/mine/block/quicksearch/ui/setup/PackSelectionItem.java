package mine.block.quicksearch.ui.setup;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceCheckboxWidget;
import me.x150.renderer.renderer.Rectangle;
import mine.block.quicksearch.ui.CodexColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class PackSelectionItem extends EmptyWidget {

    public final SetupScreen.CodexPack pack;
    public boolean shouldUse;

    public PackSelectionItem(SetupScreen quicksearchUI, SetupScreen.CodexPack codexPack, int width, int height) {
        super(width, height);
        this.pack = codexPack;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.hovered = !this.pack.forcedEnabled() && (mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height);

        int color = this.isHovered() ? CodexColors.CODEX_ELEVATED_HOVER : CodexColors.CODEX_ELEVATED;
        int borderColor = this.shouldUse || this.pack.forcedEnabled() ? this.pack.forcedEnabled() ? CodexColors.CODEX_ACCENT_B : CodexColors.CODEX_ACCENT_A : color;

        fill(matrices, getX(), getY() + getHeight(), getX() + getWidth() - 9, getY(), borderColor);
        fill(matrices, getX() + 1, getY() + getHeight() - 1, getX() + getWidth() - 10, getY() + 1, color);

        RenderSystem.enableDepthTest();
        MinecraftClient client = MinecraftClient.getInstance();
        RenderSystem.setShaderTexture(0, this.pack.iconTexture());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        drawTexture(matrices,
                getX(),
                getY(),
                this.getHeight(),
                this.getHeight(),
                0,
                0,
                this.pack.texWidth(),
                this.pack.texHeight(),
                this.pack.texHeight(),
                this.pack.texHeight());
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        client.textRenderer.drawWithShadow(matrices,
                pack.title(),
                getX() + this.getHeight(),
                getY() + (getHeight()/2) - client.textRenderer.fontHeight,
                CodexColors.WHITE);
        client.textRenderer.drawWithShadow(matrices,
                pack.license(),
                getX() + this.getHeight(),
                getY() + (getHeight()/2),
                CodexColors.DARK_GRAY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.shouldUse = !this.shouldUse;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
