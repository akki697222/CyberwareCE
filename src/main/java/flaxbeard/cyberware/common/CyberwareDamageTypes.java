package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CyberwareDamageTypes {
    public static final ResourceKey<DamageType> BRAINLESS = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "brainless")
    );
    public static final ResourceKey<DamageType> HEARTLESS = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "heartless")
    );
    public static final ResourceKey<DamageType> SURGERY = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "surgery")
    );
    public static final ResourceKey<DamageType> SPINELESS = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "spineless")
    );
    public static final ResourceKey<DamageType> NOMUSCLES = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "nomuscles")
    );
    public static final ResourceKey<DamageType> NOESSENCE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "noessence")
    );
    public static final ResourceKey<DamageType> LOWESSENCE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Cyberware.MOD_ID, "lowessence")
    );
    public static DamageSource brainless(Level level) {
        return level.damageSources().source(BRAINLESS);
    }

    public static DamageSource heartless(Level level) {
        return level.damageSources().source(HEARTLESS);
    }

    public static DamageSource surgery(Level level) {
        return level.damageSources().source(SURGERY);
    }

    public static DamageSource spineless(Level level) {
        return level.damageSources().source(SPINELESS);
    }

    public static DamageSource nomuscles(Level level) {
        return level.damageSources().source(NOMUSCLES);
    }

    public static DamageSource noessence(Level level) {
        return level.damageSources().source(NOESSENCE);
    }

    public static DamageSource lowessence(Level level) {
        return level.damageSources().source(LOWESSENCE);
    }
}
