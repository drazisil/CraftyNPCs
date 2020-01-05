package com.drazisil.craftynpcs.entity;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class NPCManager {

    private ArrayList<NPCEntry> npcs = new ArrayList<>();


    public void register(NPCEntity NPCEntity) {

        NPCEntry npcEntry = new NPCEntry(NPCEntity.getName().getFormattedText(), NPCEntity);

        npcs.add(npcEntry);
    }

    @Nullable
    public NPCEntity getNPCByName(String name) {
        for (NPCEntry npcEntry: npcs) {
            if (npcEntry.name.equals(name)) {
                return npcEntry.npc;
            }
        }
        return  null;
    }

    public void deregister(NPCEntity NPCEntity) {

        // remove all even numbers
        npcs.removeIf(npcEntry -> npcEntry.npc == NPCEntity);
    }

}
