package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "modid";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	// Define our new red block with a custom class
	public static final Block RED_BLOCK = new ConvertingBlock(FabricBlockSettings.of(Material.STONE)
		.strength(4.0f)  // Mining time/hardness
		.requiresTool()  // Requires correct tool to drop
	);

	// Custom block class that converts grass to itself
	public static class ConvertingBlock extends Block {
		public ConvertingBlock(Settings settings) {
			super(settings);
		}

		@Override
		public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
			world.scheduleBlockTick(pos, this, 1); // Schedule tick immediately
		}
		
		@Override
		public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
			// Check all adjacent blocks
			for (Direction direction : Direction.values()) {
				BlockPos neighborPos = pos.offset(direction);
				BlockState neighborState = world.getBlockState(neighborPos);
				
				// If the neighboring block is grass, convert it to our red block
				if (neighborState.isOf(Blocks.GRASS_BLOCK)) {
					world.setBlockState(neighborPos, RED_BLOCK.getDefaultState());
					LOGGER.info("Converted grass block to red block at " + neighborPos);
				}
			}
			
			// Schedule the next tick to happen immediately
			world.scheduleBlockTick(pos, this, 1);
		}
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		
		// Register our block
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "red_block"), RED_BLOCK);
		
		// Register the block item (so it can appear in inventory)
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "red_block"), 
			new BlockItem(RED_BLOCK, new FabricItemSettings()));
			
		// Add our block to the building blocks group
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
			content.add(RED_BLOCK);
		});
	}
}
