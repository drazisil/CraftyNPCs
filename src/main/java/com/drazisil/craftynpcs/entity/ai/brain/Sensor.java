package com.drazisil.craftynpcs.entity.ai.brain;

import com.drazisil.craftynpcs.entity.NPCEntity;

class Sensor {

    final NPCEntity npcEntity;
    String name;

    public String getName() {
        return this.name;
    }

    Sensor(NPCEntity npcEntity) {
        this.npcEntity = npcEntity;
    }


    public void update() {

    }

    public String getValue(){
        return null;
    }
}
