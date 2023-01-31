package mine.block.codex.ui.widgets;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import mine.block.codex.ui.CodexColors;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CodexButton extends SpruceButtonWidget {
    public CodexButton(Position position, int width, int height, Text message, PressAction action) {
        super(position, width, height, message, action);
    }

    @Override
    protected void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int color = this.hovered ? CodexColors.CODEX_ELEVATED_HOVER : CodexColors.CODEX_ELEVATED;
        fill(matrices, getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
        fill(matrices, getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, CodexColors.CODEX_ELEVATED);
    }
}
