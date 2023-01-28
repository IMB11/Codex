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

    private final SetupScreen.CodexPack pack;
    private final SpruceCheckboxWidget checkbox;
    private Rectangle clickableBounds;
    private final SetupScreen parent;
    private boolean shouldUse;

    public PackSelectionItem(SetupScreen quicksearchUI, SetupScreen.CodexPack codexPack, int width, int height) {
        super(width, height);
        this.parent = quicksearchUI;
        this.pack = codexPack;
        this.checkbox = new SpruceCheckboxWidget(Position.origin(), this.width() - 5, this.width() - 5, Text.empty(), this.pack.forcedEnabled());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (clickableBounds != null) {
            if (!clickableBounds.contains(this.getX(), this.getY())) {
                return false;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

        int color = this.isHovered() ? CodexColors.CODEX_ELEVATED_HOVER : CodexColors.CODEX_ELEVATED;
        int borderColor = this.shouldUse ? CodexColors.CODEX_ACCENT_A : color;

        fill(matrices, getX(), getY() + getHeight(), getX() + getWidth(), getY(), borderColor);
        fill(matrices, getX() + 1, getY() + getHeight() - 1, getX() + getWidth() - 1, getY() + 1, color);

        RenderSystem.enableDepthTest();
        MinecraftClient client = MinecraftClient.getInstance();
        RenderSystem.setShaderTexture(0, this.pack.iconTexture());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        drawTexture(matrices,
                getX() + 5,
                getY() + 5,
                this.getHeight() - 10,
                this.getHeight() - 10,
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
                getX() + 32,
                (int) (getY() + getHeight() - ((client.textRenderer.fontHeight + 4.5f) * 1.5f) - 3),
                CodexColors.WHITE);
        client.textRenderer.drawWithShadow(matrices,
                pack.license(),
                getX() + 32,
                (int) (getY() + getHeight() - ((client.textRenderer.fontHeight + 4.5f) * 1.5f) + 7),
                CodexColors.DARK_GRAY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.shouldUse = !this.shouldUse;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void acceptBounds(int x, int y, int width, int height) {
        this.clickableBounds = new Rectangle(x, y, x + width, y + height);
    }
}
