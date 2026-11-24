package net.minecraft.network;
public class FriendlyByteBuf {
  public FriendlyByteBuf writeVarLong(long value) { return this; }
  public long readVarLong() { return 0L; }
  public FriendlyByteBuf writeBoolean(boolean value) { return this; }
  public boolean readBoolean() { return false; }
}
