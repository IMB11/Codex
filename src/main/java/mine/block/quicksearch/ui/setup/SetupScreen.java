package mine.block.quicksearch.ui.setup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceLabelWidget;
import mine.block.quicksearch.client.QuicksearchClient;
import mine.block.quicksearch.ui.CodexColors;
import mine.block.quicksearch.ui.SearchEntryWidget;
import mine.block.quicksearch.util.RandomString;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class SetupScreen extends SpruceScreen {

    public final ArrayList<PackSelectionItem> items = new ArrayList<>();
    public final float packListWidth = 213.5f;
    public Gson GSON = new Gson();
    public boolean failed = false;
    public float scrollOffset = 0;
    public Vector2i topCorner;
    public Vector2i bottomCorner;
    public SetupScreen() {
        super(Text.empty());
    }

    private JsonElement readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            return GSON.fromJson(rd, JsonElement.class);
        } finally {
            is.close();
        }
    }

    @Override
    protected void init() {
        super.init();

        if (this.items.isEmpty()) {
            try {
                JsonElement indexData = readJsonFromUrl(QuicksearchClient.CONFIG.baseUrl() + "/_index.json?" + new Random().nextInt(0, 1000));
                System.out.println(indexData.toString());
                for (JsonElement jsonElement : indexData.getAsJsonArray()) {
                    JsonObject codexPack = jsonElement.getAsJsonObject();

                    String codexPackIconURL = QuicksearchClient.CONFIG.baseUrl() + codexPack.get("icon").getAsString();
                    NativeImage image = NativeImage.read(NativeImage.Format.RGBA, new URL(codexPackIconURL).openStream());
                    NativeImageBackedTexture codexPackIcon = new NativeImageBackedTexture(image);

                    Identifier codexPackIconTexture = new Identifier("codex", new RandomString().nextString());
                    this.client.getTextureManager().registerTexture(codexPackIconTexture, codexPackIcon);
                    CodexPack pack = new CodexPack(codexPack.get("title").getAsString(),
                            codexPack.get("license").getAsString(),
                            codexPackIconTexture,
                            image.getWidth(),
                            image.getHeight(),
                            codexPack.get("descriptions").getAsString(),
                            codexPack.get("requiredIf").getAsJsonArray());

                    items.add(new PackSelectionItem(this, pack, (int) packListWidth, 30));
                }
            } catch (Exception e) {
                failed = true;
                e.printStackTrace();
                return;
            }
        }

        for (PackSelectionItem item : items) {
            this.addSelectableChild(item);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        float offsetMax = -(items.size() * packListWidth);

        if (items.size() < 4) return super.mouseScrolled(mouseX, mouseY, amount);

        if (scrollOffset < offsetMax) {
            scrollOffset = offsetMax;
        } else {
            scrollOffset -= (float) (-amount * 5);
        }

        if (scrollOffset > 0) {
            scrollOffset = 0;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public Vector2i calculateTopLeftCorner(float width, float height) {
        return new Vector2i((int) width / 6, 5);
    }

    public Vector2i calculateBottomRightCorner(float width, float height) {
        return new Vector2i((int) width, (this.height / 8));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, this.width, this.height, CodexColors.CODEX_BG);

        topCorner = calculateTopLeftCorner(packListWidth, 30);
        bottomCorner = calculateBottomRightCorner(packListWidth, 30);

        for (int i = 0; i < this.items.size(); i++) {
            PackSelectionItem widget = items.get(i);
            widget.acceptBounds((int) (topCorner.x - packListWidth + 1), bottomCorner.y + 30 + 1, (int) packListWidth, (int) (this.height / 1.25f));
            widget.setY((int) ((topCorner.y) + (i * 30) + scrollOffset) + i + 1);
            widget.setX((topCorner.x - 30) + 1);
            widget.render(matrices, mouseX, mouseY, delta);
        }

        for (var element : this.children()) {
            if (element instanceof Drawable drawable && !(element instanceof SearchEntryWidget))
                drawable.render(matrices, mouseX, mouseY, delta);
        }

        this.textRenderer.draw(matrices, Text.literal("Codex Setup"), (packListWidth + 9 + this.width - this.textRenderer.getWidth(Text.literal("Codex Setup"))) / 2, 15 + 10 - this.textRenderer.fontHeight, CodexColors.WHITE);
        this.textRenderer.drawTrimmed(matrices, Text.literal("Please select codex packs to download on the left. Packs highlighted red are required because a mod was detected that needs it - or it is part of vanilla."), );

        Tooltip.renderAll(this, matrices);
    }

    public record CodexPack(String title, String license, Identifier iconTexture, int texWidth, int texHeight,
                            String descriptions, JsonArray requiredIf) {
        boolean forcedEnabled() {
            for (JsonElement jsonElement : requiredIf) {
                String id = jsonElement.getAsString();
                if (id.equals("default") || FabricLoader.getInstance().isModLoaded(id)) {
                    return true;
                }
            }
            return false;
        }
    }
}
