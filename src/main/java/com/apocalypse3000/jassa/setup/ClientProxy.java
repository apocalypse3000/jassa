package com.apocalypse3000.jassa.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {
    @Override
    public void init() {
        }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
    }
