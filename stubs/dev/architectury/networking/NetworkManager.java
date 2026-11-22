package dev.architectury.networking;
public class NetworkManager {
    public static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void sendToServer(T payload) {}
    public interface PacketContext {}
}
