// © 2026 DefinitivTim
package de.definitivtim.fanfare;

import dev.ftb.mods.ftblibrary.icon.Icon;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

/** Completion toast in the look of a vanilla advancement toast, in one of three kinds. */
public final class QuestToast implements Toast {
    private static final ResourceLocation BACKGROUND = ResourceLocation.withDefaultNamespace("toast/advancement");

    private final ToastKind kind;
    private final Component title;
    private final Component name;
    private final Icon icon;
    private final ResourceLocation frame;
    private boolean soundPlayed;
    private boolean iconFailed;

    public QuestToast(ToastKind kind, Component title, Component name, Icon icon) {
        this.kind = kind;
        this.title = title;
        this.name = name;
        this.icon = icon;
        this.frame = ResourceLocation.withDefaultNamespace(kind.frameSprite);
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent component, long time) {
        Minecraft mc = component.getMinecraft();
        Font font = mc.font;
        graphics.blitSprite(BACKGROUND, 0, 0, 160, 32);

        List<FormattedCharSequence> lines = font.split(this.name, 125);
        if (lines.size() <= 1 || time < 1500L) {
            graphics.drawString(font, this.title, 30, 7, this.kind.titleColor, false);
            if (!lines.isEmpty()) {
                graphics.drawString(font, lines.get(0), 30, 18, 0xFFFFFFFF, false);
            }
        } else {
            graphics.drawString(font, lines.get(0), 30, 7, 0xFFFFFFFF, false);
            graphics.drawString(font, lines.get(1), 30, 18, 0xFFFFFFFF, false);
        }

        if (!this.soundPlayed) {
            this.soundPlayed = true;
            playSound(mc);
        }

        boolean framed = Settings.iconFrame();
        if (framed) {
            graphics.blitSprite(this.frame, 3, 3, 26, 26);
        }
        if (this.icon != null && !this.iconFailed) {
            try {
                this.icon.draw(graphics, 8, 8, 16, 16);
            } catch (Throwable t) {
                this.iconFailed = true;
            }
        }

        double shown = Settings.displaySeconds() * 1000.0 * component.getNotificationDisplayTimeMultiplier();
        return time >= shown ? Visibility.HIDE : Visibility.SHOW;
    }

    private void playSound(Minecraft mc) {
        if (!Settings.sounds()) {
            return;
        }
        try {
            if (this.kind == ToastKind.CHALLENGE) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
            } else if (this.kind == ToastKind.GOAL) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.0F, 0.6F));
            }
        } catch (Throwable t) {
            // a missing sound must never break the toast
        }
    }
}
