package com.moon404.gunskills.item.skill;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.moon404.gunskills.init.GunSkillsConfigs;
import com.moon404.gunskills.init.GunSkillsEffects;
import com.moon404.gunskills.init.GunSkillsKeyMappings;
import com.moon404.gunskills.struct.ClassType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkillItem extends Item
{
    public final int cooldown;
    public final int useType;
    public final ClassType classType;
    protected List<Component> tooltips;

    public SkillItem(Properties properties, int cooldown, int useType, ClassType classType)
    {
        super(properties);
        this.cooldown = cooldown;
        this.useType = useType;
        this.classType = classType;
        this.tooltips = new ArrayList<>();
    }

    // 该玩家是否可以使用这个技能物品
    public boolean canUse(Player player)
    {
        if (ClassType.getClass(player) != this.classType) return false;
        if (player.hasEffect(GunSkillsEffects.SILENCE.get())) return false;
        if (player.getCooldowns().isOnCooldown(this)) return false;
        return true;
    }

    public int getCooldown(Player player)
    {
        Inventory inventory = player.getInventory();
        int count = inventory.countItem(this);
        int unit;
        if (count <= 1) unit = 20;
        else if (count == 2) unit = 16;
        else if (count == 3) unit = 13;
        else if (count == 4) unit = 11;
        else unit = 10;
        return this.cooldown * unit;
    }

    public void enterCooldown(Player player)
    {
        if (player.isCreative()) return;
        player.getCooldowns().addCooldown(this, this.getCooldown(player));
    }
    
    public void active(Player player)
    {

    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Inventory inventory = player.getInventory();
        int slot = inventory.findSlotMatchingItem(pStack);
        int slota = GunSkillsConfigs.DROPA_INDEX.get().intValue();
        int slotb = GunSkillsConfigs.DROPB_INDEX.get().intValue();
        int slotc = GunSkillsConfigs.DROPC_INDEX.get().intValue();
        if (this.classType != null)
            pTooltipComponents.add(Component.translatable("skill.gunskills.class.limit").append(this.classType.getDisplay()));
        if (this.useType == 1)
            pTooltipComponents.add(Component.translatable("skill.gunskills.use.1"));
        else if (this.useType == 2 || this.useType == 3)
            if (slot == slota - 1)
                pTooltipComponents.add(Component.translatable("skill.gunskills.tooltip.2", GunSkillsKeyMappings.DROPA_KEY.getKey().getDisplayName(), Component.translatable("skill.gunskills.use." + this.useType)));
            else if (slot == slotb - 1)
                pTooltipComponents.add(Component.translatable("skill.gunskills.tooltip.2", GunSkillsKeyMappings.DROPB_KEY.getKey().getDisplayName(), Component.translatable("skill.gunskills.use." + this.useType)));
            else if (slot == slotc - 1)
                pTooltipComponents.add(Component.translatable("skill.gunskills.tooltip.2", GunSkillsKeyMappings.DROPC_KEY.getKey().getDisplayName(), Component.translatable("skill.gunskills.use." + this.useType)));
            else
                pTooltipComponents.add(Component.translatable("skill.gunskills.tooltip.1", slota, slotb, slotc));
        else if (this.useType == 4)
            pTooltipComponents.add(Component.translatable("skill.gunskills.use.4", minecraft.options.keyDrop.getKey().getDisplayName()));
        if (this.cooldown > 0)
            pTooltipComponents.add(Component.translatable("skill.gunskills.cooldown", this.getCooldown(player) / 20));
        pTooltipComponents.addAll(this.tooltips);
    }
}
