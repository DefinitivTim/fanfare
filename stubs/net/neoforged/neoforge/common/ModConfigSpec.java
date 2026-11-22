package net.neoforged.neoforge.common;
public class ModConfigSpec implements net.neoforged.fml.config.IConfigSpec {
  public static class Builder {
    public Builder() { }
    public Builder comment(String c) { return this; }
    public Builder push(String p) { return this; }
    public Builder pop() { return this; }
    public BooleanValue define(String path, boolean def) { return null; }
    public IntValue defineInRange(String path, int def, int min, int max) { return null; }
    public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V def) { return null; }
    public ModConfigSpec build() { return null; }
  }
  public static class ConfigValue<T> { public T get() { return null; } }
  public static class BooleanValue extends ConfigValue<Boolean> { }
  public static class IntValue extends ConfigValue<Integer> { }
  public static class EnumValue<T extends Enum<T>> extends ConfigValue<T> { }
}
