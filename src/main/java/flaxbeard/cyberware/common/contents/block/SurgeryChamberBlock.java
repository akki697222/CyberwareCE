package flaxbeard.cyberware.common.contents.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.util.VoxelShapeUtil;
import flaxbeard.cyberware.common.contents.block.entity.SurgeryBlockEntity;
import flaxbeard.cyberware.common.contents.block.entity.SurgeryChamberBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SurgeryChamberBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<SurgeryChamberBlock> CODEC = MapCodec.unit(SurgeryChamberBlock::new);

    public static final BooleanProperty SLAVE = BooleanProperty.create("slave");
    public static final BooleanProperty DOOR = BooleanProperty.create("door");

    public SurgeryChamberBlock() {
        super(Properties.of()
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .mapColor(MapColor.METAL));

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SLAVE, false)
                .setValue(DOOR, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SLAVE, DOOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level.getBlockState(pos.above()).canBeReplaced()) {
            return this.defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(SLAVE, false)
                    .setValue(DOOR, false);
        }
        return null;
    }

    @Override
    protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (state.getValue(SLAVE)) return;

        BlockPos slavePos = pos.above();
        BlockState slaveState = this.defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(SLAVE, true)
                .setValue(DOOR, false);
        if (!slavePos.equals(pos)) {
            level.setBlock(slavePos, slaveState, 3);
        }
    }

    private VoxelShape generateShape(Direction direction, boolean isSlave) {
        VoxelShape shape = Shapes.empty();
        if (isSlave) {
            shape = Shapes.join(shape, Shapes.box(0.125, 0, 0, 0.875, 0.875, 0.125), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.125, 0.875, 1), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.875, 0, 0, 1, 0.875, 1), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0.9375, 0, 1, 1, 1), BooleanOp.OR);
        } else {
            shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0, 0.875, 1, 0.125), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0.125, 0, 0.125, 1, 1), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.875, 0.125, 0, 1, 1, 1), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.0625, 1), BooleanOp.OR);
        }
        return switch (direction) {
            case EAST -> VoxelShapeUtil.rotateShape(shape, 90);
            case SOUTH -> VoxelShapeUtil.rotateShape(shape, 180);
            case WEST -> VoxelShapeUtil.rotateShape(shape, 270);
            default -> shape;
        };
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        super.useWithoutItem(state, level, pos, player, hitResult);
        if (canOpenDoor(level, pos, state)) {
            toggleDoor(level, pos, state);
            notifyToSurgery(level, pos, state);
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }
        return InteractionResult.PASS;
    }

    public void notifyToSurgery(Level level, BlockPos pos, BlockState state) {
        SurgeryBlockEntity surgeryBlockEntity = getSurgery(level, pos, state);
        if (surgeryBlockEntity != null) {
            surgeryBlockEntity.notifyChange();
        }
    }

    public boolean canOpenDoor(Level level, BlockPos pos, BlockState state) {
        SurgeryBlockEntity surgeryBlockEntity = getSurgery(level, pos, state);
        if (surgeryBlockEntity != null) {
            return surgeryBlockEntity.canOpenChamberDoor();
        }
        return true;
    }

    @Nullable
    public SurgeryBlockEntity getSurgery(Level level, BlockPos pos, BlockState state) {
        BlockEntity above = level.getBlockEntity(pos.above());
        if (above instanceof SurgeryChamberBlockEntity) {
            above = level.getBlockEntity(pos.above().above());
        }
        if (above instanceof SurgeryBlockEntity surgeryBlockEntity) {
            return surgeryBlockEntity;
        }
        return null;
    }

    public void toggleDoor(Level level, BlockPos pos, BlockState state) {
        boolean newDoorState = !state.getValue(DOOR);
        BlockPos masterPos = state.getValue(SLAVE) ? pos.below() : pos;
        BlockPos slavePos = state.getValue(SLAVE) ? pos : pos.above();

        BlockState masterState = level.getBlockState(masterPos).setValue(DOOR, newDoorState);
        BlockState slaveState = level.getBlockState(slavePos).setValue(DOOR, newDoorState);

        level.setBlock(masterPos, masterState, Block.UPDATE_ALL);
        level.setBlock(slavePos, slaveState, Block.UPDATE_ALL);
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return generateShape(state.getValue(FACING), state.getValue(SLAVE));
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return generateShape(state.getValue(FACING), state.getValue(SLAVE));
    }

    @Override
    protected boolean isCollisionShapeFullBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !movedByPiston) {
            BlockPos otherPos = state.getValue(SLAVE) ? pos.below() : pos.above();
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.getBlock() instanceof SurgeryChamberBlock) {
                level.removeBlock(otherPos, false);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new SurgeryChamberBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
