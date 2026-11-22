package net.minecraft.client.gui.components.toasts;
public interface Toast {
  Visibility render(net.minecraft.client.gui.GuiGraphics graphics, ToastComponent component, long timeSinceLastVisible);
  enum Visibility { SHOW, HIDE }
}
