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
import java.util.Random;

public class LookAtTargetBlock extends Goal {
    protected final NPCEntity npcEntity;
    private double x;
    private double y;
    private double z;
    Vec3d closestBlock;
    private final double speedIn;
    private final World world;
    private HashSet<Block> mineableBlocks = new HashSet<>();
    protected final float maxDistance;
    private int lookTime;

    public LookAtTargetBlock(NPCEntity entityIn, HashSet<Block> mineableBlocks, float maxDistance) {
        this.npcEntity = entityIn;
        this.mineableBlocks.addAll(mineableBlocks);
        this.maxDistance = maxDistance;
        this.speedIn = 1.0d;
        this.world = npcEntity.world;
        this.setMutexFlags(EnumSet.of(Flag.LOOK));
//        if (watchTargetClass == PlayerEntity.class) {
//            this.field_220716_e = (new EntityPredicate()).setDistance((double)maxDistance).allowFriendlyFire().allowInvulnerable().setSkipAttackChecks().setCustomPredicate((p_220715_1_) -> {
//                return EntityPredicates.notRiding(entityIn).test(p_220715_1_);
//            });
//        } else {
//            this.field_220716_e = (new EntityPredicate()).setDistance((double)maxDistance).allowFriendlyFire().allowInvulnerable().setSkipAttackChecks();
//        }

    }

    public boolean shouldExecute() {
        closestBlock = this.getClosestBlock();
        if (closestBlock == null) {
            return false;
        }
//        System.out.println(closestBlock);
            npcEntity.setTargetPos(closestBlock);
            this.x = closestBlock.x;
            this.y = closestBlock.y;
            this.z = closestBlock.z;
            return true;

    }

    public boolean shouldContinueExecuting() {
//        closestBlock = this.getClosestBlock();
        if (closestBlock == null) {
            return false;
        } else
            return !(this.npcEntity.getDistanceSq(closestBlock) > (double) (this.maxDistance * this.maxDistance)) && this.lookTime > 0;
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
        Random random = this.npcEntity.getRNG();

        BlockScanBox scanBox = new BlockScanBox();
//        System.out.println(npcEntity.getPosition().toString());
        scanBox.generateBlockScanBox(world, npcEntity.getPosition(), (int) maxDistance, (int) maxDistance, (int) maxDistance, BlockScanBox.CenterType.CENTER, 0);

        Block blockToLookFor = mineableBlocks.iterator().next();

//        System.out.println(blockToLookFor);

        WorldLocation targetBlockLocation = scanBox.LocateBlock(blockToLookFor);

        if (!(targetBlockLocation == null)) {
//            System.out.println("Target Location: " + targetBlockLocation.toVec3d());
            npcEntity.setTargetPos(targetBlockLocation.toVec3d());
            return npcEntity.getTargetPos();
        }


//        BlockPos blockpos = new BlockPos(this.npcEntity.posX, this.npcEntity.getBoundingBox().minY, this.npcEntity.posZ);
//
//        for(int i = 0; i < 10; ++i) {
//            BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
//            Block block = this.world.getBlockState(blockpos1).getBlock();
////            System.out.println("Checking " + blockpos1.getX() + " , " + blockpos1.getY() + ", " + blockpos1.getZ());
//            if (mineableBlocks.contains(block)) {
//                System.out.println("Mineable block located");
//                return new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
//            }
//        }

        return null;
    }
}
