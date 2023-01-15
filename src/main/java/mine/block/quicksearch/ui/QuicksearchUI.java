package mine.block.quicksearch.ui;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.functions.FunctionIfc;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.util.ScissorManager;
import me.x150.renderer.renderer.Renderer2d;
import me.x150.renderer.renderer.color.Color;
import me.x150.renderer.renderer.util.BlurMaskFramebuffer;
import mine.block.quicksearch.math.FunctionRegistry;
import mine.block.quicksearch.search.SearchManager;
import mine.block.quicksearch.search.SearchResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2i;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class QuicksearchUI extends SpruceScreen {
    public TextFieldWidget inputBox;
    public Vector2i topCorner;
    public Vector2i bottomCorner;

    public QuicksearchUI() {
        super(Text.translatable("quicksearch.screen.title"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public Vector2i calculateTopLeftCorner(float width, float height) {
        return new Vector2i((int) ((this.width / 2) + (width / 2)), (int) ((this.height / 8) + (height / 2)));
    }

    public Vector2i calculateBottomRightCorner(float width, float height) {
        return new Vector2i((int) ((this.width / 2) - (width / 2)), (int) ((this.height / 8) - (height / 2)));
    }

    public final float search_width = 213.5f;
    public final float search_height = 30;

    @Override
    protected void init() {
        super.init();

        topCorner = calculateTopLeftCorner(search_width, search_height);
        bottomCorner = calculateBottomRightCorner(search_width, search_height);

        inputBox = new TextFieldWidget(this.textRenderer, (int) (topCorner.x - search_width + 5), (int) (topCorner.y - (search_height / 2f) - (this.textRenderer.fontHeight / 2f)), (int) (search_width - 25), (int) search_height, Text.of(""));
        inputBox.setDrawsBackground(false);
        inputBox.setPlaceholder(Text.literal("Type anything...").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        inputBox.setVisible(true);
        inputBox.setTextFieldFocused(true);
        inputBox.setFocusUnlocked(true);
        inputBox.setChangedListener(this::inputChanged);

//        this.addDrawableChild(resultList);
        this.addDrawableChild(inputBox);
    }

    public InputMode currentMode = InputMode.NONE;
    public String output = null;
    public ArrayList<SearchItemWidget> resultArrayList = new ArrayList<>();

    private void inputChanged(String s) {
        output = null;
        resultArrayList.clear();
        if(s.startsWith("=")) {
            currentMode = InputMode.MATH;

            try {
                var txt_expression = s.substring(1);

                ExpressionConfiguration configuration = ExpressionConfiguration.defaultConfiguration()
                        .withAdditionalFunctions(FunctionRegistry.CUSTOM_FUNCTIONS.entrySet().toArray(new Map.Entry[FunctionRegistry.CUSTOM_FUNCTIONS.size()]));
                Expression expression = new Expression(txt_expression, configuration).withValues(Map.of(
                        "STACK", 64
                ));

                var eval= expression.evaluate();

                switch (eval.getDataType()) {
                    case STRING -> output = eval.getStringValue();
                    case NUMBER -> output = eval.getNumberValue().toPlainString();
                    case BOOLEAN -> output = StringUtils.capitalize(eval.getBooleanValue().toString());
                    default -> throw new Exception();
                }

                float search_width = (this.width / 3f) + (this.width / 6f);

                if(this.textRenderer.getWidth(output) > search_width) {
                    output = this.textRenderer.trimToWidth(output, (int) search_width - 25) + "...";
                }
            } catch (Exception ignored) {
                output = "?";
            }
        } else if (s.isBlank()) {
            currentMode = InputMode.NONE;
        } else {
            currentMode = InputMode.SEARCH;

            final var keys = SearchManager.SEARCH_MAP.keySet();
            int i = 1;
            for (String key : keys) {
                if(key.contains(s)) {
                    // DrawableHelper.fill(matrices, topCorner.x, (int) ((topCorner.y+1+(i*(search_height+1)))+scrollOffset), bottomCorner.x, (int) ((bottomCorner.y+1+(i*(search_height+1)))+scrollOffset), 0xFF191414);
                    resultArrayList.add(new SearchItemWidget(this, SearchManager.SEARCH_MAP.get(key), i));
                    i++;
                }
            }
        }
    }

    public float scrollOffset = 0;

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        float offsetMax = -(resultArrayList.size() * search_width);

        if(resultArrayList.size() < 4) return super.mouseScrolled(mouseX, mouseY, amount);

        if(scrollOffset < offsetMax) {
            scrollOffset = offsetMax;
        } else {
           scrollOffset -= (float) (-amount * 5);
        }

        if(scrollOffset > 0) {
            scrollOffset = 0;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.mouseScrolled(mouseX, mouseY, deltaY / 5);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ScissorManager.pushScaleFactor(this.scaleFactor);

        BlurMaskFramebuffer.useAndDraw(() -> {
            Renderer2d.renderQuad(matrices, Color.WHITE, 0, 0, this.width, this.height);
        }, 16);

        topCorner = calculateTopLeftCorner(search_width, search_height);
        bottomCorner = calculateBottomRightCorner(search_width, search_height);

        DrawableHelper.fill(matrices, topCorner.x, topCorner.y, bottomCorner.x, bottomCorner.y, 0xFF191414);
        DrawableHelper.fill(matrices, (int) (topCorner.x - search_width) - 5, topCorner.y, (int) (topCorner.x - search_width) + 1, bottomCorner.y, this.currentMode.getColor());
        if(output != null) {
            if(currentMode == InputMode.MATH) {
                DrawableHelper.fill(matrices, topCorner.x, (int) (topCorner.y + search_height + 1), bottomCorner.x, (int) (bottomCorner.y + search_height + 1), 0x7F191414);
                this.textRenderer.drawWithShadow(matrices, Text.literal(output).formatted(Formatting.ITALIC), topCorner.x - search_width + 5, topCorner.y - (search_height / 2f) + search_height - (this.textRenderer.fontHeight / 2f), Formatting.GRAY.getColorValue());
            }
        } else {
            if(currentMode == InputMode.SEARCH) {

                ScissorManager.push((int) (topCorner.x - search_width + 1), (int) (bottomCorner.y + search_height + 1), (int) search_width, (int) (this.height / 1.25f), scaleFactor);

                resultArrayList.forEach(searchItemWidget -> searchItemWidget.render(matrices, mouseX, mouseY, delta));

                ScissorManager.pop();
            }
        }

        this.renderWidgets(matrices, mouseX, mouseY, delta);
        Tooltip.renderAll(this, matrices);
        ScissorManager.popScaleFactor();
    }

    public enum InputMode {
        NONE(0xFF191414),
        MATH(0xFF239cba),
        SEARCH(0xFF7423ba);

        public int getColor() {
            return color;
        }

        private final int color;

        InputMode(int color) {
            this.color = color;
        }
    }
}
