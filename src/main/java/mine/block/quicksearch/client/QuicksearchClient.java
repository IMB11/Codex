package mine.block.quicksearch.client;

import com.google.gson.JsonObject;
import mine.block.quicksearch.CodexUtils;
import mine.block.quicksearch.config.CodexConfig;
import mine.block.quicksearch.math.*;
import mine.block.quicksearch.search.SearchManager;
import mine.block.quicksearch.ui.QuicksearchUI;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static mine.block.quicksearch.math.FunctionRegistry.registerFunction;

@Environment(EnvType.CLIENT)
public class QuicksearchClient implements ClientModInitializer {
    private static final KeyBinding OPEN_QUICKSEARCH_KEY = new KeyBinding("open_quicksearch", GLFW.GLFW_KEY_N, "quicksearch");
    public static Consumer<Item> OPEN_RECIPE_SUPPLIER;
    public static CodexConfig CONFIG = CodexConfig.createAndLoad();
    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("codex:lang");

    public static boolean HAS_EXISTING_JSONS = Files.exists(FabricLoader.getInstance().getGameDir().resolve("/codex-storage"));

    @Override
    public void onInitializeClient() {

        KeyBindingHelper.registerKeyBinding(OPEN_QUICKSEARCH_KEY);

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if(OPEN_QUICKSEARCH_KEY.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new QuicksearchUI());
            }
        });

        RRPCallback.BEFORE_USER.register(resources -> {
            var translations = CodexUtils.getCodexLanguageFiles();
            translations.forEach((lang, entries) -> {
                JLang langFile = new JLang();
                entries.asMap().forEach((key, val) -> {
                    langFile.entry(key, val.getAsString());
                });
                RESOURCE_PACK.addLang(new Identifier("codex", lang), langFile);
            });
            resources.add(RESOURCE_PACK);
        });

        SearchManager.initialize();

        registerFunction("stacksof", new ToStackFunction());
        registerFunction("itemsof", new FromStackFunction());
        registerFunction("shulkersof", new StackToShulkerFunction());
        registerFunction("stacksin", new ShulkerToStackFunction());
        registerFunction("secsin", new SecToTickFunction());
        registerFunction("ticksin", new TickToSecFunction());
    }
}
