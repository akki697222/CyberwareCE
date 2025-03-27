package flaxbeard.cyberware.common.datagen;

import flaxbeard.cyberware.Cyberware;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Cyberware.MOD_ID, existingFileHelper);
    }

    private enum ToolType {
        STONE,
        IRON,
        DIAMOND
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

    }

    private void pickaxeMineable(Block block, @Nullable ToolType type) {
        TagKey<Block> tags = null;

        if (type != null) {
            switch (type) {
                case STONE -> tags = BlockTags.NEEDS_STONE_TOOL;
                case IRON -> tags = BlockTags.NEEDS_IRON_TOOL;
                case DIAMOND -> tags = BlockTags.NEEDS_DIAMOND_TOOL;
            }
            tag(tags).add(block);
        }

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
    }
}
