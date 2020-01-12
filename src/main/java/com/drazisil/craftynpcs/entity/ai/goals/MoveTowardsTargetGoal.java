package com.drazisil.craftynpcs.entity.ai.goals;

import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class MoveTowardsTargetGoal extends Goal {
    private final NPCEntity npcEntity;
    private Vec3d targetPos;
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
        if (this.targetPos == null) {
            return false;
        } else if (npcEntity.getDistanceSq(targetPos) > (double)(this.maxTargetDistance * this.maxTargetDistance)) {
            return false;
        } else {
//            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.npcEntity, 16, 7, new Vec3d(this.targetPos.x, this.targetPos.y, this.targetPos.z));
            Vec3d vec3d = this.targetPos;
            if (vec3d == null) {
                return false;
            } else {
                this.movePosX = vec3d.x;
                this.movePosY = vec3d.y;
                this.movePosZ = vec3d.z;
                return true;
            }
        }
    }

    public boolean shouldContinueExecuting() {
        return !this.npcEntity.getNavigator().noPath() && npcEntity.getDistanceSq(targetPos) < (double)(this.maxTargetDistance * this.maxTargetDistance);
    }

    public void resetTask() {
        this.targetPos = null;
    }

    public void startExecuting() {
        this.npcEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
    }
}
