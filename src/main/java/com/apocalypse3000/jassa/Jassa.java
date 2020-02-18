package com.apocalypse3000.jassa;

import com.apocalypse3000.jassa.blocks.*;
import com.apocalypse3000.jassa.config.Config;
import com.apocalypse3000.jassa.config.OreGen;
import com.apocalypse3000.jassa.items.InfusedSoulShard;
import com.apocalypse3000.jassa.items.RawSoulShard;
import com.apocalypse3000.jassa.items.SoulFragment;
import com.apocalypse3000.jassa.items.SoulIngot;
import com.apocalypse3000.jassa.setup.ClientProxy;
import com.apocalypse3000.jassa.setup.IProxy;
import com.apocalypse3000.jassa.setup.ModSetup;
import com.apocalypse3000.jassa.setup.ServerProxy;
import com.apocalypse3000.jassa.soulShard.Tiers;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.apocalypse3000.jassa.setup.ModSetup.jassa;

@Mod("jassa")
public class Jassa {
    public static IProxy proxy = DistExecutor.runForDist( () -> () -> new ClientProxy(), () -> () -> new ServerProxy() );
    public static final Logger LOGGER = LogManager.getLogger();
    public static ModSetup setup = new ModSetup();
    private static final String MODID = "jassa";
    public static GameRules.RuleKey<GameRules.BooleanValue> allowCageSpawns;

    public Jassa() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.server_config, "jassa-server.toml");
        Config.loadConfig(Config.server_config, FMLPaths.CONFIGDIR.get().resolve("jassa-server.toml").toString());
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        OreGen.setupOreGeneration();
        setup.initClient();
        proxy.init();
    }

    public static String getMODID() {
        return MODID;
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            LOGGER.info("Blocks registered");
            event.getRegistry().register(new SoulBlock());
            event.getRegistry().register(new SoulOre());
            event.getRegistry().register(new SoulCage());
            event.getRegistry().register(new SoulFactory());
        }
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            Tiers.readTiers();
            Item.Properties properties = new Item.Properties().group(jassa);
            event.getRegistry().register(new BlockItem(ModBlocks.SOULBLOCK, properties).setRegistryName("soulblock"));
            event.getRegistry().register(new BlockItem(ModBlocks.SOULORE, properties).setRegistryName("soulore"));
            event.getRegistry().register(new BlockItem(ModBlocks.SOULCAGE, properties).setRegistryName("soulcage"));
            event.getRegistry().register(new BlockItem(ModBlocks.SOULFACTORY, properties).setRegistryName("soulfactory"));
            event.getRegistry().register(new SoulFragment().setRegistryName("soulfragment"));
            event.getRegistry().register(new RawSoulShard().setRegistryName("rawsoulshard"));
            event.getRegistry().register(new InfusedSoulShard().setRegistryName("infusedsoulshard"));
            event.getRegistry().register(new SoulIngot().setRegistryName("soulingot"));

        }
        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event){
            event.getRegistry().register(TileEntityType.Builder.create(SoulCageTile::new, ModBlocks.SOULCAGE).build(null).setRegistryName("soulcage"));
        }
    }
}
