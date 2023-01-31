package mine.block.codex.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import mine.block.codex.client.CodexClient;
import net.minecraft.util.Identifier;

@JeiPlugin
public class QuicksearchJEI implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return new Identifier("mineblock11", "quicksearch");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        CodexClient.OPEN_RECIPE_SUPPLIER = (item) -> {
            jeiRuntime.getRecipesGui().show(jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.RENDER_ONLY, jeiRuntime.getJeiHelpers().getIngredientManager().getIngredientType(item), item));
        };
    }
}
