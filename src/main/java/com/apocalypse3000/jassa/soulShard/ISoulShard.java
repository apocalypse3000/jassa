package com.apocalypse3000.jassa.soulShard;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface ISoulShard {
    @Nullable
    IBinding getBinding(ItemStack stack);
}
