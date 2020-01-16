package com.drazisil.craftynpcs;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class WorldLocation {

    private int x;
    private int y;
    private int z;

    public WorldLocation(Vec3d vec3dIn){
        this.x = (int) vec3dIn.x;
        this.y = (int) vec3dIn.y;
        this.z = (int) vec3dIn.z;
    }

    public WorldLocation(BlockPos blockPosIn) {
        this.x = blockPosIn.getX();
        this.y = blockPosIn.getY();
        this.z = blockPosIn.getZ();
    }

    private WorldLocation(WorldLocation worldLocationIn){
        this.x = worldLocationIn.getX();
        this.y = worldLocationIn.getY();
        this.z = worldLocationIn.getZ();
    }

    public WorldLocation(double posX, double posY, double posZ) {
        this.x = (int) posX;
        this.y = (int) posY;
        this.z = (int) posZ;
    }

    public void update(Vec3d vec3dIn){
        this.x = (int) vec3dIn.x;
        this.y = (int) vec3dIn.y;
        this.z = (int) vec3dIn.z;
    }

    public void update(BlockPos blockPosIn) {
        this.x = blockPosIn.getX();
        this.y = blockPosIn.getY();
        this.z = blockPosIn.getZ();
    }

    public void update(WorldLocation locationIn) {
        this.x = locationIn.getX();
        this.y = locationIn.getY();
        this.z = locationIn.getZ();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    WorldLocation copy() {
        return new WorldLocation(this);
    }

    public Vec3d toVec3d() {
        return new Vec3d(this.x, this.y, this.z);
    }

    public Vec3i toVec3i() {
        return new Vec3i(this.x, this.y, this.z);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    @Override
    public String toString() {
        return "x: " + this.x + ", y: " + this.y + ", z: " + this.z;
    }
}
