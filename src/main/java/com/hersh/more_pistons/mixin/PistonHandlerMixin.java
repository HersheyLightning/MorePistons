package com.hersh.more_pistons.mixin;

import com.hersh.more_pistons.util.PistonHandlerExtensions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.piston.PistonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin implements PistonHandlerExtensions {
    @Unique private int pushLimit;

    @Override
    public void setPushLimit(int value) {
        this.pushLimit = value;
    }

    @ModifyExpressionValue(method = "tryMove", at = @At(value = "CONSTANT", args = "intValue=12"))
    private int getPushLimit(int original) {
        return pushLimit > 0 ? pushLimit : original;
    }
}
