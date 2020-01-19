package com.drazisil.craftynpcs.entity.ai.brain.task;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.WorldLocation;
import com.drazisil.craftynpcs.entity.NPCEntity;
import com.drazisil.craftynpcs.entity.ai.brain.Brain;
import com.drazisil.craftynpcs.entity.ai.brain.VisionSensor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;

public class DigTask extends Task {

    private final String name;
    private final Brain brain;
    private final NPCEntity digger;
    private final World world;
    private int durabilityRemainingOnBlock;
    private int digTicks;
    private int initialBlockDamage;
    private int initialDamage;
    private ArrayList<Block> minableBlocks = new ArrayList<>();

    public DigTask(String name, Brain brain) {
        this.name = name;
        this.brain = brain;
        this.digger = this.brain.npcEntity;
        this.world = this.digger.getEntityWorld();
        fetchMinableBlocksFromMemory();
        System.out.println(minableBlocks.toString());

    }

    private void fetchMinableBlocksFromMemory() {
        String mineableBlockNamesArray = brain.getMemoryValue("blocks_to_mine");
        ArrayList<String> mineableBlockNames = new ArrayList<>(Arrays.asList(mineableBlockNamesArray.split(",")));
        for (Block block: CraftyNPCs.getBlockRegistry().getValues()) {
//            System.out.println(block.getRegistryName().toString());
            if (mineableBlockNames.contains(block.getRegistryName().toString())) {
                minableBlocks.add(block);
            }
        }

    }

    private boolean shouldDig() {
        checkBlockMinable();
        return brain.getMemoryValue("should_dig").equals("true") && !brain.getMemoryValue("dig_pos").equals("") && digger.getPosition().getY() > 15;

    }

    @Override
    public void tick() {

        if (!shouldDig()) return;

//        brain.setMemoryValue("dig_pos", new WorldLocation(digger.getPosition().down()).toString());
        this.diggingTick();
        brain.setMemoryValue("dig_pos", "");
        brain.setMemoryValue("should_dig_pos", "false");
        brain.setMemoryValue("should_look", "true");
        brain.setMemoryValue("should_random_walk", "true");

    }

    public void diggingTick() {
        ++this.digTicks;
        BlockState blockstate1;
        BlockPos digPos = new WorldLocation(brain.getMemoryValue("dig_pos")).toBlockPos();
        if (!digger.isDigging) {
            blockstate1 = world.getBlockState(digPos);
            if (blockstate1.isAir(this.world, digPos)) {
                digger.isDigging = false;
            } else {
                float f = this.func_225417_a(blockstate1, digPos);
                if (f >= 1.0F) {
                    digger.isDigging = false;
                    digger.tryHarvestBlock(digPos);
                }
            }
        } else if (digger.isDigging) {
            blockstate1 = this.world.getBlockState(digPos);
            if (blockstate1.isAir(this.world, digPos)) {
                this.durabilityRemainingOnBlock = -1;
                digger.isDigging = false;
            } else {
                this.func_225417_a(blockstate1, digPos);
            }
        }

    }

    private float func_225417_a(BlockState p_225417_1_, BlockPos loc) {
        int i = this.digTicks - this.initialBlockDamage;
        float f = p_225417_1_.getBlockHardness(world, loc)  * (float)(i + 1);
        int j = (int)(f * 10.0F);
        if (j != this.durabilityRemainingOnBlock) {
            this.durabilityRemainingOnBlock = j;
        }

        return f;
    }

    private void checkBlockMinable() {
        if (brain.getLookingAtBlock().equals(minableBlocks.get(0))) {
            brain.setMemoryValue("should_random_walk", "false");
            brain.setMemoryValue("should_look", "false");
            if (world.isRemote()) {
                digger.sendMessage("Found something...");
            }
            brain.setMemoryValue("dig_pos", ((VisionSensor)brain.getSensorByName("vision_sensor")).getLocation().toString());
            brain.setMemoryValue("should_dig", "true");
        }
    }

}
