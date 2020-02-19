package com.apocalypse3000.jassa;

import com.apocalypse3000.jassa.blocks.*;
import com.apocalypse3000.jassa.config.Config;
import com.apocalypse3000.jassa.config.OreGen;
import com.apocalypse3000.jassa.items.*;
import com.apocalypse3000.jassa.setup.*;
import com.apocalypse3000.jassa.soulShard.Tiers;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("jassa")
public class Jassa {
    public static IProxy proxy = DistExecutor.runForDist( () -> () -> new ClientProxy(), () -> () -> new ServerProxy() );
    public static final Logger LOGGER = LogManager.getLogger();
    public static ModSetup setup = new ModSetup();
    private static final String MODID = "jassa";
    public static GameRules.RuleKey<GameRules.BooleanValue> allowCageSpawns;
    public static final JassaItemGroup jassa = new JassaItemGroup("JASSA", ()->(ModItems.RAWSOULSHARD)) {
        @OnlyIn(Dist.CLIENT)
        public void fill(NonNullList<ItemStack> items) {
            items.add(new ItemStack(ModBlocks.SOULBLOCK));
            items.add(new ItemStack(ModBlocks.SOULCAGE));
            items.add(new ItemStack(ModBlocks.SOULFACTORY));
            items.add(new ItemStack(ModBlocks.SOULORE));
            items.add(new ItemStack(ModItems.INFUSEDSOULSHARD));
            items.add(new ItemStack(ModItems.RAWSOULSHARD));
            items.add(new ItemStack(ModItems.SOULINGOT));
            items.add(new ItemStack(ModItems.SOULFRAGMENT));
            for(Item item : ForgeRegistries.ITEMS) {
                item.fillItemGroup(this, items);
            }
        }
    };
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
            Item.Properties properties = new Item.Properties();
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
