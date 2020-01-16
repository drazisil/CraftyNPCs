package com.drazisil.craftynpcs.entity.ai.brain;

import com.drazisil.craftynpcs.entity.NPCEntity;
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
        brainSpeedCounter = 0;
    }
}
