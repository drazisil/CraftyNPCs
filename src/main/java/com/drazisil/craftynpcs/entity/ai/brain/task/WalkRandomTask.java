package com.drazisil.craftynpcs.entity.ai.brain.task;

import com.drazisil.craftynpcs.entity.NPCEntity;
import com.drazisil.craftynpcs.entity.ai.brain.Brain;
import com.drazisil.craftynpcs.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WalkRandomTask extends Task {

    private final String name;
    private final Brain brain;
    private final NPCEntity npcEntity;
    private final World world;
    private double x;
    private double y;
    private double z;
    private double speed = 0.5d;
    private boolean mustUpdate;
    private int executionChance = 10;

    public WalkRandomTask(String name, Brain brain) {
        this.name = name;
        this.brain = brain;
        this.npcEntity = this.brain.npcEntity;
        this.world = this.npcEntity.getEntityWorld();
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

        npcEntity.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, this.speed);

    }

    @Nullable
    protected Vec3d getPosition() {
        return RandomPositionGenerator.findRandomTarget(npcEntity, 10, 7);
    }

    public void setExecutionChance(int newchance) {
        this.executionChance = newchance;
    }
}
