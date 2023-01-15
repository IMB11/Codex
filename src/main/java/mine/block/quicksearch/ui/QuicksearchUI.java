package mine.block.quicksearch.ui;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.functions.FunctionIfc;
import dev.lambdaurora.spruceui.util.ScissorManager;
import me.x150.renderer.renderer.ClipStack;
import me.x150.renderer.renderer.Rectangle;
import mine.block.quicksearch.math.FunctionRegistry;
import mine.block.quicksearch.search.SearchManager;
import mine.block.quicksearch.search.SearchResult;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class QuicksearchUI extends Screen {
    public TextFieldWidget inputBox;

    public QuicksearchUI() {
        super(Text.translatable("quicksearch.screen.title"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public Vector2i calculateTopLeftCorner(float width, float height) {
        return new Vector2i((int) ((this.width / 2) + (width / 2)), (int) ((this.height / 2) + (height / 2)));
    }

    public Vector2i calculateBottomRightCorner(float width, float height) {
        return new Vector2i((int) ((this.width / 2) - (width / 2)), (int) ((this.height / 2) - (height / 2)));
    }

    public final float search_width = 213.5f;
    public final float search_height = 30;

    @Override
    protected void init() {
        super.init();

        Vector2i topCorner = calculateTopLeftCorner(search_width, search_height);
        Vector2i bottomCorner = calculateBottomRightCorner(search_width, search_height);

        inputBox = new TextFieldWidget(this.textRenderer, (int) (topCorner.x - search_width + 5), (int) (topCorner.y - (search_height / 2f) - (this.textRenderer.fontHeight / 2f)), (int) (search_width - 25), (int) search_height, Text.of(""));
        inputBox.setDrawsBackground(false);
        inputBox.setEditable(true);
        inputBox.setPlaceholder(Text.literal("Type anything...").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        inputBox.setVisible(true);
        inputBox.setTextFieldFocused(true);
        inputBox.setChangedListener(this::inputChanged);

//        this.addDrawableChild(resultList);
        this.addDrawableChild(inputBox);
    }

    public InputMode currentMode = InputMode.NONE;
    public String output = null;
    public ArrayList<SearchResult> resultArrayList = new ArrayList<>();
    public ClipStack clipStack = new ClipStack();

    private void inputChanged(String s) {
        output = null;
        resultArrayList.clear();
        if(s.startsWith("=")) {
            currentMode = InputMode.MATH;

            try {
                var txt_expression = s.substring(1);
                ExpressionConfiguration configuration = ExpressionConfiguration.defaultConfiguration()
                        .withAdditionalFunctions((Map.Entry<String, FunctionIfc>[]) FunctionRegistry.CUSTOM_FUNCTIONS.entrySet().toArray());
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
            for (String key : keys) {
                if(key.contains(s)) {
                    resultArrayList.add(SearchManager.SEARCH_MAP.get(key));
                }
            }
        }
    }

    public float scrollOffset = 0;

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        float offsetMax = -(resultArrayList.size() * search_width);
        if(scrollOffset < offsetMax) {
            scrollOffset = offsetMax;
        } else {
           scrollOffset -= (float) (-amount * 2);
        }

        if(scrollOffset > 0) {
            scrollOffset = 0;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        Vector2i topCorner = calculateTopLeftCorner(search_width, search_height);
        Vector2i bottomCorner = calculateBottomRightCorner(search_width, search_height);

        DrawableHelper.fill(matrices, topCorner.x, topCorner.y, bottomCorner.x, bottomCorner.y, 0xFF191414);
        DrawableHelper.fill(matrices, (int) (topCorner.x - search_width) - 5, topCorner.y, (int) (topCorner.x - search_width) + 1, bottomCorner.y, this.currentMode.getColor());
        if(output != null) {
            if(currentMode == InputMode.MATH) {
                DrawableHelper.fill(matrices, topCorner.x, (int) (topCorner.y + search_height + 1), bottomCorner.x, (int) (bottomCorner.y + search_height + 1), 0x7F191414);
                this.textRenderer.drawWithShadow(matrices, Text.literal(output).formatted(Formatting.ITALIC), topCorner.x - search_width + 5, topCorner.y - (search_height / 2f) + search_height - (this.textRenderer.fontHeight / 2f), Formatting.GRAY.getColorValue());
            }
        } else {
            if(currentMode == InputMode.SEARCH) {

                clipStack.use(matrices, new Rectangle(topCorner.x, bottomCorner.y + 1, search_width, search_height*5), () -> {
                    DrawableHelper.fill(matrices, 0, 0, this.width, this.height, 0xFFFFFFFF);
                });

                for (int i = 1; i < resultArrayList.size() + 1; i++) {
                    DrawableHelper.fill(matrices, topCorner.x, (int) ((topCorner.y+1+(i*(search_height+1)))+scrollOffset), bottomCorner.x, (int) ((bottomCorner.y+1+(i*(search_height+1)))+scrollOffset), 0xFF191414);
                    SearchResult result = resultArrayList.get(i-1);
                    this.textRenderer.drawWithShadow(matrices, result.getName(), topCorner.x - search_width + 5, ((topCorner.y+1+(i*(search_height+1))) - ((search_height+1) - (this.textRenderer.fontHeight)))+scrollOffset, Formatting.GRAY.getColorValue());
                }
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
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
