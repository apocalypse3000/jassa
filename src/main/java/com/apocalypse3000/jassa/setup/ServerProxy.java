package com.apocalypse3000.jassa.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {
    @Override
    public void init() {

    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Only run on client!");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("Only run on client!");
    }
}
