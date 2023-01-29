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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.function.Consumer;

import static mine.block.quicksearch.math.FunctionRegistry.registerFunction;

@Environment(EnvType.CLIENT)
public class QuicksearchClient implements ClientModInitializer {
    private static final KeyBinding OPEN_QUICKSEARCH_KEY = new KeyBinding("open_quicksearch", GLFW.GLFW_KEY_N, "quicksearch");
    public static Consumer<Item> OPEN_RECIPE_SUPPLIER;
    public static CodexConfig CONFIG = CodexConfig.createAndLoad();
    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("codex:lang");
    private static HashMap<String, JsonObject> LANGUAGE_CACHE_COMPARABLE = new HashMap<>();

    @Override
    public void onInitializeClient() {

        KeyBindingHelper.registerKeyBinding(OPEN_QUICKSEARCH_KEY);

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if(OPEN_QUICKSEARCH_KEY.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new QuicksearchUI());
            }
        });

        RRPCallback.AFTER_VANILLA.register(resources -> {
            SearchManager.refresh();
            var translations = CodexUtils.getCodexLanguageFiles();
            if(translations != LANGUAGE_CACHE_COMPARABLE) {
                LANGUAGE_CACHE_COMPARABLE = translations;
                translations.forEach((lang, entries) -> {
                    JLang langFile = new JLang();
                    entries.asMap().forEach((key, val) -> langFile.entry(key, val.getAsString()));
                    RESOURCE_PACK.addLang(new Identifier("codex", lang), langFile);
                });
            }

            resources.add(RESOURCE_PACK);
        });

        registerFunction("stacksof", new ToStackFunction());
        registerFunction("itemsof", new FromStackFunction());
        registerFunction("shulkersof", new StackToShulkerFunction());
        registerFunction("stacksin", new ShulkerToStackFunction());
        registerFunction("secsin", new SecToTickFunction());
        registerFunction("ticksin", new TickToSecFunction());
    }
}
