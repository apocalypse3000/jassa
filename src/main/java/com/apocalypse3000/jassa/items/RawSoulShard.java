package com.apocalypse3000.jassa.items;

import com.apocalypse3000.jassa.setup.ModSetup;
import net.minecraft.item.Item;

public class RawSoulShard extends Item {
    public RawSoulShard(){
        super(new Item.Properties().group(ModSetup.jassa)
        .maxStackSize(1)
        );
    }
}