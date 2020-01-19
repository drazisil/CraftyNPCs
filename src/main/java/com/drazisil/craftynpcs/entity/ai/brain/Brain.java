package com.drazisil.craftynpcs.entity.ai.brain;

import com.drazisil.craftynpcs.entity.NPCEntity;
import com.drazisil.craftynpcs.entity.ai.brain.task.DigTask;
import com.drazisil.craftynpcs.entity.ai.brain.task.LookRandomTask;
import com.drazisil.craftynpcs.entity.ai.brain.task.Task;
import com.drazisil.craftynpcs.entity.ai.brain.task.WalkRandomTask;
import com.drazisil.craftynpcs.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Brain {


    public final NPCEntity npcEntity;
    private final Logger LOGGER;
    private final ArrayList<Sensor> sensors = new ArrayList<>();
    private final ArrayList<Memory> memories = new ArrayList<>();
    private final ArrayList<Task> tasks = new ArrayList<>();

    private int brainSpeedCounter = 0;

    public Brain(Logger logger, NPCEntity npcEntity) {
        this.LOGGER = logger;
        this.npcEntity = npcEntity;
        this.sensors.add(new LocationSensor(this.npcEntity));
        this.sensors.add(new VisionSensor(this.npcEntity));

        this.memories.add(new Memory(this.npcEntity,"should_brain", "true"));
        this.memories.add(new Memory(this.npcEntity,"should_random_walk", "true"));
        this.memories.add(new Memory(this.npcEntity,"should_look", "true"));
        this.memories.add(new Memory(this.npcEntity,"should_dig", "false"));
        this.memories.add(new Memory(this.npcEntity,"dig_pos", ""));
        this.memories.add(new Memory(this.npcEntity, "blocks_to_mine", "minecraft:grass_block,minecraft:dirt"));

        this.tasks.add(new DigTask("dig_task", this));
        this.tasks.add(new LookRandomTask("look_random_task", this));
        this.tasks.add(new WalkRandomTask("walk_random_task", this));
    }

    private boolean shouldBrain() {
        return this.getMemoryValue("should_brain").equals("true");
    }

    public void tick(){

        if (!shouldBrain()) return;

        int brainSpeed = 1;
        if (brainSpeedCounter < brainSpeed) {
            brainSpeedCounter++;
            return;
        }
//        LOGGER.debug("Brain Tick");
        for (Sensor sensor: this.sensors) {
            sensor.update();
            LOGGER.debug(sensor.getName() + ": " + sensor.getValue());
        }
        for (Task task: this.tasks) {
            task.tick();
        }
        for (Memory memory: this.memories) {
            LOGGER.info(memory.getName() + ": " + memory.getValue());
        }

        LOGGER.info("Looking at: " + getLookingAtBlock());
        brainSpeedCounter = 0;
    }

    public Sensor getSensorByName(String name) {
        for (Sensor sensor: this.sensors) {
            if(sensor.getName().equals(name)) return sensor;
        }
        return null;
    }

    public String getMemoryValue(String name) {
        for (Memory memory: this.memories) {
            if(memory.getName().equals(name)) return memory.getValue();
        }
        return null;
    }

    public void setMemoryValue(String name, String value) {
        for (Memory memory: this.memories) {
            if(memory.getName().equals(name)) memory.setValue(value);
        }
    }

    public Block getLookingAtBlock() {
        if (!(this.getSensorByName("vision_sensor") == null)) {
            return BlockUtil.getBlockAtPos(npcEntity.getEntityWorld(), ((VisionSensor)this.getSensorByName("vision_sensor")).getLocation().toBlockPos());
        }
        return null;
    }

    public BlockState getLookingAtBlockState() {
        if (!(this.getSensorByName("vision_sensor") == null)) {
            return BlockUtil.getBlockStateAtPos(npcEntity.getEntityWorld(), ((VisionSensor)this.getSensorByName("vision_sensor")).getLocation().toBlockPos());
        }
        return null;
    }
}
