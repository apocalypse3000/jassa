package com.apocalypse3000.jassa.items;

import com.apocalypse3000.jassa.Jassa;
import com.apocalypse3000.jassa.blocks.ModBlocks;
import com.apocalypse3000.jassa.blocks.SoulCageTile;
import com.apocalypse3000.jassa.config.ModConfig;
import com.apocalypse3000.jassa.setup.ModSetup;
import com.apocalypse3000.jassa.soulShard.Binding;
import com.apocalypse3000.jassa.soulShard.ISoulShard;
import com.apocalypse3000.jassa.soulShard.Tiers;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;

public class InfusedSoulShard extends Item implements ISoulShard {

    private static final Method GET_ENTITY_ID_METHOD;

    static {
        GET_ENTITY_ID_METHOD = ObfuscationReflectionHelper.findMethod(AbstractSpawner.class, "func_190895_g");
    }

    public InfusedSoulShard() {
        super(new Properties().group(ModSetup.jassa)
                .maxStackSize(1));
        addPropertyOverride(new ResourceLocation(Jassa.getMODID(), "bound"), (stack, worldIn, entityIn) -> getBinding(stack) != null ? 1.0F : 0.0F);
        addPropertyOverride(new ResourceLocation(Jassa.getMODID(), "tier"), (stack, world, entity) -> {
            Binding binding = getBinding(stack);
            if (binding == null)
                return 0F;

            return Float.parseFloat("0." + Tiers.INDEXED.indexOf(binding.tier()));
        });
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getPlayer() == null)
            return ActionResultType.PASS;

        BlockState state = context.getWorld().getBlockState(context.getPos());
        ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
        Binding binding = getBinding(stack);
        if (binding == null)
            return ActionResultType.PASS;

        if (state.getBlock() instanceof SpawnerBlock) {
            if (!ModConfig.getBalance().allowSpawnerAbsorption()) {
                context.getPlayer().sendStatusMessage(new TranslationTextComponent("chat.jassa.absorb_disabled"), true);
                return ActionResultType.PASS;
            }

            if (binding.kills() >= Tiers.maxKills)
                return ActionResultType.PASS;

            MobSpawnerTileEntity mobSpawner = (MobSpawnerTileEntity) context.getWorld().getTileEntity(context.getPos());
            if (mobSpawner == null)
                return ActionResultType.PASS;

            try {
                ResourceLocation entityId = (ResourceLocation) GET_ENTITY_ID_METHOD.invoke(mobSpawner.getSpawnerBaseLogic());
                if (!ModConfig.getEntityList().isEnabled(entityId))
                    return ActionResultType.PASS;

                if (entityId == null || binding.boundMob() == null || !binding.boundMob().equals(entityId))
                    return ActionResultType.FAIL;

                updateBinding(stack, binding.addKills(ModConfig.getBalance().getAbsorptionBonus()));
                context.getWorld().destroyBlock(context.getPos(), false);
                return ActionResultType.SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (state.getBlock() == ModBlocks.SOULCAGE) {
            if (binding.boundMob() == null)
                return ActionResultType.FAIL;

            SoulCageTile cage = (SoulCageTile) context.getWorld().getTileEntity(context.getPos());
            if (cage == null)
                return ActionResultType.PASS;

            IItemHandler itemHandler = cage.getInventory();
            if (itemHandler != null && itemHandler.getStackInSlot(0).isEmpty()) {
                ItemHandlerHelper.insertItem(itemHandler, stack.copy(), false);
                cage.markDirty();
                cage.setState(true);
                context.getPlayer().setHeldItem(context.getHand(), ItemStack.EMPTY);
                return ActionResultType.SUCCESS;
            }
        }

        return super.onItemUse(context);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Binding binding = getBinding(stack);
        if (binding == null)
            return;

        if (binding.boundMob() != null) {
            EntityType<?> entityEntry = ForgeRegistries.ENTITIES.getValue(binding.boundMob());
            if (entityEntry != null)
                tooltip.add(new TranslationTextComponent("tooltip.jassa.bound", entityEntry.getName()));
        }

        tooltip.add(new TranslationTextComponent("tooltip.jassa.tier", binding.tier().index()));
        tooltip.add(new TranslationTextComponent("tooltip.jassa.kills", binding.kills()));
        if (flag.isAdvanced() && binding.owner() != null)
            tooltip.add(new TranslationTextComponent("tooltip.jassa.owner", binding.owner().toString()));
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        Binding binding = getBinding(stack);
        return super.getTranslationKey(stack) + (binding == null || binding.boundMob() == null ? "_unbound" : "");
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        Binding binding = getBinding(stack);
        return binding != null && binding.kills() >= Tiers.maxKills;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        Binding binding = getBinding(stack);
        return ModConfig.getClient().displayDurabilityBar() && binding != null && binding.kills() < Tiers.maxKills;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        Binding binding = getBinding(stack);
        if (binding == null)
            return 1.0D;

        return 1.0D - ((double) binding.kills() / (double) Tiers.maxKills);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return super.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return getBinding(stack) == null ? 64 : 1;
    }

    @Nullable
    public Binding getBinding(ItemStack stack) {
        return Binding.fromNBT(stack);
    }

    public void updateBinding(ItemStack stack, Binding binding) {
        if (!stack.hasTag())
            stack.setTag(new CompoundNBT());

        stack.getTag().put("binding", binding.serializeNBT());
    }
}
