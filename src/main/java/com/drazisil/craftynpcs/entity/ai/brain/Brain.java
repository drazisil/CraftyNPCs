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

        System.out.println("Creating should_brain");
        this.memories.add(new Memory(this.npcEntity,"should_brain", "true"));
        System.out.println("should_brain created");
        this.memories.add(new Memory(this.npcEntity,"should_random_walk", "true"));
        this.memories.add(new Memory(this.npcEntity,"should_look", "true"));
        this.memories.add(new Memory(this.npcEntity,"should_dig", "false"));
        this.memories.add(new Memory(this.npcEntity,"dig_pos", ""));
        String blocksToMine = "minecraft:grass_block,minecraft:dirt,minecraft:andesite"
                + ",minecraft:coal_ore,minecraft:diamond_ore,minecraft:diorite"
                + ",minecraft:emerald_ore,minecraft:gold_ore,minecraft:gravel"
                + ",minecraft:iron_ore,minecraft:lapis_ore,minecraft:obsidian"
                + ",minecraft:stone,minecraft:redstone_ore,minecraft:granite";
        this.memories.add(new Memory(this.npcEntity, "blocks_to_mine", blocksToMine));

        this.tasks.add(new DigTask("dig_task", this));
        this.tasks.add(new LookRandomTask("look_random_task", this));
        this.tasks.add(new WalkRandomTask("walk_random_task", this));
    }

    private boolean shouldBrain() {
        String shouldBrain = this.getMemoryValue("should_brain");
        System.out.println("Current value of should_brain: " + shouldBrain + " = " + (shouldBrain.equals("true")));
        return shouldBrain.equals("true");
    }

    public void tick(){

        System.out.println("Brain Tick Start");

        if (!shouldBrain()) {
            System.out.println("NoBrain");
            return;
        }

        int brainSpeed = 1;
        if (brainSpeedCounter < brainSpeed) {
            brainSpeedCounter++;
            return;
        }
        System.out.println("Starting Brain Tick");
        for (Sensor sensor: this.sensors) {
            sensor.update();
            System.out.println(sensor.getName() + ": " + sensor.getValue());
        }
        for (Task task: this.tasks) {
            task.tick();
        }
        for (Memory memory: this.memories) {
            System.out.println(memory.getName() + ": " + memory.getValue());
        }

        System.out.println("Looking at: " + getLookingAtBlock());
        brainSpeedCounter = 0;
    }

//    public void start(){
//        this.setMemoryValue("should_brain", "true");
//    }


    public void stop(){
        this.setMemoryValue("should_brain", "false");
        System.out.println("Value of should_brain after stopping: " + this.getMemoryValue("should_brain"));
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
