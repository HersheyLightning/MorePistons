package com.hersh.more_pistons.mixin;

import com.hersh.more_pistons.block.ModBlockTags;
import com.hersh.more_pistons.block.ModBlocks;
import com.hersh.more_pistons.block.PistonArmBlock;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.state.property.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(PistonBlockEntityRenderer.class)
public abstract class PistonBlockEntityRendererMixin {

    @ModifyExpressionValue(method = "render(Lnet/minecraft/block/entity/PistonBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0))
    private boolean changeShortState(boolean original, PistonBlockEntity blockEntity, @Local(ordinal = 0) BlockState pushedState) {
        return blockEntity.isSource() && (original || pushedState.isOf(ModBlocks.PISTON_ARM));
    }

    @ModifyVariable(method = "render(Lnet/minecraft/block/entity/PistonBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", ordinal = 1, at = @At(value = "STORE", ordinal = 0))
    private BlockState getArmState(BlockState original, PistonBlockEntity blockEntity, @Local(ordinal = 0) BlockState pushedState) {
        PistonType pistonType = pushedState.isIn(ModBlockTags.HONEY_PISTONS) ? PistonType.HONEY : pushedState.isIn(ModBlockTags.STICKY_PISTONS) ? PistonType.STICKY : PistonType.DEFAULT;
        if (blockEntity.hasSourceArm()) {
            return ModBlocks.PISTON_ARM.getDefaultState().with(PistonArmBlock.TYPE, pistonType).with(PistonArmBlock.FACING, pushedState.get(Properties.FACING));
        }
        return original.with(PistonHeadBlock.TYPE, pistonType);
    }
}
