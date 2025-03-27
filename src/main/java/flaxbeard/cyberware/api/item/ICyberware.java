package flaxbeard.cyberware.api.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface ICyberware {
    BodyRegion getBodyRegion();
    NonNullList<ItemStack> requiredCyberwares();
    boolean isIncompatible(ItemStack comparison);
    boolean isEssential();
    List<String> getInfo();
    int getEnergyCapacity();
    int getMaxInstalls();

    boolean canHoldQuality(Quality quality);

    class Quality {
        private static Map<String, Quality> mapping = new HashMap<>();
        public static List<Quality> qualities = new ArrayList<>();
        private final String unlocalizedName;
        private final String nameModifier;
        private final String spriteSuffix;

        public Quality(String unlocalizedName) {
            this(unlocalizedName, null, null);
        }

        public Quality(String unlocalizedName, String nameModifier, String spriteSuffix)
        {
            this.unlocalizedName = unlocalizedName;
            this.nameModifier = nameModifier;
            this.spriteSuffix = spriteSuffix;
            mapping.put(unlocalizedName, this);
            qualities.add(this);
        }

        public String getUnlocalizedName()
        {
            return unlocalizedName;
        }

        public static Quality getQualityFromString(String name)
        {
            if (mapping.containsKey(name))
            {
                return mapping.get(name);
            }
            return null;
        }

        public String getNameModifier()
        {
            return nameModifier;
        }

        public String getSpriteSuffix()
        {
            return spriteSuffix;
        }

        public static final Codec<Quality> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("unlocalized_name").forGetter(d -> d.unlocalizedName),
                    Codec.STRING.fieldOf("name_modifier").forGetter(d -> d.nameModifier),
                    Codec.STRING.fieldOf("sprite_suffix").forGetter(d -> d.spriteSuffix)
            ).apply(instance, Quality::new)
        );
    }

    enum BodyRegion implements StringRepresentable {
        EYES(0, "eyes"),
        CRANIUM(1, "cranium"),
        HEART(2, "heart"),
        LUNGS(3, "lungs"),
        LOWER_ORGANS(4, "lower_organs"),
        SKIN(5, "skin"),
        MUSCLE(6, "muscle"),
        BONE(7, "bone"),
        ARM(8, "arm", true, true),
        HAND(9, "hand", true, false),
        LEG(10, "leg", true, true),
        FOOT(11, "foot", true, false);

        private final int slotNumber;
        private final String name;
        private final boolean sidedSlot;
        private final boolean hasEssential;

        BodyRegion(int slotNumber, String name, boolean sidedSlot, boolean hasEssential) {
            this.slotNumber = slotNumber;
            this.name = name;
            this.sidedSlot = sidedSlot;
            this.hasEssential = hasEssential;
        }

        BodyRegion(int slotNumber, String name) {
            this(slotNumber, name, false, true);
        }

        public int getSlotNumber() {
            return slotNumber;
        }

        public static BodyRegion valueOf(int slotNumber) {
            for (BodyRegion slot : values()) {
                if (slot.getSlotNumber() == slotNumber) {
                    return slot;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public boolean isSided() {
            return sidedSlot;
        }

        public boolean hasEssential() {
            return hasEssential;
        }

        public static final Codec<BodyRegion> CODEC = Codec.STRING.xmap(
                name -> BodyRegion.valueOf(name.toUpperCase()),
                BodyRegion::name
        );

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }

    void onAdded(LivingEntity livingEntity);
    void onRemoved(LivingEntity livingEntity);

    interface ISidedLimb {
        Side getSide();

        enum Side {
            LEFT,
            RIGHT;
        }
    }

    int getEssenceCost();
}
