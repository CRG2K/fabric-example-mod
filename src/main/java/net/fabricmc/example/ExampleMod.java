package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "modid";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	// Define our new red block
	public static final Block RED_BLOCK = new Block(FabricBlockSettings.of(Material.STONE)
		.strength(4.0f)  // Mining time/hardness
		.requiresTool()  // Requires correct tool to drop
	);

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
