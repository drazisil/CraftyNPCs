package com.drazisil.craftynpcs.client;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.entity.EntityCraftyNPC;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerHeldItemLayer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ModEntityRender extends MobRenderer<EntityCraftyNPC, CraftyNPCModel<EntityCraftyNPC>> {
    private static final ResourceLocation field_217779_a = new ResourceLocation(CraftyNPCs.MODID + ":textures/entity/crafty_npc.png");

    public ModEntityRender(EntityRendererManager p_i50954_1_) {
        super(p_i50954_1_, new CraftyNPCModel(0.0F), 0.5F);
    }


    public ModEntityRender(EntityRendererManager p_i50961_1_, CraftyNPCModel<EntityCraftyNPC> p_i50961_2_, float p_i50961_3_) {
        super(p_i50961_1_, p_i50961_2_, p_i50961_3_);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityCraftyNPC entityCraftyNPC) {
        return new ResourceLocation(CraftyNPCs.MODID+":textures/entity/crafty_npc.png");
    }


    public ModEntityRender(EntityRendererManager p_i50954_1_, IReloadableResourceManager p_i50954_2_) {
        super(p_i50954_1_, new CraftyNPCModel(0.0F), 0.5F);
        this.addLayer(new HeadLayer(this));
        this.addLayer(new VillagerLevelPendantLayer(this, p_i50954_2_, "crafty_npc"));
        this.addLayer(new VillagerHeldItemLayer(this));
    }



    protected ResourceLocation getEntityTexture(VillagerEntity entity) {
        return field_217779_a;
    }

    protected void preRenderCallback(VillagerEntity entitylivingbaseIn, float partialTickTime) {
        float f = 0.9375F;
        if (entitylivingbaseIn.isChild()) {
            f = (float)((double)f * 0.5D);
            this.shadowSize = 0.25F;
        } else {
            this.shadowSize = 0.5F;
        }

        GlStateManager.scalef(f, f, f);
    }


//    @Override
//    protected ResourceLocation getEntityTexture(EntityCraftyNPC entity)
//    {
//        return new ResourceLocation("craftynpcs:textures/entity/crafty_npc.png");
//    }

//    public static class RenderFactory implements IRenderFactory<EntityCraftyNPC>
//    {
//
//        @Override
//        public EntityRenderer<? super EntityCraftyNPC> createRenderFor(EntityRendererManager entityRendererManager) {
//            return new ModEntityRender(entityRendererManager);
//        }
//    }
}


