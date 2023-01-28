package mine.block.quicksearch.client;

import mine.block.quicksearch.config.CodexConfig;
import mine.block.quicksearch.math.*;
import mine.block.quicksearch.search.SearchManager;
import mine.block.quicksearch.ui.QuicksearchUI;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.screen.NamedScreenHandlerFactory;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static mine.block.quicksearch.math.FunctionRegistry.registerFunction;

@Environment(EnvType.CLIENT)
public class QuicksearchClient implements ClientModInitializer {
    private static final KeyBinding OPEN_QUICKSEARCH_KEY = new KeyBinding("open_quicksearch", GLFW.GLFW_KEY_N, "quicksearch");
    public static Consumer<Item> OPEN_RECIPE_SUPPLIER;
    public static CodexConfig CONFIG = CodexConfig.createAndLoad();

    public static boolean HAS_EXISTING_JSONS = Files.exists(FabricLoader.getInstance().getGameDir().resolve("/codex-storage"));

    @Override
    public void onInitializeClient() {

        KeyBindingHelper.registerKeyBinding(OPEN_QUICKSEARCH_KEY);

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if(OPEN_QUICKSEARCH_KEY.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new QuicksearchUI());
            }
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
