package mine.block.quicksearch.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import mine.block.quicksearch.search.SearchResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

public class SearchItemWidget extends EmptyWidget {
    private final SearchResult result;
    private final QuicksearchUI parent;
    private final int i;

    public SearchItemWidget(QuicksearchUI parent, SearchResult result, int i) {
        super(parent.topCorner.x, (int) ((parent.topCorner.y+1+(i*(parent.search_height+1)))+parent.scrollOffset), (int) parent.search_width, (int) parent.search_height);
        this.result = result;
        this.parent = parent;
        this.i = i;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.setWidth((int) parent.search_width);
        this.height = (int) parent.search_height;
        this.setX((int) (parent.topCorner.x-parent.search_width));
        this.setY((int) ((parent.topCorner.y+(i*(parent.search_height)+1))+parent.scrollOffset));
        int color = this.isHovered() ? 0xFF191414 : 0x7F191414;

        fill(matrices, getX(), getY(), (int) (getX()+parent.search_width), (int) (getY()+parent.search_height), color);

        RenderSystem.enableDepthTest();
        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer renderer = client.getItemRenderer();
        renderer.zOffset = 100f;
        renderer.renderInGui(result.getEntry().getDefaultStack(),
                getX() + 10,
                (int) (((getY()) - 10 - (client.textRenderer.fontHeight+4.5f))));
        renderer.zOffset = 0f;
        RenderSystem.disableDepthTest();

        client.textRenderer.drawWithShadow(matrices,
                result.getName(),
                getX() + 32,
                (int) (getY() - ((client.textRenderer.fontHeight+4.5f) * 1.5f)),
                Formatting.GRAY.getColorValue());
    }
}
