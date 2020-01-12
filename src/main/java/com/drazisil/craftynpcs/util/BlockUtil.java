package com.drazisil.craftynpcs.util;

import com.drazisil.craftynpcs.WorldLocation;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BlockUtil {

    public static BlockPos getLookPos(Entity entityIn) {
        Vec3i lookVec3i = new WorldLocation(entityIn.getLookVec()).toVec3i();
        return entityIn.getPosition().add(lookVec3i);

    }

    public static Block getBlockAtPos(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock();
    }
}
