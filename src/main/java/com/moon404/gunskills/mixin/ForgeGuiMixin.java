package com.moon404.gunskills.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.systems.RenderSystem;
import com.moon404.gunskills.GunSkills;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin extends GuiMixin
{
    @Shadow(remap = false)
    public int leftHeight;

    /**
     * @author Moon-404
     * @reason 用于渲染护甲值
     */
    @Overwrite(remap = false)
    public void renderHealth(int width, int height, GuiGraphics guiGraphics)
    {
        ResourceLocation resourceLocation = new ResourceLocation(GunSkills.MODID, "textures/gui/icons.png");
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getProfiler().push("health");
        RenderSystem.enableBlend();

        Player player = (Player) minecraft.getCameraEntity();
        int health = Mth.ceil(player.getHealth());
        boolean highlight = healthBlinkTime > (long) tickCount && (healthBlinkTime - (long) tickCount) / 3L % 2L == 1L;

        if (health < this.lastHealth && player.invulnerableTime > 0)
        {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (long) (this.tickCount + 20);
        }
        else if (health > this.lastHealth && player.invulnerableTime > 0)
        {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (long) (this.tickCount + 10);
        }

        if (Util.getMillis() - this.lastHealthTime > 1000L)
        {
            this.lastHealth = health;
            this.displayHealth = health;
            this.lastHealthTime = Util.getMillis();
        }

        this.lastHealth = health;
        int healthLast = this.displayHealth;

        AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = Math.max((float) attrMaxHealth.getValue(), Math.max(healthLast, health));
        int absorb = Mth.ceil(player.getAbsorptionAmount());

        int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        this.random.setSeed((long) (tickCount * 312871));

        int left = width / 2 - 91;
        int top = height - leftHeight;
        leftHeight += (healthRows * rowHeight);
        if (rowHeight != 10) leftHeight += 10 - rowHeight;

        int regen = -1;
        if (player.hasEffect(MobEffects.REGENERATION))
        {
            regen = this.tickCount % Mth.ceil(healthMax + 5.0F);
        }

        int totalAbsorb = player.experienceLevel == 0 ? 0 :player.experienceLevel * 2 + 2;
        int row = 1;
        int Voffset = 112;
        int fullAbsorb = absorb / 2;
        int halfAbsorb = absorb % 2 == 1 ? 1 : 0;
        int level = player.experienceLevel;
        for (int i = 0; i < fullAbsorb; i++)
        {
            guiGraphics.blit(resourceLocation, left + i * 8, top - row * rowHeight, 18 * level - 9, Voffset, 9, 9);
        }
        if (halfAbsorb == 1)
        {
            guiGraphics.blit(resourceLocation, left + fullAbsorb * 8, top - row * rowHeight, 18 * level, Voffset, 9, 9);
        }
        for (int i = fullAbsorb + halfAbsorb; i < totalAbsorb; i++)
        {
            guiGraphics.blit(resourceLocation, left + i * 8, top - row * rowHeight, 0, Voffset, 9, 9);
        }
        this.renderHearts(guiGraphics, player, left, top, rowHeight, regen, healthMax, health, healthLast, 0, highlight);

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }
}
