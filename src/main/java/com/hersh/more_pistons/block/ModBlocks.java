package com.hersh.more_pistons.block;

import com.hersh.more_pistons.MorePistons;
import net.minecraft.block.*;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.block.AbstractBlock.Settings;

public class ModBlocks {
    public static final Block PISTON_ARM = register(false, "piston_arm", new PistonArmBlock(Settings.create().strength(1.5F).dropsNothing().pistonBehavior(PistonBehavior.BLOCK).mapColor(MapColor.STONE_GRAY)));
    public static final Block HONEY_PISTON = register(true, "honey_piston", new GenericPistonBlock(PistonType.HONEY, pistonSettings()));
    public static final Block DOUBLE_PISTON = register(true, "double_piston", new LongPistonBlock(2, PistonType.DEFAULT, pistonSettings()));
    public static final Block DOUBLE_STICKY_PISTON = register(true, "double_sticky_piston", new LongPistonBlock(2, PistonType.STICKY, pistonSettings()));
    public static final Block DOUBLE_HONEY_PISTON = register(true, "double_honey_piston", new LongPistonBlock(2, PistonType.HONEY, pistonSettings()));
    public static final Block TRIPLE_PISTON = register(true, "triple_piston", new LongPistonBlock(3, PistonType.DEFAULT, pistonSettings()));
    public static final Block TRIPLE_STICKY_PISTON = register(true, "triple_sticky_piston", new LongPistonBlock(3, PistonType.STICKY, pistonSettings()));
    public static final Block TRIPLE_HONEY_PISTON = register(true, "triple_honey_piston", new LongPistonBlock(3, PistonType.HONEY, pistonSettings()));
    public static final Block QUADRUPLE_PISTON = register(true, "quadruple_piston", new LongPistonBlock(4, PistonType.DEFAULT, pistonSettings()));
    public static final Block QUADRUPLE_STICKY_PISTON = register(true, "quadruple_sticky_piston", new LongPistonBlock(4, PistonType.STICKY, pistonSettings()));
    public static final Block QUADRUPLE_HONEY_PISTON = register(true, "quadruple_honey_piston", new LongPistonBlock(4, PistonType.HONEY, pistonSettings()));
    public static final Block QUINTUPLE_PISTON = register(true, "quintuple_piston", new LongPistonBlock(5, PistonType.DEFAULT, pistonSettings()));
    public static final Block QUINTUPLE_STICKY_PISTON = register(true, "quintuple_sticky_piston", new LongPistonBlock(5, PistonType.STICKY, pistonSettings()));
    public static final Block QUINTUPLE_HONEY_PISTON = register(true, "quintuple_honey_piston", new LongPistonBlock(5, PistonType.HONEY, pistonSettings()));
    public static final Block SEXTUPLE_PISTON = register(true, "sextuple_piston", new LongPistonBlock(6, PistonType.DEFAULT, pistonSettings()));
    public static final Block SEXTUPLE_STICKY_PISTON = register(true, "sextuple_sticky_piston", new LongPistonBlock(6, PistonType.STICKY, pistonSettings()));
    public static final Block SEXTUPLE_HONEY_PISTON = register(true, "sextuple_honey_piston", new LongPistonBlock(6, PistonType.HONEY, pistonSettings()));
    public static final Block SEPTUPLE_PISTON = register(true, "septuple_piston", new LongPistonBlock(7, PistonType.DEFAULT, pistonSettings()));
    public static final Block SEPTUPLE_STICKY_PISTON = register(true, "septuple_sticky_piston", new LongPistonBlock(7, PistonType.STICKY, pistonSettings()));
    public static final Block SEPTUPLE_HONEY_PISTON = register(true, "septuple_honey_piston", new LongPistonBlock(7, PistonType.HONEY, pistonSettings()));
    public static final Block OCTUPLE_PISTON = register(true, "octuple_piston", new LongPistonBlock(8, PistonType.DEFAULT, pistonSettings()));
    public static final Block OCTUPLE_STICKY_PISTON = register(true, "octuple_sticky_piston", new LongPistonBlock(8, PistonType.STICKY, pistonSettings()));
    public static final Block OCTUPLE_HONEY_PISTON = register(true, "octuple_honey_piston", new LongPistonBlock(8, PistonType.HONEY, pistonSettings()));
    public static final Block SMART_PISTON = register(true, "smart_piston", new SmartPistonBlock(PistonType.DEFAULT, pistonSettings()));
    public static final Block SMART_STICKY_PISTON = register(true, "smart_sticky_piston", new SmartPistonBlock(PistonType.STICKY, pistonSettings()));
    public static final Block SMART_HONEY_PISTON = register(true, "smart_honey_piston", new SmartPistonBlock(PistonType.HONEY, pistonSettings()));

