package com.apocalypse3000.jassa.soulCage;

import com.apocalypse3000.jassa.soulShard.IBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class CageSpawnEvent extends Event {
    private final IBinding shardBinding;
    private final ItemStack shardStack;
    private final LivingEntity toSpawn;

    public CageSpawnEvent(IBinding shardBinding, ItemStack shardStack, LivingEntity toSpawn){
        this.shardBinding = shardBinding;
        this.shardStack = shardStack;
        this.toSpawn = toSpawn;
    }

    public IBinding getShardBinding() {
        return shardBinding;
    }

    public ItemStack getShardStack() {
        return shardStack;
    }

    public LivingEntity getToSpawn() {
        return toSpawn;
    }
}
