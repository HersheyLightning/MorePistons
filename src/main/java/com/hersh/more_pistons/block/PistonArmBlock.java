package com.hersh.more_pistons.block;

import com.hersh.more_pistons.util.PistonUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Optional;

import static net.minecraft.state.property.Properties.EXTENDED;

public class PistonArmBlock extends FacingBlock {
    public static final MapCodec<PistonArmBlock> CODEC = createCodec(PistonArmBlock::new);
    public static final EnumProperty<PistonType> TYPE = Properties.PISTON_TYPE;
    public static final BooleanProperty SHORT = Properties.SHORT;
    protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(6, -4, 6, 10, 16, 10);
    protected static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(6, 0, 6, 10, 20, 10);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(6, 6, -4, 10, 10, 16);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(6, 6, 0, 10, 10, 20);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(-4, 6, 6, 16, 10, 10);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0, 6, 6, 20, 10, 10);
    protected static final VoxelShape SHORT_X_SHAPE = Block.createCuboidShape(0, 6, 6, 16, 10, 10);
    protected static final VoxelShape SHORT_Y_SHAPE = Block.createCuboidShape(6, 0, 6, 10, 16, 10);
    protected static final VoxelShape SHORT_Z_SHAPE = Block.createCuboidShape(6, 6, 0, 10, 10, 16);

    public PistonArmBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, PistonType.DEFAULT).with(SHORT, false));
    }

    @Override
    protected MapCodec<PistonArmBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SHORT);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState otherState = world.getBlockState(pos.offset(state.get(FACING)));
        BlockState otherState2 = world.getBlockState(pos.offset(state.get(FACING).getOpposite()));

        return (this.isAttached(state, otherState, true) || otherState.isOf(Blocks.MOVING_PISTON) && state.get(FACING) == otherState.get(FACING)) && (this.isAttached(state, otherState2, false) || otherState2.isOf(Blocks.MOVING_PISTON) && state.get(FACING) == otherState2.get(FACING));
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.getAbilities().creativeMode) {
            BlockState otherState = world.getBlockState(pos.offset(state.get(FACING)));
            BlockState otherState2 = world.getBlockState(pos.offset(state.get(FACING).getOpposite()));
            if (this.isAttached(state, otherState, true) && this.isAttached(state, otherState2, false)) {
                PistonUtils.getBasePos(world, pos, state).ifPresent(basePos -> world.breakBlock(basePos, false));
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (moved || state.isOf(newState.getBlock())) return;

        BlockPos otherPos = pos.offset(state.get(FACING));
        if (this.isAttached(state, world.getBlockState(otherPos), true)) {
            world.breakBlock(otherPos, true);
        }
        BlockPos otherPos2 = pos.offset(state.get(FACING).getOpposite());
        if (this.isAttached(state, world.getBlockState(otherPos2), false)) {
            world.breakBlock(otherPos2, true);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (state.canPlaceAt(world, pos)) {
            world.updateNeighbor(pos.offset(state.get(FACING).getOpposite()), sourceBlock, sourcePos);
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.get(FACING).getAxis() == direction.getAxis() && !state.canPlaceAt(world, pos)
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        Optional<BlockPos> basePos = PistonUtils.getBasePos(world, pos, state);
        if (basePos.isPresent()) {
            BlockState baseState = world.getBlockState(basePos.get());
            return baseState.getBlock().getPickStack(world, basePos.get(), baseState);
        }
        return new ItemStack (switch (state.get(TYPE)) {
            case PistonType.HONEY -> ModBlocks.HONEY_PISTON;
            case PistonType.STICKY -> Blocks.STICKY_PISTON;
            default -> Blocks.PISTON;
        });
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(SHORT)) return switch (state.get(FACING).getAxis()) {
            case X -> SHORT_X_SHAPE;
            case Z -> SHORT_Z_SHAPE;
            default -> SHORT_Y_SHAPE;
        };
        return switch (state.get(FACING)) {
            case DOWN -> DOWN_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> UP_SHAPE;
        };
    }

    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    private boolean isAttached(BlockState state, BlockState otherState, boolean facingSide) {
        if (!otherState.contains(FACING) || state.get(FACING) != otherState.get(FACING)) return false;

        if (facingSide) return (
            otherState.isOf(ModBlocks.PISTON_ARM) && state.get(TYPE) == otherState.get(TYPE) ||
            otherState.isOf(Blocks.PISTON_HEAD) && state.get(TYPE) == otherState.get(TYPE)
        );

        return (
            otherState.isOf(ModBlocks.PISTON_ARM) && state.get(TYPE) == otherState.get(TYPE) ||
            otherState.contains(EXTENDED) && otherState.get(EXTENDED) && (
                otherState.isIn(ModBlockTags.NORMAL_PISTONS) && state.get(TYPE) == PistonType.DEFAULT ||
                otherState.isIn(ModBlockTags.STICKY_PISTONS) && state.get(TYPE) == PistonType.STICKY ||
                otherState.isIn(ModBlockTags.HONEY_PISTONS) && state.get(TYPE) == PistonType.HONEY
            )
        );
    }
}
