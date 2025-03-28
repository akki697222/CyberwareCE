package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUserData;
import flaxbeard.cyberware.client.CyberwareClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record CyberwareSyncPacket(int entityId, CompoundTag data) implements CustomPacketPayload {
    public static final Type<CyberwareSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cyberware", "sync_cyberware"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CyberwareSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CyberwareSyncPacket::entityId,
            ByteBufCodecs.COMPOUND_TAG, CyberwareSyncPacket::data,
            CyberwareSyncPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    //@TODO this
    public static void handle(CyberwareSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                var level = context.player().level();
                Entity entity = level.getEntity(packet.entityId());
                if (entity instanceof LivingEntity livingEntity) {
                    CyberwareUserData userData = CyberwareUserData.CODEC.parse(NbtOps.INSTANCE, packet.data())
                            .result()
                            .orElse(new CyberwareUserData());
                    CyberwareAPI.setCyberwareUserData(livingEntity, userData);
                    Cyberware.logger.info("Data Updated!");
                }
            }
        });
    }
}