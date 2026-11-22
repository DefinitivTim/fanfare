package net.neoforged.bus.api;
public interface IEventBus { <T extends Event> void addListener(Class<T> type, java.util.function.Consumer<T> consumer); }
