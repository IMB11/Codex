package mine.block.codex.ui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import me.x150.renderer.renderer.Renderer2d;
import me.x150.renderer.renderer.color.Color;
import me.x150.renderer.renderer.util.BlurMaskFramebuffer;
import mine.block.codex.search.SearchResult;
import mine.block.codex.ui.widgets.CodexIconButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CodexScreen extends SpruceScreen {
    private final SearchResult result;
    private final QuicksearchScreen parent;

    protected CodexScreen(SearchResult result, @Nullable QuicksearchScreen parent) {
        super(Text.empty());
        this.result = result;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.addSelectableChild(new CodexIconButton(Position.of(getRelativeX(0), (int) (5f / 100f * this.height)), 16, 16, new Identifier("codex:textures/ui/back.png"), button -> this.client.setScreen(parent)));
        this.addSelectableChild(new CodexIconButton(Position.of((int) (95f / 100f * this.width) - 16, (int) (5f / 100f * this.height)), 16, 16, new Identifier("codex:textures/ui/exit.png"), button -> this.client.setScreen(null)));
    }

    private int getRelativeX(int x) {
        return x + (int) (5f / 100f * this.width);
    }

    private int getRelativeY(int y) {
        return y + (int) (5f / 100f * this.height) + 16;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        super.render(matrices, mouseX, mouseY, delta);

        BlurMaskFramebuffer.useAndDraw(() -> {
            Renderer2d.renderQuad(matrices, Color.WHITE, 0, 0, this.width, this.height);
        }, 16);

        fill(matrices, (int) (5f / 100f * this.width), (int) (5f / 100f * this.height), (int) ((100 - 5f) / 100f * this.width), (int) ((100 - 5f) / 100f * this.height), CodexColors.CODEX_ELEVATED);
        fill(matrices, (int) (5f / 100f * this.width), (int) (5f / 100f * this.height), (int) ((100 - 5f) / 100f * this.width), (int) (5f / 100f * this.height) + 16, CodexColors.CODEX_BG);

        drawTextWithShadow(matrices, this.textRenderer, this.result.getName(), getRelativeX(12 + 32), getRelativeY(19 - (this.textRenderer.fontHeight / 2)), CodexColors.WHITE);

        // Item/Block render.
        {
            RenderSystem.enableDepthTest();
            MinecraftClient client = MinecraftClient.getInstance();
            ItemRenderer renderer = client.getItemRenderer();
            var model = renderer.getModel(this.result.getEntry().getDefaultStack(), this.client.world, null, 0);
            client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate((float) getRelativeX(5 + 8), (float) getRelativeY(5 + 8), 100.0F + renderer.zOffset);
            matrixStack.translate(8.0F, 8.0F, 0.0F);
            matrixStack.scale(2.0F, -2.0F, 2.0F);
            matrixStack.scale(16.0F, 16.0F, 16.0F);
            RenderSystem.applyModelViewMatrix();
            MatrixStack matrixStack2 = new MatrixStack();
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            boolean bl = !model.isSideLit();
            if (bl) {
                DiffuseLighting.disableGuiDepthLighting();
            }

            renderer.renderItem(this.result.getEntry().getDefaultStack(), ModelTransformation.Mode.GUI, false, matrixStack2, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
            immediate.draw();
            RenderSystem.enableDepthTest();
            if (bl) {
                DiffuseLighting.enableGuiDepthLighting();
            }

            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
        }

        for (var element : this.children()) {
            if (element instanceof Drawable drawable)
                drawable.render(matrices, mouseX, mouseY, delta);
        }

        this.textRenderer.drawTrimmed(Text.translatable(this.result.getKey().getValue().toTranslationKey("description")), getRelativeX(5), getRelativeY(32 + 10), (int) (85f / 100f * this.width), CodexColors.DARK_GRAY);
    }
}
