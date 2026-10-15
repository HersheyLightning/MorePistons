package com.hersh.more_pistons.util;

import com.hersh.more_pistons.block.ModBlockTags;
import com.hersh.more_pistons.block.ModBlocks;
import com.hersh.more_pistons.block.PistonArmBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Optional;

import static net.minecraft.state.property.Properties.EXTENDED;
import static net.minecraft.state.property.Properties.PISTON_TYPE;

public final class PistonUtils {
    public static final StringIdentifiable.EnumCodec<PistonType> TYPE_CODEC = StringIdentifiable.createCodec(PistonType::values);
    public static final IntProperty LENGTH = IntProperty.of("length", 0, 8);

    public static Optional<BlockPos> getBasePos(WorldView world, BlockPos armPos, BlockState armState) {
        Direction dir = armState.get(FacingBlock.FACING).getOpposite();
        BlockPos.Mutable mutable = armPos.mutableCopy();

        for (int i = 0; i < 512; i++) {
            mutable.move(dir);
            BlockState otherState = world.getBlockState(mutable);
            if (otherState.contains(FacingBlock.FACING) && otherState.get(FacingBlock.FACING) == armState.get(FacingBlock.FACING)) {
                if (otherState.isOf(ModBlocks.PISTON_ARM) && otherState.get(PistonArmBlock.TYPE) == armState.get(PistonArmBlock.TYPE)) continue;

                if (otherState.contains(EXTENDED) && otherState.get(EXTENDED)) {
                    if (otherState.isIn(ModBlockTags.NORMAL_PISTONS) && armState.get(PistonArmBlock.TYPE) == PistonType.DEFAULT || otherState.isIn(ModBlockTags.STICKY_PISTONS) && armState.get(PistonArmBlock.TYPE) == PistonType.STICKY || otherState.isIn(ModBlockTags.HONEY_PISTONS) && armState.get(PistonArmBlock.TYPE) == PistonType.HONEY) {
                        return Optional.of(mutable.toImmutable());
                    }
                }
            }
            break;
        }
        return Optional.empty();
    }

    public static int getArmLength(WorldView world, BlockPos pos, BlockState state, int maxLength) {
        Direction dir = state.get(FacingBlock.FACING);
        BlockPos.Mutable mutable = pos.mutableCopy();

        int length = 0;
        while (length < maxLength) {
            mutable.move(dir);
            BlockState otherState = world.getBlockState(mutable);
            if (
                !otherState.isOf(ModBlocks.PISTON_ARM) &&
                !otherState.isOf(Blocks.PISTON_HEAD) && (
                    !otherState.isOf(Blocks.MOVING_PISTON) ||
                    !(world.getBlockEntity(mutable) instanceof PistonBlockEntity blockEntity) ||
                    !blockEntity.isExtending() ||
                    !blockEntity.getPushedBlock().isOf(ModBlocks.PISTON_ARM) &&
                    !blockEntity.getPushedBlock().isOf(Blocks.PISTON_HEAD)
                ) ||
                otherState.get(FacingBlock.FACING) != state.get(FacingBlock.FACING) ||
                (!state.isIn(ModBlockTags.NORMAL_PISTONS) || otherState.get(PISTON_TYPE) != PistonType.DEFAULT) &&
                (!state.isIn(ModBlockTags.STICKY_PISTONS) || otherState.get(PISTON_TYPE) != PistonType.STICKY) &&
                (!state.isIn(ModBlockTags.HONEY_PISTONS) || otherState.get(PISTON_TYPE) != PistonType.HONEY)
            ) break;
            length++;
        }
        return length;
    }

    public static BlockEntity createBlockEntityPiston(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing, boolean extending, boolean source, boolean hasSourceArm) {
        PistonBlockEntity blockEntity = new PistonBlockEntity(pos, state, pushedBlock, facing, extending, source);
        blockEntity.setSourceArm(hasSourceArm);
        return blockEntity;
    }

    public static PistonHandler getPistonHandler(World world, BlockPos pos, Direction dir, boolean retracted, int pushLimit) {
        PistonHandler pistonHandler = new PistonHandler(world, pos, dir, retracted);
        pistonHandler.setPushLimit(pushLimit);
        return pistonHandler;
    }
}
