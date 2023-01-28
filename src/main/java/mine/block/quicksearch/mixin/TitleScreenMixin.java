package mine.block.quicksearch.mixin;

import mine.block.quicksearch.client.QuicksearchClient;
import mine.block.quicksearch.ui.setup.SetupScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At("RETURN"), cancellable = false)
    public void $codex_titleScreenInit(CallbackInfo ci) {
        MinecraftClient.getInstance().setScreen(new SetupScreen());
    }
}
