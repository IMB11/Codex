package mine.block.quicksearch.compat;

import dev.emi.emi.EmiRecipes;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.VanillaPlugin;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.RecipeScreen;
import mine.block.quicksearch.client.QuicksearchClient;
import net.minecraft.client.MinecraftClient;

import java.util.Objects;

public class QuicksearchEMI implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        QuicksearchClient.OPEN_RECIPE_SUPPLIER = (item) -> {
            EmiApi.displayRecipes(EmiStack.of(item.getDefaultStack()));
        };
    }
}
