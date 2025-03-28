package flaxbeard.cyberware.api;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.CyberwareAttachments;
import flaxbeard.cyberware.common.network.CyberwareSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CyberwareAPI {
    public static Map<ItemStack, ICyberware> linkedWare = new HashMap<>();

    public static boolean areCyberwareStacksEqual(@Nonnull ItemStack one, @Nonnull ItemStack two) {
        return ItemStack.isSameItemSameComponents(one, two);
    }

    public static ICyberware getCyberware(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ICyberware cyberware) {
                return cyberware;
            }
        }

        throw new IllegalArgumentException("Cannot get cyberware from non-cyberware item!");
    }

    public static boolean isCyberware(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof ICyberware || getLinkedWare(stack) != null);
    }

    @Nullable
    private static ICyberware getLinkedWare(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) return null;

        return getWareFromKey(stack);
    }

    @Nullable
    private static ICyberware getWareFromKey(@Nonnull ItemStack key) {
        for (Map.Entry<ItemStack, ICyberware> entry : linkedWare.entrySet()) {
            ItemStack entryKey = entry.getKey();
            if (ItemStack.isSameItemSameComponents(key, entryKey)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static ResourceLocation modResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, path);
    }

    @Nullable
    public static ICyberwareUserData getCyberwareUserData(@Nullable Entity entity) {
        if (entity == null) return null;
        return entity.getData(CyberwareAttachments.CYBERWARE_USER_DATA);
    }

    public static void updateData(Entity targetEntity) {
        if (checkServer(targetEntity.level())) {
            ServerLevel world = (ServerLevel) targetEntity.level();

            CyberwareUserData cyberwareUserData = (CyberwareUserData) getCyberwareUserData(targetEntity);

            if (targetEntity instanceof LivingEntity livingEntity) {
                setCyberwareUserData(livingEntity, cyberwareUserData);
                Cyberware.logger.debug("Updated server-side CyberwareUserData for {}", targetEntity.getName().getString());
            }

            CompoundTag tagCompound = cyberwareUserData.serializeNBT(null);

            if (targetEntity instanceof ServerPlayer targetPlayer) {
                PacketDistributor.sendToPlayer(targetPlayer, new CyberwareSyncPacket(targetEntity.getId(), tagCompound));
                Cyberware.logger.info("Sent data for player {} to that player's client", targetPlayer.getName().getString());
            }

            List<ServerPlayer> trackingPlayers = world.getPlayers(player ->
                    player != targetEntity &&
                            player.distanceToSqr(targetEntity) < 128 * 128
            );

            for (ServerPlayer trackingPlayer : trackingPlayers) {
                PacketDistributor.sendToPlayer(trackingPlayer, new CyberwareSyncPacket(targetEntity.getId(), tagCompound));
                if (targetEntity instanceof ServerPlayer) {
                    Cyberware.logger.info("Sent data for player {} to player {}",
                            targetEntity.getName().getString(), trackingPlayer.getName().getString());
                }
            }
        }
    }

    public static boolean checkServer(Level level) {
        return level == null || !level.isClientSide();
    }

    public static void setCyberwareUserData(LivingEntity entity, CyberwareUserData data) {
        entity.setData(CyberwareAttachments.CYBERWARE_USER_DATA, data);
    }
}
