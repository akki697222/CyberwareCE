package flaxbeard.cyberware.api.hud;

import flaxbeard.cyberware.api.CyberwareAPI;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HudColorPacketPayload(int color) implements CustomPacketPayload {
    public static final Type<HudColorPacketPayload> TYPE = new Type<>(CyberwareAPI.modResource("hud_color"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HudColorPacketPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, payload) -> buffer.writeInt(payload.color()),
                    (buffer) -> new HudColorPacketPayload(buffer.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}