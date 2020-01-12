package com.drazisil.craftynpcs.entity.ai.goals;

import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;

public class LocateMineableBlockGoal extends Goal {
    private final NPCEntity npcEntity;
    private double x;
    private double y;
    private double z;
    private final double speedIn;
    private final World world;
    private HashSet<Block> mineableBlocks = new HashSet<>();

    public LocateMineableBlockGoal(NPCEntity npcEntity, HashSet<Block> mineableBlocks, double speedIn) {
        this.mineableBlocks.addAll(mineableBlocks);


        this.npcEntity = npcEntity;
        this.speedIn = speedIn;
        this.world = npcEntity.world;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (!this.world.isDaytime()) {
            return false;
        } else {
            Vec3d vec3d = this.func_204729_f();
            if (vec3d == null) {
                return false;
            } else {
                this.x = vec3d.x;
                this.y = vec3d.y;
                this.z = vec3d.z;
                return true;
            }
        }
    }

    public boolean shouldContinueExecuting() {
        return !this.npcEntity.getNavigator().noPath();
    }

    public void startExecuting() {
        this.npcEntity.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, this.speedIn);
    }

    @Nullable
    private Vec3d func_204729_f() {
        Random random = this.npcEntity.getRNG();
        BlockPos blockpos = new BlockPos(this.npcEntity.posX, this.npcEntity.getBoundingBox().minY, this.npcEntity.posZ);

        for(int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
            if (mineableBlocks.contains(this.world.getBlockState(blockpos1).getBlock())) {
                return new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
            }
        }

        return null;
    }
}
