package com.hersh.more_pistons.mixin;

import com.hersh.more_pistons.block.AbstractPistonBlock;
import com.hersh.more_pistons.block.ModBlockTags;
import com.hersh.more_pistons.block.ModBlocks;
import com.hersh.more_pistons.util.PistonBlockEntityExtensions;
import com.hersh.more_pistons.util.PistonUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.*;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin implements PistonBlockEntityExtensions {
    @Shadow private BlockState pushedBlock;
    @Unique private boolean sourceArm = false;

    @Override
    public void setSourceArm(boolean value) {
        this.sourceArm = value;
    }

    @Override
    public boolean hasSourceArm() {
        return this.sourceArm;
    }

    //classValue=net/minecraft/class_2665
    @Redirect(method = {"getHeadBlockState", "getCollisionShape"}, at = @At(value = "CONSTANT", args = "classValue=net/minecraft/block/PistonBlock", ordinal = 0))
    private boolean isPistonBlock(Object reference, Class<?> classValue) {
        return this.pushedBlock.isIn(ModBlockTags.NORMAL_PISTONS) || this.pushedBlock.isIn(ModBlockTags.STICKY_PISTONS) || this.pushedBlock.isIn(ModBlockTags.HONEY_PISTONS);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Redirect(method = "getCollisionShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", ordinal = 0))
    private Object safeWithExtended(BlockState blockState, Property property, Comparable value) {
        return blockState.withIfExists(property, value);
    }

    @ModifyExpressionValue(method = {"getHeadBlockState", "getCollisionShape"}, at = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;PISTON_HEAD:Lnet/minecraft/block/Block;", ordinal = 0, opcode = Opcodes.GETSTATIC))
    private Block getHeadBlock(Block original) {
        return this.hasSourceArm() ? ModBlocks.PISTON_ARM : original;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlockEntity(Lnet/minecraft/util/math/BlockPos;)V"))
    private static void updateLongPiston(World world, BlockPos pos, BlockState state, PistonBlockEntity blockEntity, CallbackInfo ci) {
        if (!blockEntity.isSource()) return;

        Direction dir = blockEntity.getFacing();
        BlockPos basePos = blockEntity.isExtending() ? pos.offset(dir.getOpposite()) : pos;
        BlockState baseState = blockEntity.isExtending() ? world.getBlockState(basePos) : blockEntity.getPushedBlock();

        if (baseState.getBlock() instanceof AbstractPistonBlock pistonBlock) {
            int targetLength = pistonBlock.getTargetLength(world, basePos, baseState);
            int length = PistonUtils.getArmLength(world, basePos, baseState, pistonBlock.getMaxLength());

            if (blockEntity.isExtending() && length < targetLength) {
                if (PistonUtils.getPistonHandler(world, basePos.offset(dir, length), dir, true, pistonBlock.getPushLimit()).calculatePush()) {
                    world.addSyncedBlockEvent(basePos, pistonBlock, 0, length + 1);
                }
            } else if (!blockEntity.isExtending() && length > targetLength) {
                int type = 2;
                if (!baseState.isIn(ModBlockTags.HONEY_PISTONS)) {
                    BlockPos stickPos = basePos.offset(dir, length + 1);
                    BlockState stickState = world.getBlockState(stickPos);
                    if (
                        stickState.isOf(Blocks.MOVING_PISTON) &&
                        stickState.get(PistonExtensionBlock.FACING) == dir &&
                        world.getBlockEntity(stickPos) instanceof PistonBlockEntity blockEntity2 &&
                        blockEntity2.isExtending() && (
                            blockEntity2.getProgress(0.0F) < 0.5F ||
                            world.getTime() == blockEntity2.getSavedWorldTime()
                        )
                    ) type |= 4;
                }

                world.addSyncedBlockEvent(basePos, pistonBlock, type, length);
            }
        }
    }

    @Redirect(method = "finish", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;source:Z", opcode = Opcodes.GETFIELD))
    private boolean finish(PistonBlockEntity instance) {
        return false;
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        nbt.putBoolean("sourceArm", this.sourceArm);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        this.sourceArm = nbt.getBoolean("sourceArm");
    }
}