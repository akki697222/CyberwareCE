package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.contents.block.SurgeryBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CyberwareBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(Cyberware.MOD_ID);

    public static final DeferredHolder<Block, SurgeryBlock> SURGERY = REGISTER.register("surgery", SurgeryBlock::new);
}
