package mine.block.quicksearch.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.renderer.Rectangle;
import mine.block.quicksearch.client.QuicksearchClient;
import mine.block.quicksearch.search.SearchResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

public class SearchEntryWidget extends EmptyWidget {

    private final SearchResult result;
    private Rectangle clickableBounds;
    private final QuicksearchUI parent;

    public SearchEntryWidget(QuicksearchUI quicksearchUI, SearchResult searchResult, int width, int height) {
        super(width, height);
        this.parent = quicksearchUI;
        this.result = searchResult;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (clickableBounds != null) {
            if (!clickableBounds.contains(this.getX(), this.getY())) {
                return false;
            }
        }

        if (QuicksearchClient.OPEN_RECIPE_SUPPLIER == null) {
            return false;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

        int color = this.isHovered() ? CodexColors.withAlpha(CodexColors.CODEX_BG, CodexColors.ALPHA_75) : CodexColors.withAlpha(CodexColors.CODEX_BG, CodexColors.ALPHA_50);

        fill(matrices, getX(), getY() + getHeight(), getX() + getWidth(), getY(), color);

        RenderSystem.enableDepthTest();
        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer renderer = client.getItemRenderer();
        renderer.zOffset = 100f;
        renderer.renderInGui(result.getEntry().getDefaultStack(),
                getX() + 10,
                (int) (((getY() + getHeight()) - 10 - (client.textRenderer.fontHeight + 4.5f))));
        renderer.zOffset = 0f;
        RenderSystem.disableDepthTest();

        client.textRenderer.drawWithShadow(matrices,
                result.getName(),
                getX() + 32,
                (int) (getY() + getHeight() - ((client.textRenderer.fontHeight + 4.5f) * 1.5f)),
                Formatting.GRAY.getColorValue());
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        MinecraftClient.getInstance().setScreen(new CodexScreen(this.result));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void acceptBounds(int x, int y, int width, int height) {
        this.clickableBounds = new Rectangle(x, y, x + width, y + height);
    }
}