    public static final Block STRONG_PISTON = register(true, "strong_piston", new GenericPistonBlock(24, PistonType.DEFAULT, pistonSettings()));
    public static final Block STRONG_STICKY_PISTON = register(true, "strong_sticky_piston", new GenericPistonBlock(24, PistonType.STICKY, pistonSettings()));
    public static final Block STRONG_HONEY_PISTON = register(true, "strong_honey_piston", new GenericPistonBlock(24, PistonType.HONEY, pistonSettings()));
    public static final Block STRONG_DOUBLE_PISTON = register(true, "strong_double_piston", new LongPistonBlock(24, 2, PistonType.DEFAULT, pistonSettings()));
    public static final Block STRONG_DOUBLE_STICKY_PISTON = register(true, "strong_double_sticky_piston", new LongPistonBlock(24, 2, PistonType.STICKY, pistonSettings()));
    public static final Block STRONG_DOUBLE_HONEY_PISTON = register(true, "strong_double_honey_piston", new LongPistonBlock(24, 2, PistonType.HONEY, pistonSettings()));
    public static final Block STRONG_TRIPLE_PISTON = register(true, "strong_triple_piston", new LongPistonBlock(24, 3, PistonType.DEFAULT, pistonSettings()));
    public static final Block STRONG_TRIPLE_STICKY_PISTON = register(true, "strong_triple_sticky_piston", new LongPistonBlock(24, 3, PistonType.STICKY, pistonSettings()));
    public static final Block STRONG_TRIPLE_HONEY_PISTON = register(true, "strong_triple_honey_piston", new LongPistonBlock(24, 3, PistonType.HONEY, pistonSettings()));
    public static final Block STRONG_QUADRUPLE_PISTON = register(true, "strong_quadruple_piston", new LongPistonBlock(24, 4, PistonType.DEFAULT, pistonSettings()));
    public static final Block STRONG_QUADRUPLE_STICKY_PISTON = register(true, "strong_quadruple_sticky_piston", new LongPistonBlock(24, 4, PistonType.STICKY, pistonSettings()));
    public static final Block STRONG_QUADRUPLE_HONEY_PISTON = register(true, "strong_quadruple_honey_piston", new LongPistonBlock(24, 4, PistonType.HONEY, pistonSettings()));
    public static final Block STRONG_QUINTUPLE_PISTON = register(true, "strong_quintuple_piston", new LongPistonBlock(24, 5, PistonType.DEFAULT, pistonSettings()));
    public static final Block STRONG_QUINTUPLE_STICKY_PISTON = register(true, "strong_quintuple_sticky_piston", new LongPistonBlock(24, 5, PistonType.STICKY, pistonSettings()));
    public static final Block STRONG_QUINTUPLE_HONEY_PISTON = register(true, "strong_quintuple_honey_piston", new LongPistonBlock(24, 5, PistonType.HONEY, pistonSettings()));
    public static final Block STRONG_SEXTUPLE_PISTON = register(true, "strong_sextuple_piston", new LongPistonBlock(24, 6, PistonType.DEFAULT, pistonSettings()));
    public static final Block STRONG_SEXTUPLE_STICKY_PISTON = register(true, "strong_sextuple_sticky_piston", new LongPistonBlock(24, 6, PistonType.STICKY, pistonSettings()));
    public static final Block STRONG_SEXTUPLE_HONEY_PISTON = register(true, "strong_sextuple_honey_piston", new LongPistonBlock(24, 6, PistonType.HONEY, pistonSettings()));
    public static final Block STRONG_SEPTUPLE_PISTON = register(true, "strong_septuple_piston", new LongPistonBlock(24, 7, PistonType.DEFAULT, pistonSettings()));
    public static final Block STRONG_SEPTUPLE_STICKY_PISTON = register(true, "strong_septuple_sticky_piston", new LongPistonBlock(24, 7, PistonType.STICKY, pistonSettings()));
    public static final Block STRONG_SEPTUPLE_HONEY_PISTON = register(true, "strong_septuple_honey_piston", new LongPistonBlock(24, 7, PistonType.HONEY, pistonSettings()));
    public static final Block STRONG_OCTUPLE_PISTON = register(true, "strong_octuple_piston", new LongPistonBlock(24, 8, PistonType.DEFAULT, pistonSettings()));
    public static final Block STRONG_OCTUPLE_STICKY_PISTON = register(true, "strong_octuple_sticky_piston", new LongPistonBlock(24, 8, PistonType.STICKY, pistonSettings()));
    public static final Block STRONG_OCTUPLE_HONEY_PISTON = register(true, "strong_octuple_honey_piston", new LongPistonBlock(24, 8, PistonType.HONEY, pistonSettings()));
    public static final Block STRONG_SMART_PISTON = register(true, "strong_smart_piston", new SmartPistonBlock(24, PistonType.DEFAULT, pistonSettings()));
    public static final Block STRONG_SMART_STICKY_PISTON = register(true, "strong_smart_sticky_piston", new SmartPistonBlock(24, PistonType.STICKY, pistonSettings()));
    public static final Block STRONG_SMART_HONEY_PISTON = register(true, "strong_smart_honey_piston", new SmartPistonBlock(24, PistonType.HONEY, pistonSettings()));

    private static Settings pistonSettings() {
        AbstractBlock.ContextPredicate predicate = (state, world, pos) -> !state.get(AbstractPistonBlock.EXTENDED);
        return Settings.create().solidBlock(Blocks::never).suffocates(predicate).blockVision(predicate).strength(1.5F).pistonBehavior(PistonBehavior.BLOCK).mapColor(MapColor.STONE_GRAY);
    }

    private static Block register(boolean hasItem, String name, Block block) {
        if (hasItem) Registry.register(Registries.ITEM, MorePistons.id(name), new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, MorePistons.id(name), block);
    }

    public static void registerAll() {
    }
}
