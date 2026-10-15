package com.hersh.more_pistons.mixin;

import com.hersh.more_pistons.block.ModBlockTags;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin {

    @Inject(method = "onSyncedBlockEvent", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/block/PistonBlock;sticky:Z", opcode = Opcodes.GETFIELD)), at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/PistonBlockEntity;finish()V", shift = At.Shift.AFTER))
    private void finishSetAir(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
        world.setBlockState(pos.offset(state.get(PistonBlock.FACING)), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
    }

    @ModifyExpressionValue(method = "isMovable", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;STICKY_PISTON:Lnet/minecraft/block/Block;", opcode = Opcodes.GETSTATIC)), at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private static boolean isMovable(boolean original, BlockState state) {
        return original || state.isIn(ModBlockTags.NORMAL_PISTONS) || state.isIn(ModBlockTags.STICKY_PISTONS) || state.isIn(ModBlockTags.HONEY_PISTONS);
    }
}
