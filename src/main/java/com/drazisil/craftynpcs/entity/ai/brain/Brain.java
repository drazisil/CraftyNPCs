package com.drazisil.craftynpcs.entity.ai.brain;

import com.drazisil.craftynpcs.entity.NPCEntity;
import com.drazisil.craftynpcs.util.BlockUtil;
import net.minecraft.block.Block;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Brain {


    private final NPCEntity npcEntity;
    private Logger LOGGER;
    private ArrayList<Sensor> sensors = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();

    private int brainSpeed = 20;
    private int brainSpeedCounter = 0;

    public Brain(Logger logger, NPCEntity npcEntity) {
        this.LOGGER = logger;
        this.npcEntity = npcEntity;
        this.sensors.add(new LocationSensor(this.npcEntity));
        this.sensors.add(new VisionSensor(this.npcEntity));
    }

    public void tick(){
        if (brainSpeedCounter < brainSpeed) {
            brainSpeedCounter++;
            return;
        }
//        LOGGER.debug("Brain Tick");
        for (Sensor sensor: this.sensors) {
            sensor.update();
        }
        for (Sensor sensor: this.sensors) {
            LOGGER.debug(sensor.getName() + ": " + sensor.getValue());
        }
        LOGGER.info("Looking at: " + getLookingAtBlock());
        brainSpeedCounter = 0;
    }

    private Sensor getSensorByName(String name) {
        for (Sensor sensor: this.sensors) {
            if(sensor.getName().equals(name)) return sensor;
        }
        return null;
    }

    public Block getLookingAtBlock() {
        if (!(this.getSensorByName("vision_sensor") == null)) {
            return BlockUtil.getBlockAtPos(npcEntity.getEntityWorld(), ((VisionSensor)this.getSensorByName("vision_sensor")).getLocation().toBlockPos());
        }
        return null;
    }
}
