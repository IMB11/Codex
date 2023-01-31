package mine.block.codex.ui.setup;

import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import mine.block.codex.CodexUtils;
import mine.block.codex.client.CodexClient;
import mine.block.codex.ui.CodexColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DownloadingScreen extends SpruceScreen {
    private final ArrayList<SetupScreen.CodexPack> packsToDownload;
    private float progress = 0.00f;
    private boolean pending = false;
    private String currentTask = "Waiting...";
    private float percentageDone = 0.0F;
    private float deltaTime;

    protected DownloadingScreen(ArrayList<SetupScreen.CodexPack> packsToDownload) {
        super(Text.empty());
        this.packsToDownload = packsToDownload;
    }

    @Override
    protected void init() {
        super.init();
        if(pending) return;
        // x. Download all description etc. files.
        // x+1. Merge all files together by language.
        // x+2. Save merged to descriptions.merged.lang.json
        // Trigger Resources Reload - voila!
        // TODO: Handle failures - force mc exit by user.
        float tasksSize = (packsToDownload.size()) + 2;
        Thread mergingTask = new Thread(() -> {
            pending = true;
            float tasksDone = 0;
            ArrayListMultimap<String, JsonObject> descriptionsByLang = ArrayListMultimap.create();
            for (SetupScreen.CodexPack codexPack : this.packsToDownload) {
                currentTask = "Downloading " + codexPack.descriptions();
                String descriptionUrl = CodexClient.CONFIG.baseUrl() + codexPack.descriptions();
                JsonObject descriptions = CodexUtils.readJsonFromUrl(descriptionUrl).getAsJsonObject();
                descriptionsByLang.put(codexPack.languageCode(), descriptions);
                tasksDone += 1;
                this.progress = tasksDone / tasksSize;
            }
            HashMap<String, JsonObject> mergedDescriptions = new HashMap<>();
            currentTask = "Merging translations";
            descriptionsByLang.asMap().forEach((languageCode, descriptions) -> {
                JsonObject merged = new JsonObject();
                for (JsonObject description : descriptions) {
                    for (Map.Entry<String, JsonElement> stringJsonElementEntry : description.entrySet()) {
                        merged.add(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
                    }
                }
                mergedDescriptions.put(languageCode, merged);
            });
            tasksDone += 1;
            this.progress = tasksDone / tasksSize;
            currentTask = "Saving translations";
            if(!Files.exists(CodexUtils.CODEX_CACHE_PATH)) {
                try {
                    Files.createDirectory(CodexUtils.CODEX_CACHE_PATH);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            for (Map.Entry<String, JsonObject> stringJsonObjectEntry : mergedDescriptions.entrySet()) {
                String contents = CodexUtils.GSON.toJson(stringJsonObjectEntry.getValue());
                try {
                    Files.writeString(CodexUtils.CODEX_CACHE_PATH.resolve(stringJsonObjectEntry.getKey() + ".merged.json"), contents, StandardCharsets.UTF_16);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            tasksDone += 1;
            this.progress = tasksDone / tasksSize;
            currentTask = "Locking cache.";
            try {
                Files.writeString(CodexUtils.CODEX_CACHE_PATH.resolve("codex.lock"), "");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                // Loading resources too quickly after lock causes loop in mixin.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                MinecraftClient.getInstance().reloadResources().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().setScreen(new TitleScreen());
            });
        });
        mergingTask.start();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, this.width, this.height, CodexColors.CODEX_BG);
        this.percentageDone = MathHelper.clamp(this.percentageDone * 0.95F + this.progress * 0.050000012F, 0.0F, 1.0F);

        float fillSize = MathHelper.ceil((this.width - 22) * this.percentageDone);
        fill(matrices, 5, (this.height / 2) - 32 - 5, this.width - 5, (this.height / 2) - 5, CodexColors.CODEX_ELEVATED);

        if (!(this.percentageDone <= 0)) {
            fill(matrices, 10, (this.height / 2) - 32, (int) fillSize, (this.height / 2) - 10, CodexColors.CODEX_ACCENT_A);
        }

        this.textRenderer.drawWithShadow(matrices, Text.literal("Do not close Minecraft."), (this.width / 2f) - (this.textRenderer.getWidth(Text.literal("Do not close Minecraft.")) / 2f), (this.height / 2f) - 64, Formatting.RED.getColorValue());
        this.textRenderer.drawWithShadow(matrices, this.currentTask, (this.width / 2f) - (this.textRenderer.getWidth(this.currentTask) / 2f), (this.height / 2f), CodexColors.DARK_GRAY);

        for (var element : this.children()) {
            if (element instanceof Drawable drawable)
                drawable.render(matrices, mouseX, mouseY, delta);
        }
    }
}
