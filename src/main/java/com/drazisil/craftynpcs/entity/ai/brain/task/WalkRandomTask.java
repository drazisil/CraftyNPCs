package com.drazisil.craftynpcs.entity.ai.brain.task;

import com.drazisil.craftynpcs.entity.NPCEntity;
import com.drazisil.craftynpcs.entity.ai.RandomPositionGenerator;
import com.drazisil.craftynpcs.entity.ai.brain.Brain;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class WalkRandomTask extends Task {

    private final String name;
    private final Brain brain;
    private final NPCEntity npcEntity;
    private double x;
    private double y;
    private double z;
    private boolean mustUpdate;
    private int executionChance = 10;

    public WalkRandomTask(String name, Brain brain) {
        this.name = name;
        this.brain = brain;
        this.npcEntity = this.brain.npcEntity;
//        World world = this.npcEntity.getEntityWorld();
    }

    private boolean shouldTick() {
        if (brain.getMemoryValue("should_random_walk").equals("true")) {
            if (!this.mustUpdate) {
                if (npcEntity.getIdleTime() >= 100) {
                    return false;
                }

                if (npcEntity.getRNG().nextInt(this.executionChance) != 0) {
                    return false;
                }
            }

            Vec3d vec3d = this.getPosition();
            if (vec3d == null) {
                return false;
            } else {
                this.x = vec3d.x;
                this.y = vec3d.y;
                this.z = vec3d.z;
                this.mustUpdate = false;
                return true;
            }

        }
        return false;
    }

    @Override
    public void tick() {

        if (!shouldTick()) return;

        double speed = 0.5d;
        npcEntity.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, speed);

    }

    @Nullable
    protected Vec3d getPosition() {
        return RandomPositionGenerator.findRandomTarget(npcEntity, 10, 7);
    }

    public void setExecutionChance(int newchance) {
        this.executionChance = newchance;
    }

    public String getName() {
        return name;
    }
}
