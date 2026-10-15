package com.hersh.more_pistons;

import com.hersh.more_pistons.block.ModBlocks;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MorePistons implements ModInitializer {
	public static final String MOD_ID = "more_pistons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(), id("item_group"));

	@Override
	public void onInitialize() {
		ModBlocks.registerAll();
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.HONEY_PISTON)).displayName(Text.translatable("itemGroup.more_pistons")).build());

        ItemGroupEvents.modifyEntriesEvent(MorePistons.ITEM_GROUP).register(entries -> {
            entries.add(Blocks.PISTON);
            entries.add(Blocks.STICKY_PISTON);
            entries.add(ModBlocks.HONEY_PISTON);
            entries.add(ModBlocks.DOUBLE_PISTON);
            entries.add(ModBlocks.DOUBLE_STICKY_PISTON);
            entries.add(ModBlocks.DOUBLE_HONEY_PISTON);
			entries.add(ModBlocks.TRIPLE_PISTON);
    		entries.add(ModBlocks.TRIPLE_STICKY_PISTON);
    		entries.add(ModBlocks.TRIPLE_HONEY_PISTON);
    		entries.add(ModBlocks.QUADRUPLE_PISTON);
    		entries.add(ModBlocks.QUADRUPLE_STICKY_PISTON);
    		entries.add(ModBlocks.QUADRUPLE_HONEY_PISTON);
    		entries.add(ModBlocks.QUINTUPLE_PISTON);
    		entries.add(ModBlocks.QUINTUPLE_STICKY_PISTON);
    		entries.add(ModBlocks.QUINTUPLE_HONEY_PISTON);
    		entries.add(ModBlocks.SEXTUPLE_PISTON);
    		entries.add(ModBlocks.SEXTUPLE_STICKY_PISTON);
    		entries.add(ModBlocks.SEXTUPLE_HONEY_PISTON);
    		entries.add(ModBlocks.SEPTUPLE_PISTON);
    		entries.add(ModBlocks.SEPTUPLE_STICKY_PISTON);
    		entries.add(ModBlocks.SEPTUPLE_HONEY_PISTON);
    		entries.add(ModBlocks.OCTUPLE_PISTON);
    		entries.add(ModBlocks.OCTUPLE_STICKY_PISTON);
    		entries.add(ModBlocks.OCTUPLE_HONEY_PISTON);
            entries.add(ModBlocks.SMART_PISTON);
            entries.add(ModBlocks.SMART_STICKY_PISTON);
            entries.add(ModBlocks.SMART_HONEY_PISTON);

            entries.add(ModBlocks.STRONG_PISTON);
            entries.add(ModBlocks.STRONG_STICKY_PISTON);
            entries.add(ModBlocks.STRONG_HONEY_PISTON);
            entries.add(ModBlocks.STRONG_DOUBLE_PISTON);
            entries.add(ModBlocks.STRONG_DOUBLE_STICKY_PISTON);
            entries.add(ModBlocks.STRONG_DOUBLE_HONEY_PISTON);
            entries.add(ModBlocks.STRONG_TRIPLE_PISTON);
            entries.add(ModBlocks.STRONG_TRIPLE_STICKY_PISTON);
            entries.add(ModBlocks.STRONG_TRIPLE_HONEY_PISTON);
            entries.add(ModBlocks.STRONG_QUADRUPLE_PISTON);
            entries.add(ModBlocks.STRONG_QUADRUPLE_STICKY_PISTON);
            entries.add(ModBlocks.STRONG_QUADRUPLE_HONEY_PISTON);
            entries.add(ModBlocks.STRONG_QUINTUPLE_PISTON);
            entries.add(ModBlocks.STRONG_QUINTUPLE_STICKY_PISTON);
            entries.add(ModBlocks.STRONG_QUINTUPLE_HONEY_PISTON);
            entries.add(ModBlocks.STRONG_SEXTUPLE_PISTON);
            entries.add(ModBlocks.STRONG_SEXTUPLE_STICKY_PISTON);
            entries.add(ModBlocks.STRONG_SEXTUPLE_HONEY_PISTON);
            entries.add(ModBlocks.STRONG_SEPTUPLE_PISTON);
            entries.add(ModBlocks.STRONG_SEPTUPLE_STICKY_PISTON);
            entries.add(ModBlocks.STRONG_SEPTUPLE_HONEY_PISTON);
            entries.add(ModBlocks.STRONG_OCTUPLE_PISTON);
            entries.add(ModBlocks.STRONG_OCTUPLE_STICKY_PISTON);
            entries.add(ModBlocks.STRONG_OCTUPLE_HONEY_PISTON);
            entries.add(ModBlocks.STRONG_SMART_PISTON);
            entries.add(ModBlocks.STRONG_SMART_STICKY_PISTON);
            entries.add(ModBlocks.STRONG_SMART_HONEY_PISTON);
        });
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
