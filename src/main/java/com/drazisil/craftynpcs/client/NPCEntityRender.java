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
