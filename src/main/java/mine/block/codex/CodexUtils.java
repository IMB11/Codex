package mine.block.codex;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class CodexUtils {

    public static final Path CODEX_CACHE_PATH = Path.of(String.valueOf(FabricLoader.getInstance().getGameDir()), "codex");
    public static Gson GSON = new Gson();
    public static JsonElement readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            return GSON.fromJson(rd, JsonElement.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final HashMap<String, JsonObject> CACHED_LANGUAGE_CODES = new HashMap<>();

    public static HashMap<String, JsonObject> getCodexLanguageFiles() {
        if(!Files.exists(CODEX_CACHE_PATH) || !CACHED_LANGUAGE_CODES.isEmpty()) {
            return CACHED_LANGUAGE_CODES;
        }
        File codex_cache_dir = new File(CODEX_CACHE_PATH.toUri());
        for (File file : codex_cache_dir.listFiles()) {
            if(file.getName().contains(".lock")) continue;
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_16));
                JsonObject obj = GSON.fromJson(rd, JsonObject.class);
                CACHED_LANGUAGE_CODES.put(file.getName().replace(".merged.json", ""), obj);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return CACHED_LANGUAGE_CODES;
    }

    public static float lerp(float delta, float start, float end) {
        return (1 - delta) * start + delta * end;
    }
}
