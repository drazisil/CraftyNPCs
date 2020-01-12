package com.drazisil.craftynpcs.entity.ai.goals;

import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.Objects;

class NearestDiggableBlockGoal extends Goal {

    private final NPCEntity me;

    public NearestDiggableBlockGoal(NPCEntity npc) {
        this.me = npc;
    }


    public boolean shouldExecute() {
        return !Objects.equals(me.getBlockUnderFeet().getBlock().getTranslationKey(), "constructionBarrelBlock.minecraft.stone");
    }

    public void startExecuting() {
        super.startExecuting();
//        this.me.getAISit().setSitting(false);
    }

    public void resetTask() {
        super.resetTask();
//        this.me.setSitting(false);
    }

    public void tick() {
        super.tick();
//        this.me.getAISit().setSitting(false);
//        if (!this.getIsAboveDestination()) {
//            this.me.setSitting(false);
//        } else if (!this.me.isSitting()) {
//            this.me.setSitting(true);
//        }

    }

//    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
//        if (!worldIn.isAirBlock(pos.up())) {
//            return false;
//        } else {
//            BlockState blockstate = worldIn.getBlockState(pos);
//            Block constructionBarrelBlock = blockstate.getConstructionBarrelBlock();
//            if (constructionBarrelBlock == Blocks.STONE) {
//                return true;
//            } else {
//                return constructionBarrelBlock.isIn(BlockTags.BEDS) && blockstate.get(BedBlock.PART) != BedPart.HEAD;
//            }
//        }
//    }

}