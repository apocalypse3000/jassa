package com.apocalypse3000.jassa.blocks;

import com.apocalypse3000.jassa.config.ModConfig;
import com.apocalypse3000.jassa.items.InfusedSoulShard;
import com.apocalypse3000.jassa.soulCage.CageSpawnEvent;
import com.apocalypse3000.jassa.soulShard.Binding;
import com.apocalypse3000.jassa.soulShard.IShardTier;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.apocalypse3000.jassa.blocks.ModBlocks.SOULCAGE_TILE;

public class SoulCageTile extends TileEntity implements ITickableTileEntity {
    private ItemStackHandler inventory;
    private int time;
    private boolean active = false;
    public SoulCageTile(){
        super(SOULCAGE_TILE);
        this.inventory = new SoulCageInventory();
        }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public void tick() {
        if (world.isRemote)
            return;

        ActionResult<Binding> result = canSpawn();
        if (result.getType() != ActionResultType.SUCCESS) {
            if (active) {
                setState(false);
                world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
            }
            return;
        }
        if (!active) {
        setState(true);
        world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
    }
    time++;

        if(time % result.getResult().tier().cooldown() == 0)
    spawnEntities();
}
    private void spawnEntities() {
        Binding binding = binding();
        if (binding == null || binding.boundMob() == null)
            return;

        EntityType<?> entityEntry = ForgeRegistries.ENTITIES.getValue(binding.boundMob());
        if (entityEntry == null)
            return;

        IShardTier tier = binding.tier();
        for (int i = 0; i < tier.spawnCount(); i++) {
            for (int attempts = 0; attempts < 5; attempts++) {
                double x = getPos().getX() + (getWorld().rand.nextDouble() - getWorld().rand.nextDouble()) * 4.0D + 0.5D;
                double y = getPos().getY() + getWorld().rand.nextInt(3);
                double z = getPos().getZ() + (getWorld().rand.nextDouble() - getWorld().rand.nextDouble()) * 4.0D + 0.5D;
                BlockPos spawnAt = new BlockPos(x, y, z);

                if (spawnAt.equals(getPos()))
                    spawnAt = new BlockPos(x, y + 1, z);

                LivingEntity entityLiving = (LivingEntity) entityEntry.create(getWorld());
                if (entityLiving == null)
                    continue;

                if (binding.tier().lightLevel() && !canSpawnInLight(entityLiving, spawnAt))
                    continue;

                entityLiving.moveToBlockPosAndAngles(spawnAt, getWorld().rand.nextFloat() * 360F, 0F);
                entityLiving.getPersistentData().putBoolean("cageBorn", true);
                entityLiving.forceSpawn = true;

                if (entityLiving.isAlive() && !hasReachedSpawnCap(entityLiving) && getWorld().checkNoEntityCollision(entityLiving)) { // func_226668_i_ -> checkNoEntityCollision
                    if (ModConfig.getBalance().allowBossSpawns() && !entityLiving.isNonBoss())
                        continue;

                    CageSpawnEvent event = new CageSpawnEvent(binding, inventory.getStackInSlot(0), entityLiving);
                    if (MinecraftForge.EVENT_BUS.post(event))
                        continue;

                    getWorld().addEntity(entityLiving);
                    if (entityLiving instanceof MobEntity)
                        ((MobEntity) entityLiving).onInitialSpawn(getWorld(), getWorld().getDifficultyForLocation(spawnAt), SpawnReason.SPAWNER, null, null);
                    break;
                }
            }
        }
    }

    private ActionResult<Binding> canSpawn() {
       BlockState state = getBlockState();
        if (state.getBlock() != ModBlocks.SOULCAGE)
           return new ActionResult<>(ActionResultType.FAIL, null);

        ItemStack shardStack = inventory.getStackInSlot(0);
        if (shardStack.isEmpty() || !(shardStack.getItem() instanceof InfusedSoulShard))
            return new ActionResult<>(ActionResultType.FAIL, null);

        Binding binding = binding();
        if (binding == null || binding.boundMob() == null)
            return new ActionResult<>(ActionResultType.FAIL, binding);

        IShardTier tier = binding.tier();

        if (tier.spawnCount() == 0)
            return new ActionResult<>(ActionResultType.FAIL, binding);

        if (ModConfig.getBalance().requireOwnerOnline() && !ownerOnline())
            return new ActionResult<>(ActionResultType.FAIL, binding);

        if (ModConfig.getEntityList().isEnabled(binding.boundMob()))
            return new ActionResult<>(ActionResultType.FAIL, binding);

        if (ModConfig.getBalance().requireRedstoneSignal()) {
            if (state.get(SoulCage.POWERED) && tier.checkRs())
                return new ActionResult<>(ActionResultType.FAIL, binding);
        } else if (!state.get(SoulCage.POWERED))
            return new ActionResult<>(ActionResultType.FAIL, binding);

        if (tier.playerNick() && getWorld().getClosestPlayer(getPos().getX(), getPos().getY(), getPos().getZ(), 16, false) == null)
            return new ActionResult<>(ActionResultType.FAIL, binding);

        return new ActionResult<>(ActionResultType.SUCCESS, binding);
    }

    private boolean canSpawnInLight(LivingEntity entityLiving, BlockPos pos) {
        return !(entityLiving instanceof MonsterEntity) || world.getLightValue(pos) <= 8;
    }

    private boolean hasReachedSpawnCap(LivingEntity living) {
        AxisAlignedBB box = new AxisAlignedBB(getPos().getX() - 16, getPos().getY() - 16, getPos().getZ() - 16, getPos().getX() + 16, getPos().getY() + 16, getPos().getZ() + 16);

        int mobCount = world.getEntitiesWithinAABB(living.getClass(), box, e -> e != null && e.getPersistentData().getBoolean("cageBorn")).size();
        return mobCount >= ModConfig.getBalance().getSpawnCap();
    }

    private boolean isColliding(LivingEntity entity) {
        return world.areCollisionShapesEmpty(entity.getBoundingBox()) && world.getEntitiesWithinAABB(LivingEntity.class, entity.getBoundingBox(), e -> true).isEmpty();
    }

    public void setState(boolean active) {
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof SoulCage))
            return;

        world.setBlockState(getPos(), state.with(SoulCage.ACTIVE, active));
        this.active = active;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.time = tag.getInt("time");
        this.active = tag.getBoolean("active");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("time", time);
        tag.putBoolean("active", active);

        return super.write(tag);
    }
    @Nullable
    public Binding binding() {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof InfusedSoulShard))
            return null;

        return ((InfusedSoulShard) stack.getItem()).getBinding(stack);
    }
    public boolean ownerOnline() {
        Binding binding = binding();
        return binding != null && binding.owner() != null && world.getServer().getPlayerList().getPlayerByUUID(binding.owner()) == null;
    }
    public static class SoulCageInventory extends ItemStackHandler {

        public SoulCageInventory() {
            super(1);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!(stack.getItem() instanceof InfusedSoulShard))
                return stack;

            Binding binding = ((InfusedSoulShard) stack.getItem()).getBinding(stack);
            if (binding == null || binding.boundMob() == null || ModConfig.getEntityList().isEnabled(binding.boundMob()))
                return stack;

            return super.insertItem(slot, stack, simulate);
        }
    }
}