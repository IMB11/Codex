package mine.block.codex.compat;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import mine.block.codex.client.CodexClient;

public class QuicksearchEMI implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        CodexClient.OPEN_RECIPE_SUPPLIER = (item) -> {
            EmiApi.displayRecipes(EmiStack.of(item.getDefaultStack()));
        };
    }
}
