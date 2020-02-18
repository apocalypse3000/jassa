package com.apocalypse3000.jassa.soulShard;

import com.apocalypse3000.jassa.Jassa;
import com.apocalypse3000.jassa.setup.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLConfig;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

public class Tiers implements IShardTier {
    public static final TreeMap<Integer, IShardTier> TIERS = Maps.newTreeMap();
    public static final List<IShardTier> INDEXED = Lists.newArrayList();
    public static int maxKills;
    private final int killRequirement;
    private final boolean playerNick;
    private final boolean lightLevel;
    private final boolean checkRs;
    private final int spawnCount;
    private final int cooldown;

    public Tiers(int killRequirement, boolean playerNick, boolean lightLevel, boolean checkRs, int spawnCount, int cooldown) {
        this.killRequirement = killRequirement;
        this.playerNick = playerNick;
        this.lightLevel = lightLevel;
        this.checkRs = checkRs;
        this.spawnCount = spawnCount;
        this.cooldown = cooldown;
    }

    @Override
    public int killRequirement() {
        return killRequirement;
    }

    @Override
    public boolean playerNick() {
        return playerNick;
    }

    @Override
    public boolean lightLevel() {
        return lightLevel;
    }

    public boolean checkRs() {
        return checkRs;
    }

    @Override
    public int spawnCount() {
        return spawnCount;
    }

    @Override
    public int cooldown() {
        return cooldown;
    }

    @Override
    public int index() {
        return INDEXED.indexOf(this);
    }
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Tiers)) return false;

        Tiers tier = (Tiers) object;

        return killRequirement == tier.killRequirement;
    }
    @Override
    public int hashCode() {
        return killRequirement;
    }
    public static void readTiers() {
        Tiers[] tiers = JsonUtil.fromJson(TypeToken.get(Tiers[].class), new File(FMLConfig.defaultConfigPath(), Jassa.getMODID() + "/tiers.json"), generateDefaults());
        for (Tiers tier : tiers) {
            TIERS.put(tier.killRequirement, tier);
            INDEXED.add(tier);
            if (tier.killRequirement() > maxKills)
                maxKills = tier.killRequirement();
        }
    }
    private static Tiers[] generateDefaults() {
        return new Tiers[]{
                new Tiers(0, true, false, false,0, 0),
                new Tiers(30, true, true,false,1, 30 * 20),
                new Tiers(250, true, true,false,3, 20 * 20),
                new Tiers(500, false, true,false,3, 15 * 20),
                new Tiers(750, false, false,false,4, 10 * 20),
                new Tiers(1024, false, false,false,5, 3 * 20)
        };
    }
}
