package com.hersh.more_pistons.mixin;

import com.hersh.more_pistons.block.ModBlockTags;
import com.hersh.more_pistons.block.ModBlocks;
import com.hersh.more_pistons.util.PistonUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.enums.PistonType;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;


@Mixin(PistonHeadBlock.class)
public abstract class PistonHeadBlockMixin {

    @ModifyExpressionValue(method = "onBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonHeadBlock;isAttached(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
    private boolean onBreak(boolean original, World world, BlockPos pos, BlockState state) {
        if (original) PistonUtils.getBasePos(world, pos, state).ifPresent(basePos -> world.breakBlock(basePos, false));
        return false;
    }

    @ModifyExpressionValue(method = "onStateReplaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonHeadBlock;isAttached(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
    private boolean onStateReplaced(boolean original, BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        return original && !moved;
    }

    @Inject(method = "getPickStack", at = @At("HEAD"), cancellable = true)
    private void getPickStack(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> cir) {
        Optional<BlockPos> basePos = PistonUtils.getBasePos(world, pos, state);
        if (basePos.isPresent()) {
            BlockState baseState = world.getBlockState(basePos.get());
            cir.setReturnValue(baseState.getBlock().getPickStack(world, basePos.get(), baseState));
        } else if (state.get(Properties.PISTON_TYPE) == PistonType.HONEY) {
            cir.setReturnValue(new ItemStack(ModBlocks.HONEY_PISTON));
        }
    }

    @ModifyReturnValue(method = "isAttached", at = @At("RETURN"))
    private boolean isAttached(boolean original, BlockState state, BlockState otherState) {
        if (!otherState.contains(Properties.FACING) || state.get(Properties.FACING) != otherState.get(Properties.FACING)) return original;

        return original || (
            otherState.isOf(ModBlocks.PISTON_ARM) && state.get(Properties.PISTON_TYPE) == otherState.get(Properties.PISTON_TYPE) ||
            otherState.contains(Properties.EXTENDED) && otherState.get(Properties.EXTENDED) && (
                otherState.isIn(ModBlockTags.NORMAL_PISTONS) && state.get(Properties.PISTON_TYPE) == PistonType.DEFAULT ||
                otherState.isIn(ModBlockTags.STICKY_PISTONS) && state.get(Properties.PISTON_TYPE) == PistonType.STICKY ||
                otherState.isIn(ModBlockTags.HONEY_PISTONS) && state.get(Properties.PISTON_TYPE) == PistonType.HONEY
            )
        );
    }
}
