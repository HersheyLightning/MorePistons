package com.hersh.more_pistons.mixin;

import net.minecraft.block.enums.PistonType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PistonType.class)
public enum PistonTypeMixin {
    HONEY("honey");

    @Shadow
    PistonTypeMixin(final String name) {
    }
}