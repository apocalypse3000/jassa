package com.apocalypse3000.jassa.config;

import com.apocalypse3000.jassa.Jassa;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

@Mod.EventBusSubscriber
public class Config {
	private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec server_config;
	static {
		OreGenConfig.init(SERVER_BUILDER);
		server_config = SERVER_BUILDER.build();
	}
	public static void loadConfig(ForgeConfigSpec config, String path) {
		Jassa.LOGGER.info("Loading Config: " + path);
		final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave()
				.writingMode(WritingMode.REPLACE).build();
		Jassa.LOGGER.info("Built Config " + path);
		file.load();
		Jassa.LOGGER.info("Loaded Config" + path);
		config.setConfig(file);
	}
}
