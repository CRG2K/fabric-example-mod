package net.fabricmc.example;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ExampleModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        
        // Add model provider
        pack.addProvider(ExampleModelGenerator::new);
        
        // Add loot table provider
        pack.addProvider(ExampleLootTableGenerator::new);
        
        // Add recipe provider
        pack.addProvider(ExampleRecipeGenerator::new);
    }
    
    // Model generator to handle block and item models
    private static class ExampleModelGenerator extends FabricModelProvider {
        public ExampleModelGenerator(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            // Register the red block model
            blockStateModelGenerator.registerSimpleCubeAll(ExampleMod.RED_BLOCK);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            // Item models for blocks are auto-generated, so we don't need to do anything here
        }
    }
    
    // Loot table generator for the block drops
    private static class ExampleLootTableGenerator extends FabricBlockLootTableProvider {
        public ExampleLootTableGenerator(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate() {
            // Add loot table for red block (drops itself)
            addDrop(ExampleMod.RED_BLOCK);
        }
    }
    
    // Recipe generator for crafting recipes
    private static class ExampleRecipeGenerator extends FabricRecipeProvider {
        public ExampleRecipeGenerator(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
            // Add a recipe for the red block
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ExampleMod.RED_BLOCK)
                .pattern("RRR")
                .pattern("RDR")
                .pattern("RRR")
                .input('R', Items.REDSTONE)
                .input('D', Items.RED_DYE)
                .criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
                .offerTo(exporter, new Identifier(ExampleMod.MOD_ID, "red_block"));
        }
    }
} 