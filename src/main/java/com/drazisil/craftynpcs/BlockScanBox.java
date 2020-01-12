package com.drazisil.craftynpcs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockScanBox {

    private final CraftyNPCs plugin = CraftyNPCs.getInstance();

    public enum CenterType {
        NONE,
        CENTER,
        CENTER_OFFSET_Y,
    }

    private World world;
    private int height;
    private int width;
    private int depth;



    private int leftSideX;
    private int rightSideX;
    private int frontSideZ;
    private int backSideZ;
    private int topSideY;
    private int bottomSideY;

    private final ArrayList<BlockSave> blocks = new ArrayList<>();

    // Methods


    public void generateBlockScanBox(World world, Vec3d rawStartLocation,
                                      int height, int width, int depth,
                                      CenterType typeOfBox,
                                      int offsetY) {
        generateBlockScanBox(world, new WorldLocation(rawStartLocation), height, width, depth, typeOfBox, offsetY);
    }

    public void generateBlockScanBox(World world, BlockPos rawStartLocation,
                                      int height, int width, int depth,
                                      CenterType typeOfBox,
                                      int offsetY) {
        generateBlockScanBox(world, new WorldLocation(rawStartLocation), height, width, depth, typeOfBox, offsetY);
    }

    private void generateBlockScanBox(World world, WorldLocation rawStartLocation,
                                       int height, int width, int depth,
                                       CenterType typeOfBox,
                                       int offsetY) {

        this.world = world;

        WorldLocation startLocation = rawStartLocation.copy();

//        System.out.println(rawStartLocation.toVec3d() + " / " + startLocation.toVec3d());

//        System.out.println("Start: " + startLocation.toVec3d());

        setHeight(height);
        setWidth(width);
        setDepth(depth);

        int startX = 0;
        int startY = 0;
        int startZ = 0;

        switch (typeOfBox) {
            case CENTER:
                startX = getSquareStartX(startLocation, width);
                startY = getSquareStartY(startLocation, height);
                startZ = getSquareStartZ(startLocation, depth);
                break;
            case CENTER_OFFSET_Y:
                startX = getSquareStartX(startLocation, width);
                startY = getSquareStartY(startLocation, height) + offsetY;
                startZ = getSquareStartZ(startLocation, depth);
                break;

            case NONE:
                startX = startLocation.getX();
                startY = startLocation.getY();
                startZ = startLocation.getZ();
                break;
        }

        computeSides(startX, startY, startZ);

        WorldLocation cursorLocation = startLocation.copy();

//        System.out.println("Scan Start");
        for (int y = startY; y > (startY - depth); y -= 1) {
            cursorLocation.setY(y);

            for (int x = startX; x < (startX + width); x += 1) {
                cursorLocation.setX(x);

                for (int z = startZ; z < (startZ + depth); z += 1) {
                    cursorLocation.setZ(z);

                    // Capture the block
                    BlockPos blockPos = cursorLocation.copy().toBlockPos();
//                    CraftyNPCs.LOGGER.info(blockPos);
                    BlockState state = world.getBlockState(cursorLocation.toBlockPos());

                    Block block = state.getBlock();

                    BlockSave blockSave = new BlockSave(world, cursorLocation.copy(), block, state);
//                    CraftyNPCs.LOGGER.info(blockSave.toString());
                    addBlock(blockSave);
                }
            }

        }
//        System.out.println("Scan End");
    }

    public double getLeftSideX() {
        return Math.floor(this.leftSideX);
    }

    public double getRightSideX() {
        return Math.floor(rightSideX);
    }

    public double getFrontSideZ() {
        return Math.floor(frontSideZ);
    }

    public double getBackSideZ() {
        return Math.floor(backSideZ);
    }

    public double getTopSideY() {  return Math.floor(topSideY);  }

    public double getBottomSideY() { return Math.floor(bottomSideY); }


    private void setHeight(int height) {
        this.height = height;
    }

    private void setWidth(int width) {
        this.width = width;
    }

    private void setDepth(int depth) {
        this.depth = depth;
    }

    public ArrayList<BlockSave> getBlocks() {
        return blocks;
    }

    private void addBlock(BlockSave block) {
        this.blocks.add(block);
    }

    private void computeSides(int startX, int startY, int startZ) {
        this.leftSideX = (int) Math.floor(startX);
        this.frontSideZ = (int) Math.floor(startZ);
        this.topSideY = (int) Math.floor(startY);

        this.rightSideX = this.leftSideX + this.width - 1;
        this.backSideZ = this.frontSideZ + this.depth - 1;
        this.bottomSideY = this.topSideY - this.height + 1;
    }

    private int getSquareStartX(WorldLocation location, int width) {

        int startOffset = (width - 1) / 2;

        WorldLocation startLocation = location.copy();
        startLocation.setX(startLocation.getX() - startOffset);
        return  startLocation.getX();
    }

    private int getSquareStartY(WorldLocation location, int height) {
        assert (height % 2) == 0;

        int startOffset = (height - 1) / 2;

        WorldLocation startLocation = location.copy();
        startLocation.setY(startLocation.getY() + startOffset);
        return startLocation.getY();
    }

    private int getSquareStartZ(WorldLocation location, int depth) {

        int startOffset = (depth - 1) / 2;

        WorldLocation startLocation = location.copy();
        startLocation.setZ(startLocation.getZ() - startOffset);
        return startLocation.getZ();
    }

//    public void restoreAll(BlockSaveRecord blocksToChange) {
//
//        ArrayList<BlockSave> currentBlocks = blocksToChange.getBlocks();
//        ArrayList<BlockSave> restoreBlocks = getBlocks();
//
//
//        for (int i = 0; i < blocksToChange.getBlocks().size(); i++) {
//            BlockSave currentBlock = currentBlocks.get(i);
//            BlockSave restoreBlock = restoreBlocks.get(i);
//
//            currentBlock.getBlock().setType(restoreBlock.getType());
//            restoreBlock.getState().update();
//        }
//
//    }

    private boolean compareFloorDouble(double d1, double d2) {
        return Math.floor(d1) == Math.floor(d2);
    }

    private boolean compareFloorLocation(WorldLocation l1, WorldLocation l2) {
        return (compareFloorDouble(l1.getX(), l2.getX())
                && compareFloorDouble(l1.getY(), l2.getY())
                && compareFloorDouble(l1.getZ(), l2.getZ()));
    }

    public boolean isBorder(WorldLocation loc) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        return x == this.getLeftSideX()
                || x == this.getRightSideX()
                || y == this.getTopSideY()
                || y == this.getBottomSideY()
                || z == this.getFrontSideZ()
                || z == this.getBackSideZ();
    }

    public WorldLocation LocateBlock(Block block){

//        System.out.println(this.leftSideX + " / " + this.rightSideX);
//        System.out.println(this.frontSideZ + " / " + this.backSideZ);

        for (BlockSave blockSave: this.blocks) {
//            System.out.println("Pos: " + blockSave.getLocation().getX() + " " + blockSave.getLocation().getY() + " " + blockSave.getLocation().getZ());
            if (blockSave.getLocation().getX() == -186 && blockSave.getLocation().getZ() == -88) {
//                System.out.println("Block!: " + blockSave.getBlock() + " = " + blockSave.getLocation().toVec3d());
            }
            if (blockSave.getBlock() == block) {
//                System.out.println("Found!: " + blockSave.getLocation());
            }
//            System.out.println(blockSave.getBlock() + " = " + blockSave.getLocation().toVec3d());

            if (blockSave.getBlock() == block) {
                return blockSave.getLocation();
            }
        }
        return null;
    }
}