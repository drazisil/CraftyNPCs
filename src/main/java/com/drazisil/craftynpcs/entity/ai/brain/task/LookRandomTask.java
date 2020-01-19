package com.drazisil.craftynpcs.entity.ai.brain.task;

import com.drazisil.craftynpcs.entity.NPCEntity;
import com.drazisil.craftynpcs.entity.ai.brain.Brain;
import net.minecraft.world.World;

public class LookRandomTask extends Task {

    private final String name;
    private final Brain brain;
    private final NPCEntity npcEntity;
    private final World world;
    private int idleTime;
    private double lookX;
    private double lookZ;
    private double lookY;

    public LookRandomTask(String name, Brain brain) {
        this.name = name;
        this.brain = brain;
        this.npcEntity = this.brain.npcEntity;
        this.world = this.npcEntity.getEntityWorld();
    }

    @Override
    public void tick() {

        if (!shouldTick()) return;

        --this.idleTime;
        npcEntity.getLookController().func_220679_a(npcEntity.posX + this.lookX,
                npcEntity.posY + (double)npcEntity.getEyeHeight() + this.lookY,
                npcEntity.posZ + this.lookZ);


    }

    private boolean shouldTick() {
        if (brain.getMemoryValue("should_look").equals("true"))
        {
            if (this.idleTime == 0) {
                double d0 = 6.283185307179586D * npcEntity.getRNG().nextDouble();
                this.lookX = Math.cos(d0);
                double random = (Math.floor(((Math.random() * 10 - 5) + 1)));
                int plusOrMinus = Math.random() < 0.5 ? -1 : 1;
                this.lookY = (Math.floor(Math.random() * 10 - 5) + 1) * plusOrMinus;
                this.lookZ = Math.sin(d0);
                this.idleTime = 20 + npcEntity.getRNG().nextInt(20);
            }
            System.out.println("y: "+this.lookY);

            return true;
        }


        return (npcEntity.getRNG().nextFloat() < 0.02F) || this.idleTime >= 0;
    }
}
