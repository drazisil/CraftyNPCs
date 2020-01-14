package com.drazisil.craftynpcs.entity.ai.structure;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AIStructure {

    private BlockPos centerVec;
    private boolean completed = false;
    private NPCEntity npcEntity;
    private BlockPos currentPos;
    private int maxSteps = 10;
    private int stepCount;
    private int tickSpread = 40;
    private int currentTick;

    public AIStructure() {

    }

    public BlockPos getCenterVec() {
        return centerVec;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public void tick(BlockPos position) {
//        CraftyNPCs.LOGGER.info("Tick...");
        if (currentTick < tickSpread) {
            currentTick++;
        } else {
            currentTick = 0;
            digStep();
            stepCount++;
            if (stepCount >= maxSteps) {
                this.npcEntity.isDigging = false;
                this.completed = true;
            }
        }
    }

    public void start(NPCEntity npcEntity) {
        this.npcEntity = npcEntity;
        this.centerVec = this.npcEntity.getTargetPos();
        CraftyNPCs.LOGGER.info("Start building structure at " + this.centerVec);
        BlockPos startPos = this.centerVec.east().down();
        this.currentPos = startPos;
        this.npcEntity.isDigging = true;
        this.npcEntity.getNavigator().tryMoveToXYZ(startPos.getX(), startPos.getY(), startPos.getZ(), 0.5f);
    }

    private void digStep() {
        BlockPos newStep = this.currentPos.north().down();
        this.npcEntity.swingArm(Hand.MAIN_HAND);
        World world = this.npcEntity.getEntityWorld();
        world.destroyBlock(newStep.up(2), false);
        world.destroyBlock(newStep.up(), false);
        world.destroyBlock(newStep, false);
        this.npcEntity.getNavigator().tryMoveToXYZ(newStep.getX(), newStep.getY(), newStep.getZ(), 0.5f);
        this.currentPos = newStep;
    }
}
