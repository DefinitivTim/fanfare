package net.neoforged.fml;
public abstract class ModContainer {
  public void registerConfig(net.neoforged.fml.config.ModConfig.Type type, net.neoforged.fml.config.IConfigSpec spec) { }
  public <T> void registerExtensionPoint(Class<T> point, java.util.function.Supplier<T> extension) { }
}
