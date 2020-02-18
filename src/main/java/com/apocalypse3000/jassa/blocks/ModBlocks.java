package com.apocalypse3000.jassa.blocks;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {
    @ObjectHolder("jassa:soulblock")
    public static SoulBlock SOULBLOCK;
    @ObjectHolder("jassa:soulore")
    public static SoulOre SOULORE;
    @ObjectHolder("jassa:soulfactory")
    public static SoulFactory SOULFACTORY;
    @ObjectHolder("jassa:soulcage")
    public static SoulCage SOULCAGE;
    @ObjectHolder("jassa:soulcage")
    public static TileEntityType<SoulCageTile> SOULCAGE_TILE;
}
