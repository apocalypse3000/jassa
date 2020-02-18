package com.apocalypse3000.jassa.config;

import com.apocalypse3000.jassa.blocks.ModBlocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

public class OreGen {
    public static void setupOreGeneration() {
        for(Biome biome : ForgeRegistries.BIOMES) {
            biome.addFeature(Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig
                            (FillerBlockType.NATURAL_STONE, ModBlocks.SOULORE.getDefaultState(), OreGenConfig.SoulOreChance.get()),
                    Placement.COUNT_RANGE, new CountRangeConfig(OreGenConfig.SoulOreVein.get(), 1, 0, 20)));
        }
    }
}

