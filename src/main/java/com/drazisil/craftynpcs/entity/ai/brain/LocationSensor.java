package com.drazisil.craftynpcs.entity.ai.brain;

import com.drazisil.craftynpcs.WorldLocation;
import com.drazisil.craftynpcs.entity.NPCEntity;

public class LocationSensor extends Sensor {

    private final WorldLocation location;

    public LocationSensor(NPCEntity npcEntity){
        super((npcEntity));
        this.name = "location_sensor";
        this.location = new WorldLocation(npcEntity.getPosition());
    }

    @Override
    public void update() {
        this.location.update(this.npcEntity.getPosition());
    }

    @Override
    public String getValue() {
        return this.location.toBlockPos().toString();
    }
}
