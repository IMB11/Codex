package mine.block.quicksearch.ui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import mine.block.quicksearch.search.SearchResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightingProvider;

public class CodexScreen extends SpruceScreen {
    private final SearchResult result;

    protected CodexScreen(SearchResult result) {
        super(Text.empty());
        this.result = result;
    }

    private int getRelativeX(int x) {
        return x + (int)(5f/100f * this.width);
    }

    private int getRelativeY(int y) {
        return y + (int)(5f/100f * this.height) + 16;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        fill(matrices, (int)(5f/100f * this.width), (int)(5f/100f * this.height), (int)((100-5f)/100f * this.width), (int)((100-5f)/100f * this.height), 0xFF191414);
        fill(matrices, (int)(5f/100f * this.width), (int)(5f/100f * this.height),  (int)((100-5f)/100f * this.width), (int)(5f/100f * this.height)+16, 0xFF302f2f);
        drawTextWithShadow(matrices, this.textRenderer, this.result.getName(), getRelativeX(12+32), getRelativeY(19-(this.textRenderer.fontHeight/2)), 0xFFFFFFFF);

        RenderSystem.enableDepthTest();
        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer renderer = client.getItemRenderer();
        MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        var model = renderer.getModel(this.result.getEntry().getDefaultStack(), (World)null, null, 0);
        renderer.zOffset = 100f;
        matrixStack.push();
        matrixStack.translate((float)getRelativeX(12), (float)getRelativeY(12), 100.0F + renderer.zOffset);
        matrixStack.translate(8.0F, 8.0F, 0.0F);
        matrixStack.scale(2F, -2.0F, 2F);
        matrixStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        renderer.renderItem(this.result.getEntry().getDefaultStack(), ModelTransformation.Mode.GUI, false, matrixStack2, immediate, 0xF000FF, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        renderer.zOffset = 0f;

        this.textRenderer.drawTrimmed(Text.translatable(this.result.getKey().getValue().toTranslationKey("description")), getRelativeX(5), getRelativeY(32+10),  (int)((100-5f)/100f * (this.width - 10)), 0x7FFFFFFF);
    }
}
