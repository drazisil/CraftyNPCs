package com.drazisil.craftynpcs.util;

import com.drazisil.craftynpcs.WorldLocation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import static com.drazisil.craftynpcs.CraftyNPCs.LOGGER;

public class BlockUtil {

    public static BlockPos getLookPos(MobEntity entityIn) {
        Vec3d lookVec3d = entityIn.getLookVec();
        LOGGER.info("1: " + lookVec3d);
        Vec3i lookVec3i = new WorldLocation(lookVec3d).toVec3i();
        LOGGER.info("2: " + lookVec3i);
        BlockPos position = entityIn.getPosition();
        LOGGER.info("3: " + position);
        BlockPos finalPosition = position.add(lookVec3i);
        LOGGER.info("4: " + finalPosition);
        LookController look = entityIn.getLookController();
        LOGGER.info("5: " + look.getLookPosX() + ", " + look.getLookPosY() + ", " +look.getLookPosZ());
        return finalPosition;

    }

    public static Block getBlockAtPos(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock();
    }

    public static BlockState getBlockStateAtPos(World world, BlockPos pos) {
        return world.getBlockState(pos);
    }
}
