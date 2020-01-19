package com.drazisil.craftynpcs.entity.ai.goals;

import com.drazisil.craftynpcs.entity.NPCEntity;
import com.drazisil.craftynpcs.entity.ai.structure.AIStructure;
import net.minecraft.entity.ai.goal.Goal;

public class MakeStructure extends Goal {
    private final NPCEntity npcEntity;
    private final AIStructure structure;

    public MakeStructure(NPCEntity entityIn, AIStructure structure) {
        this.npcEntity = entityIn;
        this.structure = structure;
    }

    public boolean shouldExecute() {
        if (this.npcEntity.getPosition().equals(this.npcEntity.getTargetPos())) {

            if (this.structure.isNotCompleted()) return true;
        }
        return false;
    }

    public boolean shouldContinueExecuting() {
        return this.structure.isNotCompleted();
    }

    public void startExecuting() {
        this.structure.start(npcEntity);
    }

    public void tick() {
        this.structure.tick(this.npcEntity.getPosition());
    }
}
