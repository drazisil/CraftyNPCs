package com.drazisil.craftynpcs.client;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

@SuppressWarnings("ALL")
@OnlyIn(Dist.CLIENT)
public class NPCEntityRender extends LivingRenderer<NPCEntity, NPCModel<NPCEntity>> {
    public NPCEntityRender(EntityRendererManager renderManagerIn, NPCModel<NPCEntity> modelBipedIn, float shadowSize) {
        super(renderManagerIn, modelBipedIn, shadowSize);
    }

    private static final ResourceLocation field_217771_a = new ResourceLocation(CraftyNPCs.MODID + ":textures/entity/crafty_npc.png");

    EntityRendererManager renderManager;
    float shadowSize;

    public NPCEntityRender(EntityRendererManager p_i50954_1_) {
        super(p_i50954_1_, new NPCModel<>(1.0F, false), 0.5F);
        this.renderManager = p_i50954_1_;
        this.shadowSize = 0.5F;
        this.addLayer(new BipedArmorLayer<NPCEntity, NPCModel<NPCEntity>, BipedModel<NPCEntity>>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new ArrowLayer<>(this));
        //noinspection unchecked
        this.addLayer(new HeadLayer(this));
        this.addLayer(new ElytraLayer<>(this));
        this.addLayer(new SpinAttackEffectLayer(this));
    }

    @Override
    public void doRender(NPCEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.setModelVisibilities(entity);

        super.doRender(entity, x, y, z, entityYaw, partialTicks);

    }

    private void setModelVisibilities(NPCEntity clientPlayer) {
        NPCModel<NPCEntity> playermodel = this.getEntityModel();
        if (clientPlayer.isSpectator()) {
            playermodel.setVisible(false);
            playermodel.bipedHead.showModel = true;
            playermodel.bipedHeadwear.showModel = true;
        } else {
            ItemStack itemstack = clientPlayer.getHeldItemMainhand();
            ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
            playermodel.setVisible(true);
            playermodel.bipedHeadwear.showModel = true;
            playermodel.bipedBodyWear.showModel = true;
            playermodel.bipedLeftLegwear.showModel = true;
            playermodel.bipedRightLegwear.showModel = true;
            playermodel.bipedLeftArmwear.showModel = true;
            playermodel.bipedRightArmwear.showModel = true;
            playermodel.isSneak = clientPlayer.shouldRenderSneaking();
            BipedModel.ArmPose bipedmodel$armpose = this.func_217766_a(clientPlayer, itemstack, itemstack1, Hand.MAIN_HAND);
            BipedModel.ArmPose bipedmodel$armpose1 = this.func_217766_a(clientPlayer, itemstack, itemstack1, Hand.OFF_HAND);
            if (clientPlayer.getPrimaryHand() == HandSide.RIGHT) {
                playermodel.rightArmPose = bipedmodel$armpose;
                playermodel.leftArmPose = bipedmodel$armpose1;
            } else {
                playermodel.rightArmPose = bipedmodel$armpose1;
                playermodel.leftArmPose = bipedmodel$armpose;
            }
        }

    }

    private BipedModel.ArmPose func_217766_a(NPCEntity p_217766_1_, ItemStack p_217766_2_, ItemStack p_217766_3_, Hand p_217766_4_) {
        BipedModel.ArmPose bipedmodel$armpose = BipedModel.ArmPose.EMPTY;
        ItemStack itemstack = p_217766_4_ == Hand.MAIN_HAND ? p_217766_2_ : p_217766_3_;
        if (!itemstack.isEmpty()) {
            bipedmodel$armpose = BipedModel.ArmPose.ITEM;
            if (p_217766_1_.getItemInUseCount() > 0) {
                UseAction useaction = itemstack.getUseAction();
                if (useaction == UseAction.BLOCK) {
                    bipedmodel$armpose = BipedModel.ArmPose.BLOCK;
                } else if (useaction == UseAction.BOW) {
                    bipedmodel$armpose = BipedModel.ArmPose.BOW_AND_ARROW;
                } else if (useaction == UseAction.SPEAR) {
                    bipedmodel$armpose = BipedModel.ArmPose.THROW_SPEAR;
                } else if (useaction == UseAction.CROSSBOW && p_217766_4_ == p_217766_1_.getActiveHand()) {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else {
                boolean flag3 = p_217766_2_.getItem() == Items.CROSSBOW;
                boolean flag = CrossbowItem.isCharged(p_217766_2_);
                boolean flag1 = p_217766_3_.getItem() == Items.CROSSBOW;
                boolean flag2 = CrossbowItem.isCharged(p_217766_3_);
                if (flag3 && flag) {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }

                if (flag1 && flag2 && p_217766_2_.getItem().getUseAction(p_217766_2_) == UseAction.NONE) {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }

        return bipedmodel$armpose;
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
