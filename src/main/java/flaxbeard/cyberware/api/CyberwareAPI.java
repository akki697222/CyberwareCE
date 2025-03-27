package flaxbeard.cyberware.api;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.item.CyberwareData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.CyberwareAttachments;
import flaxbeard.cyberware.common.CyberwareComponents;
import net.minecraft.resources.ResourceLocation;
import flaxbeard.cyberware.api.item.ICyberware.Quality;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CyberwareAPI {
    /**
     * Store any functional data of your Cyberware in NBT under this tag, which will be cleared when new items are added or removed
     * to ensure stacking and such works
     */
    public static final String DATA_TAG = "cyberwareFunctionData";

    public static final String QUALITY_TAG = "cyberwareQuality";

    /**
     * Quality for Cyberware scavenged from mobs
     */
    public static final Quality QUALITY_SCAVENGED = new Quality("cyberware.quality.scavenged", "cyberware.quality.scavenged.name_modifier", "scavenged");

    /**
     * Quality for Cyberware built at the Engineering Table
     */
    public static final Quality QUALITY_MANUFACTURED = new Quality("cyberware.quality.manufactured");

    private static final CyberwareData DEFAULT_DATA = new CyberwareData();

    public static Map<ItemStack, ICyberware> linkedWare = new HashMap<>();

    @Nonnull
    public static CyberwareData getCyberwareData(@Nonnull ItemStack stack) {
        CyberwareData data = stack.get(CyberwareComponents.CYBERWARE_DATA.get());
        if (data == null) {
            data = DEFAULT_DATA;
            stack.set(CyberwareComponents.CYBERWARE_DATA.get(), data);
        }
        return data;
    }

    public static boolean areCyberwareStacksEqual(@Nonnull ItemStack one, @Nonnull ItemStack two) {
        if (one.isEmpty() || two.isEmpty()) return false;

        ItemStack sanitized1 = sanitize(one.copy());
        ItemStack sanitized2 = sanitize(two.copy());
        return ItemStack.isSameItemSameComponents(sanitized1, sanitized2);
    }

    public static ItemStack sanitize(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.remove(CyberwareComponents.CYBERWARE_DATA);
        }
        return stack;
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

    public static void updateData(Entity target) {
        if (checkServer(target.level())) {
            //@TODO this
        }
    }

    public static boolean checkServer(Level level) {
        return level == null || !level.isClientSide();
    }

    public static void setCyberwareUserData(LivingEntity entity, CyberwareUserData data) {
        entity.setData(CyberwareAttachments.CYBERWARE_USER_DATA, data);
    }
}
