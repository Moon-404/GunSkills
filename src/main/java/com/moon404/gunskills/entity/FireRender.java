package com.moon404.gunskills.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class FireRender extends EntityRenderer<FireEntity>
{
    public FireRender(Context pContext)
    {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(FireEntity pEntity)
    {
        return null;
    }

    @Override
    public void render(FireEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight)
    {
        if (pEntity.activeTickCount > -1) return;
        Minecraft.getInstance().getItemRenderer().renderStatic(pEntity.getItem(), ItemDisplayContext.NONE, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), 0);
    }
}
