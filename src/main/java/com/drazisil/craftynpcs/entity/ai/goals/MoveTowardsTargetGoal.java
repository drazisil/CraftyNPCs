package com.drazisil.craftynpcs.entity.ai.goals;

import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class MoveTowardsTargetGoal extends Goal {
    private final NPCEntity npcEntity;
    private BlockPos targetPos;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private final double speed;
    private final float maxTargetDistance;

    public MoveTowardsTargetGoal(NPCEntity npcEntity, double speedIn, float targetMaxDistance) {
        this.npcEntity = npcEntity;
        this.speed = speedIn;
        this.maxTargetDistance = targetMaxDistance;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        this.targetPos = npcEntity.getTargetPos();
        if (this.targetPos == null || npcEntity.isDigging) {
            return false;
        }

        this.targetPos.up();
        if (npcEntity.getDistanceSq(targetPos.getX(), targetPos.getY(), targetPos.getZ()) > (double)(this.maxTargetDistance * this.maxTargetDistance)) {
            return false;
        }

        BlockPos vec3d = this.targetPos;
        this.movePosX = vec3d.getX();
        this.movePosY = vec3d.getY();
        this.movePosZ = vec3d.getZ();
        return true;

    }

    public boolean shouldContinueExecuting() {
        return !this.npcEntity.getNavigator().noPath()
                && npcEntity.getDistanceSq(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < (double)(this.maxTargetDistance * this.maxTargetDistance);
    }

    public void resetTask() {

        this.targetPos = null;
    }

    public void startExecuting() {
        this.npcEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
    }
}
