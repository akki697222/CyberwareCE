package flaxbeard.cyberware.common.datagen;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.CyberwareDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTagProvider extends DamageTypeTagsProvider {
    public DamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Cyberware.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(DamageTypeTags.BYPASSES_ARMOR)
                .addOptionalTag(CyberwareDamageTypes.BRAINLESS.location())
                .addOptionalTag(CyberwareDamageTypes.HEARTLESS.location())
                .addOptionalTag(CyberwareDamageTypes.SURGERY.location())
                .addOptionalTag(CyberwareDamageTypes.SPINELESS.location())
                .addOptionalTag(CyberwareDamageTypes.NOMUSCLES.location())
                .addOptionalTag(CyberwareDamageTypes.NOESSENCE.location())
                .addOptionalTag(CyberwareDamageTypes.LOWESSENCE.location());

        tag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .addOptionalTag(CyberwareDamageTypes.BRAINLESS.location())
                .addOptionalTag(CyberwareDamageTypes.HEARTLESS.location())
                .addOptionalTag(CyberwareDamageTypes.SPINELESS.location())
                .addOptionalTag(CyberwareDamageTypes.NOMUSCLES.location())
                .addOptionalTag(CyberwareDamageTypes.NOESSENCE.location())
                .addOptionalTag(CyberwareDamageTypes.LOWESSENCE.location());
    }
}
