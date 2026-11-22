package net.minecraft.network.chat;
public interface Component extends FormattedText {
    default String getString() { return null; }
    static MutableComponent literal(String text) { return null; }
    static MutableComponent translatable(String key) { return null; }
}
