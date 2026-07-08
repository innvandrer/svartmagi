package net.svartmagi.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.svartmagi.Svartmagi;
import net.svartmagi.entity.SkyggevokterEntity;

public class SkyggevokterRenderer extends HumanoidMobRenderer<SkyggevokterEntity, HumanoidModel<SkyggevokterEntity>> {
    private static final ResourceLocation TEXTURE = Svartmagi.id("textures/entity/skyggevokter.png");

    public SkyggevokterRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.6f);
    }

    @Override
    protected void scale(SkyggevokterEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.35f, 1.35f, 1.35f);
    }

    @Override
    public ResourceLocation getTextureLocation(SkyggevokterEntity entity) {
        return TEXTURE;
    }
}
