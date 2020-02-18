package com.apocalypse3000.jassa.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Set;

public class ModConfig {
    private static ConfigBalance balance;
    private static ConfigClient client;
    private static ConfigEntityList entityList;

    private ModConfig(ConfigBalance balance, ConfigClient client, ConfigEntityList entityList) {
        this.balance = balance;
        this.client = client;
        this.entityList = entityList;
    }

	/*public Config() {
		this(new ConfigBalance(), new ConfigClient(), new ConfigEntityList());
	}*/

    public static ConfigBalance getBalance() {
        return balance;
    }

    public static ConfigClient getClient() {
        return client;
    }

    public static ConfigEntityList getEntityList() {
        return entityList;
    }

    public static class ConfigBalance {

        private boolean allowSpawnerAbsorption;
        private boolean allowFakePlayers;
        private int absorptionBonus;
        private boolean allowBossSpawns;
        private boolean countCageBornForShard;
        private boolean requireOwnerOnline;
        private boolean requireRedstoneSignal;
        private boolean allowShardCombination;
        private int spawnCap;
        private boolean dropExp;

        public ConfigBalance(boolean allowSpawnerAbsorption, boolean allowFakePlayers, int absorptionBonus, boolean allowBossSpawns, boolean countCageBornForShard, boolean requireOwnerOnline, boolean requireRedstoneSignal, boolean allowShardCombination, int spawnCap, boolean dropExp) {
            this.allowSpawnerAbsorption = allowSpawnerAbsorption;
            this.allowBossSpawns = allowFakePlayers;
            this.absorptionBonus = absorptionBonus;
            this.allowBossSpawns = allowBossSpawns;
            this.countCageBornForShard = countCageBornForShard;
            this.requireOwnerOnline = requireOwnerOnline;
            this.requireRedstoneSignal = requireRedstoneSignal;
            this.allowShardCombination = allowShardCombination;
            this.spawnCap = spawnCap;
            this.dropExp = dropExp;
        }

        public ConfigBalance() {
            this(true,false, 200, false, false, false, false, true, 32, false);
        }

        public boolean allowSpawnerAbsorption() {
            return allowSpawnerAbsorption;
        }

        public boolean allowFakePlayers() {
            return allowFakePlayers;
        }

        public int getAbsorptionBonus() {
            return absorptionBonus;
        }

        public boolean allowBossSpawns() {
            return allowBossSpawns;
        }

        public boolean countCageBornForShard() {
            return countCageBornForShard;
        }

        public boolean requireOwnerOnline() {
            return requireOwnerOnline;
        }

        public boolean requireRedstoneSignal() {
            return requireRedstoneSignal;
        }

        public boolean allowShardCombination() {
            return allowShardCombination;
        }

        public int getSpawnCap() {
            return spawnCap;
        }

        public boolean dropExp(){
            return dropExp;
        }
    }

    public static class ConfigClient {
        private boolean displayDurabilityBar;

        public ConfigClient(boolean displayDurabilityBar) {
            this.displayDurabilityBar = displayDurabilityBar;
        }

        public ConfigClient() {
            this(true);
        }

        public boolean displayDurabilityBar() {
            return displayDurabilityBar;
        }
    }

    public static class ConfigEntityList {
        private static final Set<String> DEFAULT_DISABLES = Sets.newHashSet(
                "minecraft:armor_stand",
                "minecraft:elder_guardian",
                "minecraft:ender_dragon",
                "minecraft:wither",
                "minecraft:wither",
                "minecraft:player"
        );

        private Map<String, Boolean> entities;

        public ConfigEntityList(Map<String, Boolean> entities) {
            this.entities = entities;
        }

        public ConfigEntityList() {
            this(getDefaults());
        }

        public boolean isEnabled(ResourceLocation entityId) {
            return entities.getOrDefault(entityId.toString(), false);
        }

        private static Map<String, Boolean> getDefaults() {
            Map<String, Boolean> defaults = Maps.newHashMap();
            Registry.ENTITY_TYPE.stream()
                    .filter(e -> e.getClassification() != EntityClassification.MISC)
                    .forEach(e -> {
                        String entityId = e.getRegistryName().toString();
                        defaults.put(entityId, !DEFAULT_DISABLES.contains(entityId));
                    });
            return defaults;
        }
    }
}
