package com.moon404.gunskills.handler;

import com.moon404.gunskills.GunSkills;
import com.moon404.gunskills.init.GunSkillsConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;


@Mod.EventBusSubscriber(modid = GunSkills.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GunOverLimit {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer player) {
            if (event.phase == TickEvent.Phase.END && event.player.tickCount % 10 == 0) {
                Inventory inventory = player.getInventory();
                if (getGunNum(inventory) > GunSkills.MAX_GUN_NUMBER) {
                    dropExcessGuns(inventory, player);
                    player.sendSystemMessage(Component.translatable("message.gunskills.gun_hold_over_limit").withStyle(ChatFormatting.RED));
                    player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 0.9F, 1.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        Inventory inventory = player.getInventory();
        ItemEntity pickedUpItemEntity = event.getItem();
        ItemStack pickedUpStack = pickedUpItemEntity.getItem();

        if (isGun(pickedUpStack)) {
            event.setCanceled(getGunNum(inventory) >= GunSkills.MAX_GUN_NUMBER);
        }
    }

    private static boolean isGun(ItemStack stack){
        if (stack.isEmpty()) {
            return false;
        }

        boolean gunIdMatch = false;
        for (String gunId : GunSkillsConfigs.Gun_ID.get()) {
            gunIdMatch = gunId.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            if (gunId.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()).toString())) {
                gunIdMatch = true;
                break;
            }
        }

        boolean gunNbtNoMatch = true;
        for (String nbt_values : GunSkillsConfigs.Banished_Gun_Nbt_values.get()) {
            CompoundTag nbt = stack.getTag();

            String nbt_key = GunSkillsConfigs.Banished_Gun_Nbt_key.get();
            if (nbt != null && nbt.contains(nbt_key)) {
                if (nbt.getString(nbt_key).equals(nbt_values)) {
                    gunNbtNoMatch = false;
                    break;
                }
            }
        }

        return gunIdMatch && gunNbtNoMatch;
    }

    private static int getGunNum(Inventory inventory) {
        int gunNum = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (isGun(itemStack)) {
                gunNum++;
            }
        }
        return gunNum;
    }

    private static void dropExcessGuns(Inventory inventory, Player player) {
        int gunToDropNum = getGunNum(inventory) - GunSkills.MAX_GUN_NUMBER;
        for (int i = 35; i >= 0; i--) {
            ItemStack itemStack = inventory.getItem(i);
            if (gunToDropNum <= 0) {
                return;
            }
            if (isGun(itemStack)) {
                player.drop(itemStack, false);
                inventory.setItem(i, ItemStack.EMPTY);
                gunToDropNum--;
            }
        }
    }
}
