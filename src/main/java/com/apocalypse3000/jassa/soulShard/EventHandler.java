package com.apocalypse3000.jassa.soulShard;

import com.apocalypse3000.jassa.config.ModConfig;
import com.apocalypse3000.jassa.items.InfusedSoulShard;
import com.apocalypse3000.jassa.items.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class EventHandler {
    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity)
            return;

        if (!ModConfig.getBalance().allowFakePlayers() && event.getSource().getTrueSource() instanceof FakePlayer)
            return;

        if (!ModConfig.getEntityList().isEnabled(event.getEntityLiving().getType().getRegistryName()))
            return;

        if (!ModConfig.getBalance().allowBossSpawns() && !event.getEntityLiving().isNonBoss())
            return;

        if (!ModConfig.getBalance().countCageBornForShard() && event.getEntityLiving().getPersistentData().getBoolean("cageBorn"))
            return;

        if (event.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();

            BindingEvent.GetEntityName getEntityName = new BindingEvent.GetEntityName(event.getEntityLiving());
            MinecraftForge.EVENT_BUS.post(getEntityName);
            ResourceLocation entityId = getEntityName.getEntityId() == null ?  ForgeRegistries.ENTITIES.getKey(event.getEntityLiving().getType()) : getEntityName.getEntityId();

            ItemStack shardItem = getFirstShard(player, entityId);
            if (shardItem.isEmpty())
                return;
            InfusedSoulShard soulShard = (InfusedSoulShard) shardItem.getItem();

            boolean newItem = false;
            Binding binding = soulShard.getBinding(shardItem);
            if (binding == null) {
                BindingEvent.NewBinding newBinding = new BindingEvent.NewBinding(event.getEntityLiving(), new Binding(null, 0));
                MinecraftForge.EVENT_BUS.post(newBinding);
                if (MinecraftForge.EVENT_BUS.post(newBinding))
                    return;

                if (shardItem.getCount() > 1) { // Peel off one blank shard from a stack of them
                    shardItem = shardItem.split(1);
                    newItem = true;
                }

                binding = (Binding) newBinding.getBinding();
            }

            ItemStack mainHand = player.getHeldItem(Hand.MAIN_HAND);
            int soulsGained = 1;
            BindingEvent.GainSouls gainSouls = new BindingEvent.GainSouls(event.getEntityLiving(), binding, soulsGained);
            MinecraftForge.EVENT_BUS.post(gainSouls);

            if (binding.boundMob() == null)
                binding.setBoundMob(entityId);

            if (binding.owner() == null)
                binding.setOwner(player.getGameProfile().getId());

            soulShard.updateBinding(shardItem, binding.addKills(gainSouls.getAmount()));
            if (newItem) // Give the player the peeled off stack
                ItemHandlerHelper.giveItemToPlayer(player, shardItem);
        }
    }

    @SubscribeEvent
    public static void onAnvil(AnvilUpdateEvent event) {
        if (!ModConfig.getBalance().allowShardCombination())
            return;

        if (event.getLeft().getItem() instanceof InfusedSoulShard && event.getRight().getItem() instanceof InfusedSoulShard) {
            Binding left = ((InfusedSoulShard) event.getLeft().getItem()).getBinding(event.getLeft());
            Binding right = ((InfusedSoulShard) event.getRight().getItem()).getBinding(event.getRight());

            if (left == null || right == null)
                return;

            if (left.boundMob() != null && left.boundMob().equals(right.boundMob())) {
                ItemStack output = new ItemStack(ModItems.INFUSEDSOULSHARD);
                ((InfusedSoulShard) output.getItem()).updateBinding(output, left.addKills(right.kills()));
                event.setOutput(output);
                event.setCost(left.tier().index() * 6);
            }
        }
    }

    @SubscribeEvent
    public static void dropExperience(LivingExperienceDropEvent event) {
        if (!ModConfig.getBalance().dropExp() && event.getEntityLiving().getPersistentData().getBoolean("cageBorn"))
            event.setCanceled(true);
    }

    @Nonnull
    public static ItemStack getFirstShard(PlayerEntity player, ResourceLocation entityId) {
        // Checks the offhand first
        ItemStack shardItem = player.getHeldItem(Hand.OFF_HAND);
        // If offhand isn't a shard, loop through the hotbar
        if (shardItem.isEmpty() || !(shardItem.getItem() instanceof InfusedSoulShard)) {
            for (int i = 0; i < 9; i++) {
                shardItem = player.inventory.getStackInSlot(i);
                if (!shardItem.isEmpty() && shardItem.getItem() instanceof InfusedSoulShard) {
                    Binding binding = ((InfusedSoulShard) shardItem.getItem()).getBinding(shardItem);

                    // If there's no binding or no bound entity, this is a valid shard
                    if (binding == null || binding.boundMob() == null)
                        return shardItem;

                    // If there is a bound entity and we're less than the max kills, this is a valid shard
                    if (binding.boundMob().equals(entityId) && binding.kills() < Tiers.maxKills)
                        return shardItem;
                }
            }
        } else { // If offhand is a shard, check it it
            Binding binding = ((InfusedSoulShard) shardItem.getItem()).getBinding(shardItem);

            // If there's no binding or no bound entity, this is a valid shard
            if (binding == null || binding.boundMob() == null)
                return shardItem;

            // If there is a bound entity and we're less than the max kills, this is a valid shard
            if (binding.boundMob().equals(entityId) && binding.kills() < Tiers.maxKills)
                return shardItem;
        }

        return ItemStack.EMPTY; // No shard found
    }
}
