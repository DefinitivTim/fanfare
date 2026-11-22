package net.neoforged.neoforge.client.event;
import net.minecraft.client.gui.screens.Screen;
public abstract class ScreenEvent extends net.neoforged.bus.api.Event {
    public static class Opening extends ScreenEvent {
        public Screen getCurrentScreen() { return null; }
        public Screen getNewScreen() { return null; }
        public void setNewScreen(Screen newScreen) {}
    }
}
