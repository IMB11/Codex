package mine.block.codex.ui;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.util.ScissorManager;
import me.x150.renderer.renderer.Renderer2d;
import me.x150.renderer.renderer.color.Color;
import me.x150.renderer.renderer.util.BlurMaskFramebuffer;
import mine.block.codex.CodexUtils;
import mine.block.codex.math.MathRegistry;
import mine.block.codex.search.SearchManager;
import mine.block.codex.search.SearchResult;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Map;


public class QuicksearchScreen extends SpruceScreen {
    public final float search_width = 213.5f;
    public final float search_height = 30;
    public TextFieldWidget inputBox;
    public Vector2i topCorner;
    public Vector2i bottomCorner;
    public InputMode currentMode = InputMode.NONE;
    public String output = null;
    public ArrayList<SearchEntryWidget> resultArrayList = new ArrayList<>();
    public float scrollOffset = 0, targetScrollOffset = 0;

    public QuicksearchScreen() {
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
        this.addDrawableChild(inputBox);

        this.resultArrayList.clear();
    }

    private void inputChanged(String query) {
        output = null;
        if (!resultArrayList.isEmpty()) {
            resultArrayList.forEach(this::remove);
            resultArrayList.clear();
        }
        if (query.startsWith("=")) {
            currentMode = InputMode.MATH;

            try {
                var txt_expression = query.substring(1);

                ExpressionConfiguration configuration = ExpressionConfiguration.defaultConfiguration()
                        .withAdditionalFunctions(MathRegistry.CUSTOM_FUNCTIONS.entrySet().toArray(new Map.Entry[MathRegistry.CUSTOM_FUNCTIONS.size()]));
                Expression expression = new Expression(txt_expression, configuration).withValues(MathRegistry.CUSTOM_CONSTANTS);

                var eval = expression.evaluate();

                switch (eval.getDataType()) {
                    case STRING -> output = eval.getStringValue();
                    case NUMBER -> output = eval.getNumberValue().toPlainString();
                    case BOOLEAN -> output = StringUtils.capitalize(eval.getBooleanValue().toString());
                    default -> throw new Exception();
                }

                float search_width = (this.width / 3f) + (this.width / 6f);

                if (this.textRenderer.getWidth(output) > search_width) {
                    output = this.textRenderer.trimToWidth(output, (int) search_width - 25) + "...";
                }
            } catch (Exception ignored) {
                output = "?";
            }
        } else if (query.isBlank()) {
            currentMode = InputMode.NONE;
        } else {
            currentMode = InputMode.SEARCH;

            final var results = SearchManager.search(query);
            int i = 1;
            for (SearchResult result : results) {
                var widget = new SearchEntryWidget(this, result, (int) search_width, (int) search_height);
                resultArrayList.add(widget);
                widget.visible = true;
                widget.setY((int) (search_height * i));
                widget.setX(topCorner.x);
                this.addDrawableChild(widget);
                i++;
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        float offsetMax = -((resultArrayList.size() - 3) * search_height);

        if (resultArrayList.size() < 4) return super.mouseScrolled(mouseX, mouseY, amount);

        if (targetScrollOffset < offsetMax) {
            scrollOffset = offsetMax;
            targetScrollOffset = offsetMax;
        } else {
            targetScrollOffset -= (float) (-amount * 12.5f);

            if (targetScrollOffset > 0) {
                targetScrollOffset = 0;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.mouseScrolled(mouseX, mouseY, deltaY / 1.5f);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ScissorManager.pushScaleFactor(this.scaleFactor);

        this.scrollOffset = CodexUtils.lerp(delta, this.scrollOffset, this.targetScrollOffset);

        BlurMaskFramebuffer.useAndDraw(() -> {
            Renderer2d.renderQuad(matrices, Color.WHITE, 0, 0, this.width, this.height);
        }, 16);

        topCorner = calculateTopLeftCorner(search_width, search_height);
        bottomCorner = calculateBottomRightCorner(search_width, search_height);

        DrawableHelper.fill(matrices, topCorner.x, topCorner.y, bottomCorner.x, bottomCorner.y, CodexColors.CODEX_BG);
        DrawableHelper.fill(matrices, (int) (topCorner.x - search_width) - 5, topCorner.y, (int) (topCorner.x - search_width) + 1, bottomCorner.y, this.currentMode.getColor());
        if (output != null) {
            if (currentMode == InputMode.MATH) {
                DrawableHelper.fill(matrices, topCorner.x, (int) (topCorner.y + search_height + 1), bottomCorner.x, (int) (bottomCorner.y + search_height + 1), 0x7F191414);
                this.textRenderer.drawWithShadow(matrices, Text.literal(output).formatted(Formatting.ITALIC), topCorner.x - search_width + 5, topCorner.y - (search_height / 2f) + search_height - (this.textRenderer.fontHeight / 2f), Formatting.GRAY.getColorValue());
            }
        } else {
            if (currentMode == InputMode.SEARCH) {

                ScissorManager.push((int) (topCorner.x - search_width + 1), (int) (bottomCorner.y + search_height + 1), (int) search_width, (int) (this.height / 1.25f), scaleFactor);
                for (int i = 0; i < this.resultArrayList.size(); i++) {
                    SearchEntryWidget widget = resultArrayList.get(i);
                    widget.acceptBounds((int) (topCorner.x - search_width + 1), (int) (bottomCorner.y + search_height + 1), (int) search_width, (int) (this.height / 1.25f));
                    widget.setY((int) ((topCorner.y) + (i * search_height) + scrollOffset) + i + 1);
                    widget.setX((int) (topCorner.x - search_width) + 1);
                    widget.render(matrices, mouseX, mouseY, delta);
                }
                ScissorManager.pop();
                for (var element : this.children()) {
                    if (element instanceof Drawable drawable && !(element instanceof SearchEntryWidget))
                        drawable.render(matrices, mouseX, mouseY, delta);
                }
                Tooltip.renderAll(this, matrices);
                return;
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

        private final int color;

        InputMode(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }
}
