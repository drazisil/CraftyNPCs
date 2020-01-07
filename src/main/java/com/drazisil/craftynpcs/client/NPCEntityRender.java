package com.drazisil.craftynpcs.client;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class NPCEntityRender extends BipedRenderer<NPCEntity, NPCModel<NPCEntity>> {
    public NPCEntityRender(EntityRendererManager renderManagerIn, NPCModel modelBipedIn, float shadowSize) {
        super(renderManagerIn, modelBipedIn, shadowSize);
    }

    private static final ResourceLocation field_217771_a = new ResourceLocation(CraftyNPCs.MODID + ":textures/entity/crafty_npc.png");

    public NPCEntityRender(EntityRendererManager p_i50954_1_) {
        super(p_i50954_1_, new NPCModel(1.0F, false), 0.5F);
    }

//    public NPCEntityRender(EntityRendererManager p_i50965_1_, NPCModel<AbstractClientPlayerEntity> p_i50965_2_, float p_i50965_3_) {
//        super(p_i50965_1_, p_i50965_2_, p_i50965_3_);
//    }
//
//    public NPCEntityRender(EntityRendererManager p_i50954_1_) {
//        super(p_i50954_1_, new NPCModel<>(1.0F, false), 0.5F);
//    }

//    public NPCEntityRender(EntityRendererManager p_i50961_1_, NPCModel<NPCEntity> p_i50961_2_, float p_i50961_3_) {
//        super(p_i50961_1_, p_i50961_2_, p_i50961_3_);
//    }


//    protected ResourceLocation getEntityTexture(NPCEntity entity) {
//        return field_217771_a;
//    }

//    protected void applyRotations(T entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
////        if (entityLiving.isDrowning()) {
////            rotationYaw += (float)(Math.cos((double)entityLiving.ticksExisted * 3.25D) * 3.141592653589793D * 0.25D);
////        }
//
//        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
//    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(NPCEntity abstractClientPlayerEntity) {
        return new ResourceLocation(CraftyNPCs.MODID+":textures/entity/crafty_npc.png");
    }

        public static class RenderFactory implements IRenderFactory<NPCEntity>
    {

        @Override
        public EntityRenderer<? super NPCEntity> createRenderFor(EntityRendererManager entityRendererManager) {
            return new NPCEntityRender(entityRendererManager);
        }
    }

}
//
//public class NPCEntityRender extends LivingRenderer<NPCEntity, NPCModel<NPCEntity>> {
//    private static final ResourceLocation field_217779_a = new ResourceLocation(CraftyNPCs.MODID + ":textures/entity/crafty_npc.png");
//
//    public NPCEntityRender(EntityRendererManager p_i50954_1_) {
//        super(p_i50954_1_, new NPCModel<>(1.0F, false), 0.5F);
//    }
//
//
//    public NPCEntityRender(EntityRendererManager p_i50961_1_, NPCModel<NPCEntity> p_i50961_2_, float p_i50961_3_) {
//        super(p_i50961_1_, p_i50961_2_, p_i50961_3_);
//    }
//
//    @Override
//    protected ResourceLocation getEntityTexture(NPCEntity npcEntity) {
//        return new ResourceLocation(CraftyNPCs.MODID+":textures/entity/crafty_npc.png");
//    }
//
//
//    public NPCEntityRender(EntityRendererManager p_i50954_1_, IReloadableResourceManager p_i50954_2_) {
//        super(p_i50954_1_, new NPCModel<>();
//        this.addLayer(new HeadLayer<>(this));
//    }
//
//
//
//    protected ResourceLocation getEntityTexture(NPCEntity entity) {
//        return field_217779_a;
//    }
//
//    protected void preRenderCallback(NPCEntity entitylivingbaseIn, float partialTickTime) {
//        float f = 0.9375F;
//        if (entitylivingbaseIn.isChild()) {
//            f = (float)((double)f * 0.5D);
//            this.shadowSize = 0.25F;
//        } else {
//            this.shadowSize = 0.5F;
//        }
//
//        GlStateManager.scalef(f, f, f);
//    }
//
//}
//}
////
////
