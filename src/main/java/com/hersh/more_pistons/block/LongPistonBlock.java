package com.hersh.more_pistons.block;

import com.hersh.more_pistons.util.PistonUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class LongPistonBlock extends AbstractPistonBlock {
    public static final MapCodec<LongPistonBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.INT.fieldOf("max_length").forGetter(block -> block.maxLength), Codec.INT.fieldOf("push_limit").forGetter(block -> block.pushLimit), PistonUtils.TYPE_CODEC.fieldOf("type").forGetter(block -> block.type), createSettingsCodec()).apply(instance, LongPistonBlock::new));
    public static final BooleanProperty POWERED = Properties.POWERED;
    private final int maxLength;

    public LongPistonBlock(int pushLimit, int length, PistonType type, Settings settings) {
        super(pushLimit, type, settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(EXTENDED, false).with(POWERED, false));
        this.maxLength = length;
    }

    public LongPistonBlock(int length, PistonType type, Settings settings) {
        this(0, length, type, settings);
    }

    @Override
    public int getMaxLength() {
        return this.maxLength;
    }

    @Override
    public int getTargetLength(RedstoneView world, BlockPos pos, BlockState state) {
        return this.hasPower(world, pos, state.get(FACING)) ? this.getMaxLength() : 0;
    }

    @Override
    public int getPushLimit() {
        return this.pushLimit;
    }

    @Override
    public MapCodec<LongPistonBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENDED, POWERED);
    }

    @Override
    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        Direction dir = state.get(FACING);
        if (!world.isClient) {
            boolean shouldExtend = this.hasPower(world, pos, dir);
            if (!shouldExtend && (type & 2) == 0 || shouldExtend && (type & 2) != 0) return false;
        }

        BlockPos headPos = pos.offset(dir, data);
        BlockState movingPiston = Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, dir).with(PistonExtensionBlock.TYPE, this.type);
        if ((type & 2) == 0) {
            PistonHandler handler = PistonUtils.getPistonHandler(world, headPos.offset(dir.getOpposite()), dir, true, this.getPushLimit());
		    if (!handler.calculatePush()) return false;

            world.setBlockState(pos, state.with(EXTENDED, true).with(POWERED, false), Block.FORCE_STATE | Block.NO_REDRAW);
            this.moveBlocks(world, headPos, dir, true, handler.getMovedBlocks(), handler.getBrokenBlocks());

            for (int i = 1; i < data; i++) {
                world.setBlockState(pos.offset(dir, i), movingPiston, Block.MOVED | Block.NO_REDRAW);
                world.addBlockEntity(PistonUtils.createBlockEntityPiston(pos.offset(dir, i), movingPiston, ModBlocks.PISTON_ARM.getDefaultState().with(PistonArmBlock.FACING, dir).with(PistonArmBlock.TYPE, this.type).with(PistonArmBlock.SHORT, i != 1), dir, true, i == 1, i == 1));
            }
            world.setBlockState(headPos, movingPiston, Block.MOVED | Block.NO_REDRAW);
            world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(headPos, movingPiston, Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir).with(PistonHeadBlock.TYPE, this.type).with(PistonHeadBlock.SHORT, data != 1), dir, true, data == 1));
            world.updateNeighborsExcept(headPos, Blocks.PISTON_HEAD, dir.getOpposite());

            world.setBlockState(pos, state.with(EXTENDED, true).with(POWERED, true), Block.NOTIFY_ALL | Block.MOVED);

            if ((type & 1) != 0) {
                world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
                world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(movingPiston));
            }
        } else {
            if (world.getBlockEntity(headPos) instanceof PistonBlockEntity blockEntity) {
                blockEntity.finish();
            }
            for (int i = 1; i < data; i++) {
                if (world.getBlockEntity(pos.offset(dir, i)) instanceof PistonBlockEntity blockEntity) {
                    blockEntity.finish();
                }
                world.setBlockState(pos.offset(dir, i), movingPiston, Block.MOVED | Block.NO_REDRAW);
                world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(pos.offset(dir, i), movingPiston, (i == data - 1 ? Blocks.PISTON_HEAD : ModBlocks.PISTON_ARM).getDefaultState().with(PistonHeadBlock.FACING, dir).with(PistonHeadBlock.TYPE, this.type).with(PistonArmBlock.SHORT, i != 1), dir, false, false));
            }
            world.setBlockState(pos, movingPiston, Block.FORCE_STATE | Block.NO_REDRAW);
            world.addBlockEntity(PistonUtils.createBlockEntityPiston(pos, movingPiston, this.getDefaultState().with(FACING, dir).with(EXTENDED, data != 1).with(POWERED, false), dir, false, true, data != 1));
            world.updateNeighbors(pos, movingPiston.getBlock());
            movingPiston.updateNeighbors(world, pos, Block.NOTIFY_LISTENERS);

            if (this.type != PistonType.DEFAULT) {
                BlockPos stickPos = headPos.offset(dir);
                boolean stick = true;
                if (world.getBlockState(stickPos).isOf(Blocks.MOVING_PISTON) && world.getBlockEntity(stickPos) instanceof PistonBlockEntity blockEntity && blockEntity.getFacing() == dir && blockEntity.isExtending()) {
                    blockEntity.finish();
                    if (this.type == PistonType.STICKY) {
                        world.setBlockState(headPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                        stick = false;
                    }
                }
                BlockState stickState = world.getBlockState(stickPos);
                if (stick) {
                    if ((type & 4) == 0 && !stickState.isAir() && PistonBlock.isMovable(stickState, world, stickPos, dir.getOpposite(), false, dir)) {
                        if (world.getBlockState(headPos).isOf(Blocks.PISTON_HEAD)) {
                            world.setBlockState(headPos, Blocks.AIR.getDefaultState(), Block.FORCE_STATE | Block.NO_REDRAW);
                        }
                        PistonHandler handler = PistonUtils.getPistonHandler(world, headPos.offset(dir.getOpposite()), dir, false, this.getPushLimit());
                        if (handler.calculatePush()) {
                            this.moveBlocks(world, headPos, dir, false, handler.getMovedBlocks(), handler.getBrokenBlocks());
                        }
                    } else {
                        world.removeBlock(headPos, false);
                    }
                }
            } else {
                world.removeBlock(headPos, false);
            }

            if ((type & 1) != 0) {
                world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
                world.emitGameEvent(GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Emitter.of(movingPiston));
            }
        }
        return true;
    }

    @Override
    protected void tryMove(World world, BlockPos pos, BlockState state) {
        Direction dir = state.get(FacingBlock.FACING);
        int targetLength = this.getTargetLength(world, pos, state);
        int length = PistonUtils.getArmLength(world, pos, state, this.getMaxLength());

        if (!state.get(POWERED) && length < targetLength) {
            if (PistonUtils.getPistonHandler(world, pos.offset(dir, length), dir, true, this.getPushLimit()).calculatePush()) {
                world.addSyncedBlockEvent(pos, this, 1, length + 1);
            }
        } else if (state.get(POWERED) && length > targetLength) {
            int type = 3;
            if (!state.isIn(ModBlockTags.HONEY_PISTONS)) {
                BlockPos stickPos = pos.offset(dir, length + 1);
                BlockState stickState = world.getBlockState(stickPos);
                if (
                    stickState.isOf(Blocks.MOVING_PISTON) &&
                    stickState.get(PistonExtensionBlock.FACING) == dir &&
                    world.getBlockEntity(stickPos) instanceof PistonBlockEntity blockEntity &&
                    blockEntity.isExtending() && (
                        blockEntity.getProgress(0.0F) < 0.5F ||
                        world.getTime() == blockEntity.getSavedWorldTime() ||
                        ((ServerWorld) world).isInBlockTick()
                    )
                ) type |= 4;
            }

            world.addSyncedBlockEvent(pos, this, type, length);
        }
    }

    private boolean hasPower(RedstoneView world, BlockPos pos, Direction pistonFace) {
        for (Direction dir : Direction.values()) {
            if (dir != pistonFace && world.isEmittingRedstonePower(pos.offset(dir), dir)) {
                return true;
            }
        }

        return world.isReceivingRedstonePower(pos.up());
    }
}
