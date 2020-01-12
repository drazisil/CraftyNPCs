package com.drazisil.craftynpcs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;

public class BlockSave {

    private final World world;
    private final Block block;
    private final WorldLocation location;
    private final BlockState state;

    BlockSave(World world, WorldLocation location, Block block, BlockState state) {
        this.world = world;
        this.block = block;
        this.location = location;
        this.state = state;
    }

    public World getWorld() {
        return world;
    }

    public Block getBlock() {
        return block;
    }

    public WorldLocation getLocation() {
        return location;
    }

    public BlockState getState() {
        return state;
    }

    public String toString() {
        return "BlockSave {world=" + world.toString()
                + ", location=" + location.toVec3d()
                + ", block=" + block.toString()
                + ", state=" + state.toString() + "}";
    }

}
