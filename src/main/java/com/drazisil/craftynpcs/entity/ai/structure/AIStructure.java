package com.drazisil.craftynpcs.entity.ai.structure;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class AIStructure {

    private boolean completed = false;
    private NPCEntity npcEntity;
    private BlockPos currentPos;
    private int currentTick;
    private World world;

    public AIStructure() {

    }

    public boolean isNotCompleted() {
        return !this.completed;
    }

    public void tick(BlockPos position) {
//        CraftyNPCs.LOGGER.info("Tick...");
        int tickSpread = 40;
        if (currentTick < tickSpread) {
            currentTick++;
        } else {
            currentTick = 0;
        }

        updateStep(this.currentPos);
        int maxY = 10;
        if (this.currentPos.getY() <= maxY) {
            this.npcEntity.isDigging = false;
            this.completed = true;
        }
    }

    public void start(NPCEntity npcEntity) {
        this.npcEntity = npcEntity;
        this.world = npcEntity.getEntityWorld();
        BlockPos centerPos = this.npcEntity.getTargetPos();
        CraftyNPCs.LOGGER.info("Start building structure at " + centerPos);
        BlockPos startPos = centerPos.east().down();
        this.currentPos = startPos;
        this.npcEntity.isDigging = true;
        this.npcEntity.getNavigator().tryMoveToXYZ(startPos.getX(), startPos.getY(), startPos.getZ(), 0.5f);
    }

    private void digStep(BlockPos stepPos) {
        this.npcEntity.swingArm(Hand.MAIN_HAND);
        world.destroyBlock(stepPos.up(2), false);
        world.destroyBlock(stepPos.up(), false);
        world.destroyBlock(stepPos, false);
    }

    private void updateStep(BlockPos stepPos) {
        BlockPos newStepLeft = stepPos.north().down();
        digStep(newStepLeft);

        if (!this.npcEntity.getNavigator().tryMoveToXYZ(newStepLeft.getX(), newStepLeft.getY(), newStepLeft.getZ(), 0.5f)) {
            // Try again?
            digStep(newStepLeft);
        }
        BlockPos newStepRight = stepPos.east();
        digStep(newStepRight);
        if (!this.npcEntity.getNavigator().tryMoveToXYZ(newStepRight.getX(), newStepRight.getY(), newStepRight.getZ(), 0.5f)) {
            // Try again?
            digStep(newStepRight);
        }
        this.currentPos = newStepLeft;

    }
}
