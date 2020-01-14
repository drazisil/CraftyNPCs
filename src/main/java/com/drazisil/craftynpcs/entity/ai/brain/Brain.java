package com.drazisil.craftynpcs.entity.ai.brain;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Brain {


    private Logger LOGGER;
    private ArrayList<Sensor> sensors = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();

    public Brain(Logger logger) {
        this.LOGGER = logger;
    }

    public void tick(){
//        LOGGER.info("Brain Tick");
    }
}
