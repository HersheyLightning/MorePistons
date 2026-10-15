package com.hersh.more_pistons.block;

import carpet.CarpetSettings;
import carpet.fakes.PistonBlockEntityInterface;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Map;

public abstract class AbstractPistonBlock extends FacingBlock {
    public static final BooleanProperty EXTENDED = Properties.EXTENDED;
    protected static final VoxelShape EXTENDED_EAST_SHAPE = Block.createCuboidShape(0, 0, 0, 12, 16, 16);
    protected static final VoxelShape EXTENDED_WEST_SHAPE = Block.createCuboidShape(4, 0, 0, 16, 16, 16);
    protected static final VoxelShape EXTENDED_SOUTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 12);
    protected static final VoxelShape EXTENDED_NORTH_SHAPE = Block.createCuboidShape(0, 0, 4, 16, 16, 16);
    protected static final VoxelShape EXTENDED_UP_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 12, 16);
    protected static final VoxelShape EXTENDED_DOWN_SHAPE = Block.createCuboidShape(0, 4, 0, 16, 16, 16);
    protected final PistonType type;
    protected final int pushLimit;

    public AbstractPistonBlock(int pushLimit, PistonType type, AbstractBlock.Settings settings) {
        super(settings);
        this.type = type;
        this.pushLimit = pushLimit;
    }

    public abstract int getMaxLength();

    public abstract int getTargetLength(RedstoneView world, BlockPos pos, BlockState state);

    public abstract int getPushLimit();

    @Override
    protected abstract MapCodec<? extends AbstractPistonBlock> getCodec();

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(EXTENDED, false);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) return;
        this.tryMove(world, pos, state);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient || oldState.isOf(state.getBlock()) || world.getBlockEntity(pos) != null) return;
        this.tryMove(world, pos, state);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;
        this.tryMove(world, pos, state);
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
        if (state.get(EXTENDED)) return switch (state.get(FACING)) {
            case DOWN -> EXTENDED_DOWN_SHAPE;
            case NORTH -> EXTENDED_NORTH_SHAPE;
            case SOUTH -> EXTENDED_SOUTH_SHAPE;
            case WEST -> EXTENDED_WEST_SHAPE;
            case EAST -> EXTENDED_EAST_SHAPE;
            default -> EXTENDED_UP_SHAPE;
        };
        return VoxelShapes.fullCube();
    }

    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return state.get(EXTENDED);
    }

    protected void moveBlocks(World world, BlockPos headPos, Direction pistonFace, boolean retracted, List<BlockPos> movedBlocks, List<BlockPos> brokenBlocks) {
		Map<BlockPos, BlockState> setAirMap = Maps.newHashMap();
        List<BlockState> movedStates = Lists.newArrayList();

		for (BlockPos blockPos2 : movedBlocks) {
			BlockState blockState = world.getBlockState(blockPos2);
			movedStates.add(blockState);
			setAirMap.put(blockPos2, blockState);
		}

        BlockState[] modifiedStates = new BlockState[movedBlocks.size() + brokenBlocks.size()];
        BlockState airState = Blocks.AIR.getDefaultState();
        Direction moveDir = retracted ? pistonFace : pistonFace.getOpposite();

        int i = 0;
		for (int j = brokenBlocks.size() - 1; j >= 0; j--) {
			BlockPos brokenPos = brokenBlocks.get(j);
			BlockState brokenState = world.getBlockState(brokenPos);
			BlockEntity blockEntity = brokenState.hasBlockEntity() ? world.getBlockEntity(brokenPos) : null;
			Block.dropStacks(brokenState, world, brokenPos, blockEntity);
			world.setBlockState(brokenPos, airState, Block.FORCE_STATE | Block.NOTIFY_LISTENERS);
			world.emitGameEvent(GameEvent.BLOCK_DESTROY, brokenPos, GameEvent.Emitter.of(brokenState));
			if (!brokenState.isIn(BlockTags.FIRE)) {
				world.addBlockBreakParticles(brokenPos, brokenState);
			}

			modifiedStates[i++] = brokenState;
		}

        for (int j = movedBlocks.size() - 1; j >= 0; j--) {
            BlockPos blockPos = movedBlocks.get(j);
            BlockState blockState = world.getBlockState(blockPos);
            BlockPos movedPos = blockPos.offset(moveDir);

			setAirMap.remove(movedPos);
			BlockState movingPiston = Blocks.MOVING_PISTON.getDefaultState().with(FACING, pistonFace);
			world.setBlockState(movedPos, movingPiston, Block.MOVED | Block.NO_REDRAW);
            BlockEntity pistonBlockEntity = PistonExtensionBlock.createBlockEntityPiston(movedPos, movingPiston, movedStates.get(j), pistonFace, retracted, false);

            if (FabricLoader.getInstance().isModLoaded("carpet") && CarpetSettings.movableBlockEntities) {
                BlockEntity blockEntity = movedStates.get(j).hasBlockEntity() ? world.getBlockEntity(blockPos) : null;
                ((PistonBlockEntityInterface) pistonBlockEntity).setCarriedBlockEntity(blockEntity);
                if (blockEntity != null) {
                    world.removeBlockEntity(blockPos);
                    blockEntity.markDirty();
                }
            }
            world.addBlockEntity(pistonBlockEntity);

			modifiedStates[i++] = blockState;
		}

        if (retracted) setAirMap.remove(headPos);

		for (BlockPos blockPos : setAirMap.keySet()) {
			world.setBlockState(blockPos, airState, Block.FORCE_STATE | Block.MOVED | Block.NOTIFY_LISTENERS);
		}

		for (Map.Entry<BlockPos, BlockState> entry : setAirMap.entrySet()) {
			BlockPos blockPos = entry.getKey();
			BlockState blockState = entry.getValue();
			blockState.prepare(world, blockPos, Block.NOTIFY_LISTENERS);
			airState.updateNeighbors(world, blockPos, Block.NOTIFY_LISTENERS);
			airState.prepare(world, blockPos, Block.NOTIFY_LISTENERS);
		}

		i = 0;
		for (int j = brokenBlocks.size() - 1; j >= 0; j--) {
			BlockState blockState = modifiedStates[i++];
			BlockPos blockPos = brokenBlocks.get(j);
			blockState.prepare(world, blockPos, 2);
			world.updateNeighborsAlways(blockPos, blockState.getBlock());
		}

		for (int j = movedBlocks.size() - 1; j >= 0; j--) {
			world.updateNeighborsAlways(movedBlocks.get(j), modifiedStates[i++].getBlock());
		}
    }

    protected abstract void tryMove(World world, BlockPos pos, BlockState state);


}
