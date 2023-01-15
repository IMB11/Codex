package mine.block.quicksearch.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.fabric.JustEnoughItems;
import mine.block.quicksearch.client.QuicksearchClient;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.lang.annotation.Annotation;

@JeiPlugin
public class QuicksearchJEI implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return new Identifier("mineblock11", "quicksearch");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        QuicksearchClient.OPEN_RECIPE_SUPPLIER = (item) -> {
            jeiRuntime.getRecipesGui().show(jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.RENDER_ONLY, jeiRuntime.getJeiHelpers().getIngredientManager().getIngredientType(item), item));
        };
    }
}
