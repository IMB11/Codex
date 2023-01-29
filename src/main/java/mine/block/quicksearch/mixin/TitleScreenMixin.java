package mine.block.quicksearch.mixin;

import mine.block.quicksearch.CodexUtils;
import mine.block.quicksearch.client.QuicksearchClient;
import mine.block.quicksearch.ui.setup.SetupScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Files;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At("RETURN"), cancellable = false)
    public void $codex_titleScreenInit(CallbackInfo ci) {
        if(!Files.exists(CodexUtils.CODEX_CACHE_PATH.resolve("codex.lock"))) {
            MinecraftClient.getInstance().setScreen(new SetupScreen());
        }
    }
}
