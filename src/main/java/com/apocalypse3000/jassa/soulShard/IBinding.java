package com.apocalypse3000.jassa.soulShard;


import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public interface IBinding {
    UUID owner();
    ResourceLocation boundMob();
    int kills();
    IBinding addKills(int amount);
    IShardTier tier();
}
