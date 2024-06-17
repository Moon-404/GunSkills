package com.moon404.gunskills.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class SnareRender extends EntityRenderer<SnareEntity>
{
    public SnareRender(Context pContext)
    {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(SnareEntity pEntity)
    {
        return null;
    }

    @Override
    public void render(SnareEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight)
    {
        Minecraft.getInstance().getItemRenderer().renderStatic(pEntity.getItem(), ItemDisplayContext.NONE, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), 0);
    }
}
