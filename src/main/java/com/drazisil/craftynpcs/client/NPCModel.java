package com.drazisil.craftynpcs.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
class NPCModel<T extends LivingEntity> extends BipedModel<T> {
    private final RendererModel bipedLeftArmwear;
    private final RendererModel bipedRightArmwear;
    private final RendererModel bipedLeftLegwear;
    private final RendererModel bipedRightLegwear;
    private final int textureWidthIn = 64;
    private final int textureHeightIn = 64;
    public RendererModel bipedHead;
    public RendererModel bipedHeadwear;
    public RendererModel bipedBody;
    public RendererModel bipedRightArm;
    public RendererModel bipedLeftArm;
    public RendererModel bipedRightLeg;
    public RendererModel bipedLeftLeg;
    private final RendererModel bipedBodyWear;
    private final RendererModel bipedCape;
    private final RendererModel bipedDeadmau5Head;
    private final boolean smallArms;
    private float remainingItemUseTime;

    public NPCModel(float modelSize, boolean smallArmsIn) {
        super();

        float p_i1149_2_ = 1.0f;

        this.bipedHead = new RendererModel(this, 0, 0);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
        this.bipedHead.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
        this.bipedHeadwear = new RendererModel(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);

        this.bipedLeftLeg = new RendererModel(this, 16, 48);
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.bipedLeftLegwear = new RendererModel(this, 0, 48);
        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.bipedRightLegwear = new RendererModel(this, 0, 32);
        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
        this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.bipedBodyWear = new RendererModel(this, 16, 32);
        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
        this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.smallArms = smallArmsIn;
        this.bipedDeadmau5Head = new RendererModel(this, 24, 0);
        this.bipedDeadmau5Head.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, modelSize);
        this.bipedCape = new RendererModel(this, 0, 0);
        this.bipedCape.setTextureSize(64, 32);
        this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, modelSize);
        if (smallArmsIn) {
            this.bipedLeftArm = new RendererModel(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
            this.bipedRightArm = new RendererModel(this, 40, 16);
            this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
            this.bipedLeftArmwear = new RendererModel(this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
            this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
            this.bipedRightArmwear = new RendererModel(this, 40, 32);
            this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
            this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
        } else {
            this.bipedLeftArm = new RendererModel(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.bipedLeftArmwear = new RendererModel(this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
            this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.bipedRightArmwear = new RendererModel(this, 40, 32);
            this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
            this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
        }

        this.leftArmPose = BipedModel.ArmPose.EMPTY;
        this.rightArmPose = BipedModel.ArmPose.EMPTY;
        this.textureWidth = textureWidthIn;
        this.textureHeight = textureHeightIn;
        this.bipedHead = new RendererModel(this, 0, 0);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
        this.bipedHead.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
        this.bipedHeadwear = new RendererModel(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
        this.bipedBody = new RendererModel(this, 16, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
        this.bipedBody.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
        this.bipedRightArm = new RendererModel(this, 40, 16);
        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i1149_2_, 0.0F);
        this.bipedLeftArm = new RendererModel(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + p_i1149_2_, 0.0F);
        this.bipedRightLeg = new RendererModel(this, 0, 16);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + p_i1149_2_, 0.0F);
        this.bipedLeftLeg = new RendererModel(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + p_i1149_2_, 0.0F);


    }

    public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.pushMatrix();
        if (this.isChild) {
            float f = 2.0F;
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
            this.bipedLeftLegwear.render(scale);
            this.bipedRightLegwear.render(scale);
            this.bipedLeftArmwear.render(scale);
            this.bipedRightArmwear.render(scale);
            this.bipedBodyWear.render(scale);
        } else {
            if (entityIn.shouldRenderSneaking()) {
                GlStateManager.translatef(0.0F, 0.2F, 0.0F);
            }

            this.bipedLeftLegwear.render(scale);
            this.bipedRightLegwear.render(scale);
            this.bipedLeftArmwear.render(scale);
            this.bipedRightArmwear.render(scale);
            this.bipedBodyWear.render(scale);
        }

        GlStateManager.popMatrix();
    }

    public void renderDeadmau5Head(float scale) {
        this.bipedDeadmau5Head.copyModelAngles(this.bipedHead);
        this.bipedDeadmau5Head.rotationPointX = 0.0F;
        this.bipedDeadmau5Head.rotationPointY = 0.0F;
        this.bipedDeadmau5Head.render(scale);
    }

    public void renderCape(float scale) {
        this.bipedCape.render(scale);
    }

    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean flag = entityIn.getTicksElytraFlying() > 4;
        boolean flag1 = entityIn.func_213314_bj();
        this.bipedHead.rotateAngleY = netHeadYaw * 0.017453292F;
        if (flag) {
            this.bipedHead.rotateAngleX = -0.7853982F;
        } else if (this.swimAnimation > 0.0F) {
            if (flag1) {
                this.bipedHead.rotateAngleX = this.func_205060_a(this.bipedHead.rotateAngleX, -0.7853982F, this.swimAnimation);
            } else {
                this.bipedHead.rotateAngleX = this.func_205060_a(this.bipedHead.rotateAngleX, headPitch * 0.017453292F, this.swimAnimation);
            }
        } else {
            this.bipedHead.rotateAngleX = headPitch * 0.017453292F;
        }

        this.bipedBody.rotateAngleY = 0.0F;
        this.bipedRightArm.rotationPointZ = 0.0F;
        this.bipedRightArm.rotationPointX = -5.0F;
        this.bipedLeftArm.rotationPointZ = 0.0F;
        this.bipedLeftArm.rotationPointX = 5.0F;
        float f = 1.0F;
        if (flag) {
            f = (float)entityIn.getMotion().lengthSquared();
            f /= 0.2F;
            f = f * f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount / f;
        this.bipedRightLeg.rotateAngleY = 0.0F;
        this.bipedLeftLeg.rotateAngleY = 0.0F;
        this.bipedRightLeg.rotateAngleZ = 0.0F;
        this.bipedLeftLeg.rotateAngleZ = 0.0F;
        if (this.isSitting) {
            this.bipedRightArm.rotateAngleX += -0.62831855F;
            this.bipedLeftArm.rotateAngleX += -0.62831855F;
            this.bipedRightLeg.rotateAngleX = -1.4137167F;
            this.bipedRightLeg.rotateAngleY = 0.31415927F;
            this.bipedRightLeg.rotateAngleZ = 0.07853982F;
            this.bipedLeftLeg.rotateAngleX = -1.4137167F;
            this.bipedLeftLeg.rotateAngleY = -0.31415927F;
            this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
        }

        this.bipedRightArm.rotateAngleY = 0.0F;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        switch(this.leftArmPose) {
            case EMPTY:
                this.bipedLeftArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
                this.bipedLeftArm.rotateAngleY = 0.5235988F;
                break;
            case ITEM:
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.31415927F;
                this.bipedLeftArm.rotateAngleY = 0.0F;
        }

        switch(this.rightArmPose) {
            case EMPTY:
                this.bipedRightArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
                this.bipedRightArm.rotateAngleY = -0.5235988F;
                break;
            case ITEM:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.31415927F;
                this.bipedRightArm.rotateAngleY = 0.0F;
                break;
            case THROW_SPEAR:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 3.1415927F;
                this.bipedRightArm.rotateAngleY = 0.0F;
        }

        if (this.leftArmPose == BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BOW_AND_ARROW) {
            this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 3.1415927F;
            this.bipedLeftArm.rotateAngleY = 0.0F;
        }

        float f8;
        float f9;
        float f12;
        if (this.swingProgress > 0.0F) {
            HandSide handside = this.func_217147_a(entityIn);
            RendererModel renderermodel = this.getArmForSide(handside);
            f8 = this.swingProgress;
            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f8) * 6.2831855F) * 0.2F;
            if (handside == HandSide.LEFT) {
                this.bipedBody.rotateAngleY *= -1.0F;
            }

            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
            f8 = 1.0F - this.swingProgress;
            f8 *= f8;
            f8 *= f8;
            f8 = 1.0F - f8;
            f9 = MathHelper.sin(f8 * 3.1415927F);
            f12 = MathHelper.sin(this.swingProgress * 3.1415927F) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            renderermodel.rotateAngleX = (float)((double)renderermodel.rotateAngleX - ((double)f9 * 1.2D + (double)f12));
            renderermodel.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
            renderermodel.rotateAngleZ += MathHelper.sin(this.swingProgress * 3.1415927F) * -0.4F;
        }

        if (this.isSneak) {
            this.bipedBody.rotateAngleX = 0.5F;
            this.bipedRightArm.rotateAngleX += 0.4F;
            this.bipedLeftArm.rotateAngleX += 0.4F;
            this.bipedRightLeg.rotationPointZ = 4.0F;
            this.bipedLeftLeg.rotationPointZ = 4.0F;
            this.bipedRightLeg.rotationPointY = 9.0F;
            this.bipedLeftLeg.rotationPointY = 9.0F;
            this.bipedHead.rotationPointY = 1.0F;
        } else {
            this.bipedBody.rotateAngleX = 0.0F;
            this.bipedRightLeg.rotationPointZ = 0.1F;
            this.bipedLeftLeg.rotationPointZ = 0.1F;
            this.bipedRightLeg.rotationPointY = 12.0F;
            this.bipedLeftLeg.rotationPointY = 12.0F;
            this.bipedHead.rotationPointY = 0.0F;
        }

        this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        if (this.rightArmPose == BipedModel.ArmPose.BOW_AND_ARROW) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
            this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
        } else if (this.leftArmPose == BipedModel.ArmPose.BOW_AND_ARROW && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
        }

        float f4 = (float) CrossbowItem.getChargeTime(entityIn.getActiveItemStack());
        float f7;
        if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
            this.bipedRightArm.rotateAngleY = -0.8F;
            this.bipedRightArm.rotateAngleX = -0.97079635F;
            this.bipedLeftArm.rotateAngleX = -0.97079635F;
            f7 = MathHelper.clamp(this.remainingItemUseTime, 0.0F, f4);
            this.bipedLeftArm.rotateAngleY = MathHelper.lerp(f7 / f4, 0.4F, 0.85F);
            this.bipedLeftArm.rotateAngleX = MathHelper.lerp(f7 / f4, this.bipedLeftArm.rotateAngleX, -1.5707964F);
        } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
            this.bipedLeftArm.rotateAngleY = 0.8F;
            this.bipedRightArm.rotateAngleX = -0.97079635F;
            this.bipedLeftArm.rotateAngleX = -0.97079635F;
            f7 = MathHelper.clamp(this.remainingItemUseTime, 0.0F, f4);
            this.bipedRightArm.rotateAngleY = MathHelper.lerp(f7 / f4, -0.4F, -0.85F);
            this.bipedRightArm.rotateAngleX = MathHelper.lerp(f7 / f4, this.bipedRightArm.rotateAngleX, -1.5707964F);
        }

        if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_HOLD && this.swingProgress <= 0.0F) {
            this.bipedRightArm.rotateAngleY = -0.3F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.6F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX + 0.1F;
            this.bipedLeftArm.rotateAngleX = -1.5F + this.bipedHead.rotateAngleX;
        } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_HOLD) {
            this.bipedRightArm.rotateAngleY = -0.6F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.3F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = -1.5F + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX + 0.1F;
        }

        if (this.swimAnimation > 0.0F) {
            f7 = limbSwing % 26.0F;
            f8 = this.swingProgress > 0.0F ? 0.0F : this.swimAnimation;
            if (f7 < 14.0F) {
                this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, 0.0F, this.swimAnimation);
                this.bipedRightArm.rotateAngleX = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleX, 0.0F);
                this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, 3.1415927F, this.swimAnimation);
                this.bipedRightArm.rotateAngleY = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleY, 3.1415927F);
                this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 3.1415927F + 1.8707964F * this.func_203068_a(f7) / this.func_203068_a(14.0F), this.swimAnimation);
                this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleZ, 3.1415927F - 1.8707964F * this.func_203068_a(f7) / this.func_203068_a(14.0F));
            } else if (f7 >= 14.0F && f7 < 22.0F) {
                f9 = (f7 - 14.0F) / 8.0F;
                this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, 1.5707964F * f9, this.swimAnimation);
                this.bipedRightArm.rotateAngleX = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleX, 1.5707964F * f9);
                this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, 3.1415927F, this.swimAnimation);
                this.bipedRightArm.rotateAngleY = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleY, 3.1415927F);
                this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 5.012389F - 1.8707964F * f9, this.swimAnimation);
                this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleZ, 1.2707963F + 1.8707964F * f9);
            } else if (f7 >= 22.0F && f7 < 26.0F) {
                f9 = (f7 - 22.0F) / 4.0F;
                this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, 1.5707964F - 1.5707964F * f9, this.swimAnimation);
                this.bipedRightArm.rotateAngleX = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleX, 1.5707964F - 1.5707964F * f9);
                this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, 3.1415927F, this.swimAnimation);
                this.bipedRightArm.rotateAngleY = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleY, 3.1415927F);
                this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 3.1415927F, this.swimAnimation);
                this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleZ, 3.1415927F);
            }

            f9 = 0.3F;
            f12 = 0.33333334F;
            this.bipedLeftLeg.rotateAngleX = MathHelper.lerp(this.swimAnimation, this.bipedLeftLeg.rotateAngleX, 0.3F * MathHelper.cos(limbSwing * 0.33333334F + 3.1415927F));
            this.bipedRightLeg.rotateAngleX = MathHelper.lerp(this.swimAnimation, this.bipedRightLeg.rotateAngleX, 0.3F * MathHelper.cos(limbSwing * 0.33333334F));
        }

        this.bipedHeadwear.copyModelAngles(this.bipedHead);
    }

    private float func_203068_a(float p_203068_1_) {
        return -65.0F * p_203068_1_ + p_203068_1_ * p_203068_1_;
    }

    public void setVisible(boolean visible) {
        this.setVisible(visible);
        this.bipedLeftArmwear.showModel = visible;
        this.bipedRightArmwear.showModel = visible;
        this.bipedLeftLegwear.showModel = visible;
        this.bipedRightLegwear.showModel = visible;
        this.bipedBodyWear.showModel = visible;
        this.bipedCape.showModel = visible;
        this.bipedDeadmau5Head.showModel = visible;
    }

    public void postRenderArm(float scale, HandSide side) {
        RendererModel renderermodel = this.getArmForSide(side);
        if (this.smallArms) {
            float f = 0.5F * (float)(side == HandSide.RIGHT ? 1 : -1);
            renderermodel.rotationPointX += f;
            renderermodel.postRender(scale);
            renderermodel.rotationPointX -= f;
        } else {
            renderermodel.postRender(scale);
        }

    }

    protected RendererModel getArmForSide(HandSide side) {
        return side == HandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
    }

    @Override
    public RendererModel func_205072_a() {
        return null;
    }
}