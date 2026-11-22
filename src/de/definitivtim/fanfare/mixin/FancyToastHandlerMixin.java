// © 2026 DefinitivTim
package de.definitivtim.fanfare.mixin;

import de.definitivtim.fanfare.FancyBridge;
import net.bivrik.fancytoasts.client.config.data.ToastConfigData;
import net.bivrik.fancytoasts.platform.utility.QuestType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Fancy Toasts 1.5.1: the sound of a quest toast follows the kind chosen by the quest tag. */
@Pseudo
@Mixin(targets = "net.bivrik.fancytoasts.platform.utility.ToastHandler", remap = false)
public abstract class FancyToastHandlerMixin {

    @Redirect(method = "handleFTBQuestsToast", at = @At(value = "INVOKE",
            target = "Lnet/bivrik/fancytoasts/client/config/data/ToastConfigData;getSoundIdByQuestType(Lnet/bivrik/fancytoasts/platform/utility/QuestType;)Lnet/minecraft/resources/ResourceLocation;",
            remap = false), remap = false, require = 0)
    private ResourceLocation dt$sound(ToastConfigData data, QuestType questType) {
        return FancyBridge.sound(data, questType);
    }
}
