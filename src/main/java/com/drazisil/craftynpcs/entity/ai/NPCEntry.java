package com.drazisil.craftynpcs.entity.ai;

import com.drazisil.craftynpcs.entity.NPCEntity;

class NPCEntry {

    public final String name;
    public final NPCEntity npc;

    public NPCEntry(String name, NPCEntity npc) {
        this.name = name;
        this.npc = npc;
    }
}
