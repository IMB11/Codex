package mine.block.codex.ui.setup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import mine.block.codex.CodexUtils;
import mine.block.codex.client.CodexClient;
import mine.block.codex.ui.CodexColors;
import mine.block.codex.ui.widgets.CodexButton;
import mine.block.codex.util.RandomString;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class SetupScreen extends SpruceScreen {

    public final ArrayList<PackSelectionItem> items = new ArrayList<>();
    public float packListWidth = 213.5f;
    public boolean failed = false;
    public float scrollOffset = 0;
    public Vector2i topCorner;
    public Vector2i bottomCorner;
    public SetupScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        super.init();

        if (this.items.isEmpty()) {
            try {
                JsonElement indexData = CodexUtils.readJsonFromUrl(CodexClient.CONFIG.baseUrl() + "/_index.json?" + new Random().nextInt(0, 1000));
                System.out.println(indexData.toString());
                for (JsonElement jsonElement : indexData.getAsJsonArray()) {
                    JsonObject codexPack = jsonElement.getAsJsonObject();

                    String codexPackIconURL = CodexClient.CONFIG.baseUrl() + codexPack.get("icon").getAsString();
                    NativeImage image = NativeImage.read(NativeImage.Format.RGBA, new URL(codexPackIconURL).openStream());
                    NativeImageBackedTexture codexPackIcon = new NativeImageBackedTexture(image);

                    Identifier codexPackIconTexture = new Identifier("codex", new RandomString().nextString());
                    this.client.getTextureManager().registerTexture(codexPackIconTexture, codexPackIcon);
                    CodexPack pack = new CodexPack(codexPack.get("title").getAsString(),
                            codexPack.get("license").getAsString(),
                            codexPack.get("lang").getAsString(),
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

        this.addSelectableChild(new CodexButton(Position.of(this.width - 155, this.height - 21), 150, 16, Text.literal("Continue"), button -> {
            ArrayList<CodexPack> packsToDownload = new ArrayList<>();
            for (PackSelectionItem item : items) {
                if(item.shouldUse || item.pack.forcedEnabled()) {
                    packsToDownload.add(item.pack);
                }
            }
            this.client.setScreen(new DownloadingScreen(packsToDownload));
        }));
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
        return new Vector2i(5, 5);
    }

    public Vector2i calculateBottomRightCorner(float width, float height) {
        return new Vector2i((int) (5+width), (int) (height+5));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, this.width, this.height, CodexColors.CODEX_BG);

        packListWidth = (this.width / 2) - 10;
        topCorner = calculateTopLeftCorner(packListWidth, (int) 30);
        bottomCorner = calculateBottomRightCorner(packListWidth, (int) 30);

        for (int i = 0; i < this.items.size(); i++) {
            PackSelectionItem widget = items.get(i);
            widget.setY((int) ((topCorner.y) + (i * 30) + scrollOffset) + i + 1);
            widget.setX(5);
            widget.setWidth((int) packListWidth);
            widget.height = 30;
            widget.render(matrices, mouseX, mouseY, delta);
        }

        for (var element : this.children()) {
            if (element instanceof Drawable drawable && !(element instanceof PackSelectionItem))
                drawable.render(matrices, mouseX, mouseY, delta);
        }

        this.textRenderer.draw(matrices, Text.literal("Codex Setup"), packListWidth + 10, 15 + 10 - this.textRenderer.fontHeight, CodexColors.WHITE);
        StringVisitable descriptionText = Text.literal("Please select codex packs to download on the left. Packs highlighted red are required because a mod was detected that needs it - or it is part of vanilla.");
        this.textRenderer.drawTrimmed(descriptionText, (int) packListWidth + 10, 15 + 20, this.width / 2, CodexColors.WHITE);

        Tooltip.renderAll(this, matrices);
    }

    public record CodexPack(String title, String license, String languageCode, Identifier iconTexture, int texWidth, int texHeight,
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
