package com.hersh.more_pistons.block;

import com.hersh.more_pistons.MorePistons;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModBlockTags {
    public static final TagKey<Block> NORMAL_PISTONS = register("normal_pistons");
    public static final TagKey<Block> STICKY_PISTONS = register("sticky_pistons");
    public static final TagKey<Block> HONEY_PISTONS = register("honey_pistons");

    private static TagKey<Block> register(String name) {
        return TagKey.of(RegistryKeys.BLOCK, MorePistons.id(name));
    }
}
