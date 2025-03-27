package flaxbeard.cyberware.common.contents.block.entity;

import flaxbeard.cyberware.common.CyberwareBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SurgeryChamberBlockEntity extends BlockEntity {
    public SurgeryChamberBlockEntity(BlockPos pos, BlockState blockState) {
        super(CyberwareBlockEntities.SURGERY_CHAMBER.get(), pos, blockState);
    }
}
