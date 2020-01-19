package com.drazisil.craftynpcs.entity.ai.brain;

import com.drazisil.craftynpcs.entity.NPCEntity;

class Memory {

    private final NPCEntity npcEntity;
    private final String name;
    private String value;

    public String getName() {
        return this.name;
    }

    public Memory(NPCEntity npcEntity, String name, String value) {
        this.npcEntity = npcEntity;
        this.name = name;
        this.value = value;
    }



    public String getValue(){
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
