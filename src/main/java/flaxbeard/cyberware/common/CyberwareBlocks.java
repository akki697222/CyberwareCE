package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.contents.block.SurgeryBlock;
import flaxbeard.cyberware.common.contents.block.SurgeryChamberBlock;
import flaxbeard.cyberware.common.contents.item.CyberwareItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CyberwareBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(Cyberware.MOD_ID);

    public static final DeferredHolder<Block, SurgeryBlock> SURGERY = registerBlock("surgery", SurgeryBlock::new);
    public static final DeferredHolder<Block, SurgeryChamberBlock> SURGERY_CHAMBER = registerBlock("surgery_chamber", SurgeryChamberBlock::new);

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        DeferredHolder<Block, T> blockHolder = REGISTER.register(name, block);
        CyberwareItems.registerBlockItem(blockHolder);
        return blockHolder;
    }
}
