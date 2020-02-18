package com.apocalypse3000.jassa.setup;

import com.apocalypse3000.jassa.items.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {
    public static final ItemGroup jassa = new ItemGroup("JASSA") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.RAWSOULSHARD);
        }
    };
    public void initClient(){
        /*RenderTypeLookup.setRenderLayer(ModBlocks.SOULCAGE, BlockRenderLayer.CUTOUT());*/
    }
}
