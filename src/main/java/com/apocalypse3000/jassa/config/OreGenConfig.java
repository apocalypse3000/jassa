package com.apocalypse3000.jassa.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class OreGenConfig {
	public static ForgeConfigSpec.IntValue SoulOreChance;
	public static ForgeConfigSpec.IntValue SoulOreVein;
	
	public static void init(ForgeConfigSpec.Builder server) {
		server.comment("OreGen Config");
		
		SoulOreChance = server
				.comment("Maximum ore veins in one chunk. Maximum: 20")
				.defineInRange("oregen.SoulOreChance", 10, 1, 100); //default=10, min, max
		SoulOreVein = server
				.comment("Maximum number of ores per a vein. Maximum: 8")
				.defineInRange("oregen.SoulOreVein", 3, 1, 8); //default=3, min, max
	}
}