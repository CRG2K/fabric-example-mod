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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        
        // Generate textures directly (not through the data generation system)
        generateTextures();
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
    
    // Method to generate textures directly
    private static void generateTextures() {
        try {
            // First, generate the texture in memory
            BufferedImage redBlockTexture = createBlockTexture(Color.RED);
            
            // Get the current working directory (should be in the build directory)
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current working directory: " + currentDir);
            
            // Create the textures directory in the build directory
            File buildTexturesDir = new File("src/main/resources/assets/modid/textures/block");
            buildTexturesDir.mkdirs();
            
            // Save the texture to the build directory first
            File buildOutputFile = new File(buildTexturesDir, "red_block.png");
            ImageIO.write(redBlockTexture, "PNG", buildOutputFile);
            System.out.println("Generated texture in build directory: " + buildOutputFile.getAbsolutePath());
            
            // Find the root project directory
            Path currentPath = Paths.get(currentDir);
            Path projectRoot;
            
            // Navigate up directories until we find the project root (where build.gradle is)
            if (currentDir.contains("build")) {
                // We're in a build directory, go up until we find the project root
                projectRoot = findProjectRoot(currentPath);
            } else {
                // We're already in the project root
                projectRoot = currentPath;
            }
            
            if (projectRoot != null) {
                // Create the destination directory in the project root
                Path projectTexturesDir = projectRoot.resolve("src/main/resources/assets/modid/textures/block");
                Files.createDirectories(projectTexturesDir);
                
                // Copy the texture to the project's resources directory
                Path srcTexture = buildOutputFile.toPath();
                Path destTexture = projectTexturesDir.resolve("red_block.png");
                Files.copy(srcTexture, destTexture, StandardCopyOption.REPLACE_EXISTING);
                
                System.out.println("Copied texture to project directory: " + destTexture);
            } else {
                System.err.println("Could not find project root!");
            }
            
            System.out.println("Texture generation complete!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Path findProjectRoot(Path startingDir) {
        Path currentDir = startingDir;
        // Go up directories until we find build.gradle or reach the filesystem root
        while (currentDir != null && !Files.exists(currentDir.resolve("build.gradle"))) {
            currentDir = currentDir.getParent();
        }
        return currentDir;
    }
    
    private static BufferedImage createBlockTexture(Color baseColor) {
        // Create a 16x16 texture
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        // Fill with base color
        g.setColor(baseColor);
        g.fillRect(0, 0, size, size);
        
        // Add some shading for a simple 3D effect
        g.setColor(darken(baseColor, 0.2f));
        g.fillRect(0, 0, size, 1);  // top edge
        g.fillRect(0, 0, 1, size);  // left edge
        
        g.setColor(lighten(baseColor, 0.2f));
        g.fillRect(0, size-1, size, 1);  // bottom edge
        g.fillRect(size-1, 0, 1, size);  // right edge
        
        g.dispose();
        return image;
    }
    
    private static Color darken(Color color, float factor) {
        return new Color(
            Math.max((int)(color.getRed() * (1 - factor)), 0),
            Math.max((int)(color.getGreen() * (1 - factor)), 0),
            Math.max((int)(color.getBlue() * (1 - factor)), 0)
        );
    }
    
    private static Color lighten(Color color, float factor) {
        return new Color(
            Math.min((int)(color.getRed() * (1 + factor)), 255),
            Math.min((int)(color.getGreen() * (1 + factor)), 255),
            Math.min((int)(color.getBlue() * (1 + factor)), 255)
        );
    }
} 