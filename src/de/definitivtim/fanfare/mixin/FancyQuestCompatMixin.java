// © 2026 DefinitivTim
package de.definitivtim.fanfare.mixin;

import de.definitivtim.fanfare.FancyBridge;
import net.bivrik.fancytoasts.platform.utility.AdvancementDisplay;
import net.minecraft.client.gui.components.toasts.Toast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Fancy Toasts 1.5.1: after it has built the data for a quest toast, apply the kind chosen by the quest tag. */
@Pseudo
@Mixin(targets = "net.bivrik.fancytoasts.compat.FTBQuestsCompat", remap = false)
public abstract class FancyQuestCompatMixin {

    @Inject(method = "getDisplayInfo", at = @At("RETURN"), remap = false, require = 0)
    private static void dt$restyle(Toast toast, CallbackInfoReturnable<AdvancementDisplay> cir) {
        FancyBridge.restyle(toast, cir.getReturnValue());
    }
}
