package com.apocalypse3000.jassa.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class SoulFactory extends Block {
    public SoulFactory(){
        super(Properties.create(Material.IRON)
                .hardnessAndResistance(2.0f, 3.0f)
                .sound(SoundType.METAL)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("soulfactory");
    }
}