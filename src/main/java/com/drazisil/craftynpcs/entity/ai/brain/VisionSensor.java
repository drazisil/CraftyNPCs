package com.drazisil.craftynpcs.entity.ai.brain;

import com.drazisil.craftynpcs.WorldLocation;
import com.drazisil.craftynpcs.entity.NPCEntity;

public class VisionSensor extends Sensor {

    private WorldLocation location;

    public VisionSensor(NPCEntity npcEntity) {
        super((npcEntity));
        this.name = "vision_sensor";
        this.location = npcEntity.getLookPos();
    }

    @Override
    public void update() {
        this.location.update(npcEntity.getLookPos());
    }

    @Override
    public String getValue() {
        return this.location.toString();
    }

    public WorldLocation getLocation() {
        return this.location;
    }
}
