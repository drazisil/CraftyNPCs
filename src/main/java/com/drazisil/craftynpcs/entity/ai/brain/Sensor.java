package com.drazisil.craftynpcs.entity.ai.brain;

import com.drazisil.craftynpcs.entity.NPCEntity;

class Sensor {

    protected NPCEntity npcEntity;
    protected String name;

    public String getName() {
        return this.name;
    }

    public Sensor(NPCEntity npcEntity) {
        this.npcEntity = npcEntity;
    }


    public void update() {

    }

    public String getValue(){
        return null;
    }
}
