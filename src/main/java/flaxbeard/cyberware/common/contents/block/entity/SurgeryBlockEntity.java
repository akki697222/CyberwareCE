package flaxbeard.cyberware.common.contents.block.entity;

import flaxbeard.cyberware.common.CyberwareBlockEntities;
import flaxbeard.cyberware.common.CyberwareBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SurgeryBlockEntity extends BlockEntity implements BlockEntityTicker<SurgeryBlockEntity>, Container {
    public SurgeryBlockEntity(BlockPos pos, BlockState blockState) {
        super(CyberwareBlockEntities.SURGERY.get(), pos, blockState);
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        return null;
    }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return null;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {

    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {

    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, SurgeryBlockEntity surgeryBlockEntity) {

    }
}
