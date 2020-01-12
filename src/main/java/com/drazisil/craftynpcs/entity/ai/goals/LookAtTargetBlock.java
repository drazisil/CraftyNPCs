package com.drazisil.craftynpcs.entity.ai.goals;

import com.drazisil.craftynpcs.BlockScanBox;
import com.drazisil.craftynpcs.WorldLocation;
import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashSet;

public class LookAtTargetBlock extends Goal {
    private final NPCEntity npcEntity;
    private Vec3d closestBlock;
    private final World world;
    private HashSet<Block> mineableBlocks = new HashSet<>();
    private final float maxDistance;
    private int lookTime;

    public LookAtTargetBlock(NPCEntity entityIn, HashSet<Block> mineableBlocks, float maxDistance) {
        this.npcEntity = entityIn;
        this.mineableBlocks.addAll(mineableBlocks);
        this.maxDistance = maxDistance;
        this.world = npcEntity.world;
        this.setMutexFlags(EnumSet.of(Flag.LOOK));
    }

    public boolean shouldExecute() {
        closestBlock = this.getClosestBlock();
        if (closestBlock == null) {
            return false;
        }
            npcEntity.setTargetPos(closestBlock);
            return true;

    }

    public boolean shouldContinueExecuting() {
        return closestBlock != null && !(this.npcEntity.getDistanceSq(closestBlock) > (double) (this.maxDistance * this.maxDistance)) && this.lookTime > 0;
    }

    public void startExecuting() {
        this.lookTime = 40 + this.npcEntity.getRNG().nextInt(40);
    }

    public void resetTask() {
        npcEntity.setTargetPos(null);
        closestBlock = null;
    }

    public void tick() {
        this.npcEntity.getLookController().func_220679_a(closestBlock.x, closestBlock.y, closestBlock.z);
        --this.lookTime;
    }

    @Nullable
    private Vec3d getClosestBlock() {
        BlockScanBox scanBox = new BlockScanBox();
        scanBox.generateBlockScanBox(world, npcEntity.getPosition(), (int) maxDistance, (int) maxDistance, (int) maxDistance, BlockScanBox.CenterType.CENTER, 0);

        Block blockToLookFor = mineableBlocks.iterator().next();

        WorldLocation targetBlockLocation = scanBox.LocateBlock(blockToLookFor);

        if (!(targetBlockLocation == null)) {
            npcEntity.setTargetPos(targetBlockLocation.toVec3d());
            return npcEntity.getTargetPos();
        }

        return null;
    }
}
