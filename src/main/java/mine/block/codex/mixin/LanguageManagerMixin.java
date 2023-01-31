package mine.block.codex.mixin;

import mine.block.codex.search.SearchManager;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin {
    @Inject(method = "setLanguage", at = @At("RETURN"), cancellable = false)
    private void $codex_languageChangedMixin(LanguageDefinition language, CallbackInfo ci) {
        SearchManager.refresh();
    }
}
