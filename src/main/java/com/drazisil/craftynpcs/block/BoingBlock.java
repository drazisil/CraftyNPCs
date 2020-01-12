package com.drazisil.craftynpcs.block;

import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;

public class BoingBlock extends SlimeBlock {
    public BoingBlock(Properties properties) {
        super(properties);
    }


    public void onLanded(IBlockReader worldIn, Entity entityIn) {
        if (entityIn.isSneaking()) {
            super.onLanded(worldIn, entityIn);
        } else {
            Vec3d vec3d = entityIn.getMotion();
            if (vec3d.y < 0.0D) {
                double d0 = entityIn instanceof LivingEntity ? 50.0D : 0.8D;
                if (entityIn instanceof PlayerEntity) {
                    ((PlayerEntity) entityIn).noClip = true;
                } else {
                    entityIn.setMotion(vec3d.x, -vec3d.y * d0, vec3d.z);
                }


            }
        }

    }
}
