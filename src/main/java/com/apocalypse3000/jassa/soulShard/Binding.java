package com.apocalypse3000.jassa.soulShard;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public class Binding implements IBinding, INBTSerializable<CompoundNBT> {
    @Nullable
    private ResourceLocation boundMob;
    @Nullable
    private UUID owner;
    private int kills;

    public Binding(ResourceLocation boundMob, UUID owner, int kills) {
        this.boundMob = boundMob;
        this.owner = owner;
        this.kills = kills;
    }

    public Binding(ResourceLocation boundMob, int kills) {
        this(boundMob, null, kills);
    }

    public Binding(CompoundNBT bindingTag) {
        deserializeNBT(bindingTag);
    }

    @Nullable
    @Override
    public ResourceLocation boundMob() {
        return boundMob;
    }

    public Binding setBoundMob(@Nullable ResourceLocation boundMob) {
        this.boundMob = boundMob;
        return this;
    }

    @Nullable
    @Override
    public UUID owner() {
        return owner;
    }

    public Binding setOwner(@Nullable UUID owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public int kills() {
        return kills;
    }

    public Binding setKills(int kills) {
        this.kills = Math.min(Tiers.maxKills, kills);
        return this;
    }

    @Override
    public Binding addKills(int kills) {
        this.kills = Math.min(Tiers.maxKills, this.kills + kills);
        return this;
    }

    @Override
    public IShardTier tier() {
        return Tiers.TIERS.floorEntry(kills).getValue();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();

        if (boundMob != null)
            tag.putString("bound", boundMob.toString());
        if (owner != null)
            tag.putUniqueId("owner", owner);
        tag.putInt("kills", kills);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("bound"))
            this.boundMob = new ResourceLocation(nbt.getString("bound"));
        if (nbt.hasUniqueId("owner"))
            this.owner = nbt.getUniqueId("owner");
        this.kills = nbt.getInt("kills");
    }

    @Nullable
    public static Binding fromNBT(ItemStack stack) {
        if (!stack.hasTag())
            return null;

        CompoundNBT tag = stack.getTag();
        if (!tag.contains("binding"))
            return null;

        return new Binding(tag.getCompound("binding"));
    }
}
