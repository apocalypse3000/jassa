package com.apocalypse3000.jassa.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class SoulOre extends Block {
    public SoulOre(){
        super(Block.Properties.create(Material.IRON)
                .hardnessAndResistance(2.0f, 3.0f)
                .lightValue(3)
                .sound(SoundType.STONE)
                .harvestLevel(2)
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("soulore");
    }
}
