package mine.block.quicksearch.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import mine.block.quicksearch.ui.CodexColors;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CodexIconButton extends SpruceIconButtonWidget {
    private Identifier icon;

    public CodexIconButton(Position position, int width, int height, Identifier icon, PressAction action) {
        super(position, width, height, Text.empty(), action);
        this.icon = icon;
    }

    @Override
    protected void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int color = this.hovered ? CodexColors.CODEX_ELEVATED_HOVER : CodexColors.CODEX_ELEVATED;
        fill(matrices, getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
        fill(matrices, getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, CodexColors.CODEX_ELEVATED);
    }

    @Override
    protected int renderIcon(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, this.icon);
        RenderSystem.enableBlend();
        drawTexture(matrices, this.getX()+2, this.getY()+2, 16, 16, 0F, 0F, 32, 32, 32, 32);
        RenderSystem.disableBlend();
        return super.renderIcon(matrices, mouseX, mouseY, delta);
    }
}
