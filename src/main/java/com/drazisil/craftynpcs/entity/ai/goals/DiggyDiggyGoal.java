package com.drazisil.craftynpcs.entity.ai.goals;

import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.HashSet;

public class DiggyDiggyGoal extends TargetGoal {
    private final NPCEntity me;
    private HashSet<Block> minableBlocks = new HashSet<>();

    public DiggyDiggyGoal(NPCEntity npc, HashSet<Block> mineableBlocks) {
        super(npc, false, true);
        this.me = npc;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.minableBlocks.addAll(mineableBlocks);
    }

    public boolean shouldExecute() {

        Block block = me.getBlockUnderFeet().getBlock();

        return minableBlocks.contains(block);
    }

    public void startExecuting() {

        me.swingArm(Hand.MAIN_HAND);
        ItemStack mainHandItemStack = me.getEquipmentInventory().getItemStackInSlot(me.MAIN_HAND_SLOT);
        if (mainHandItemStack == ItemStack.EMPTY) {
            return;
        }

        Item mainHandItem = mainHandItemStack.getItem();

        if (!(mainHandItem instanceof PickaxeItem)) {
            return;
        }

        BlockPos pos = me.getPosition().down(1);
        BlockState state = me.world.getBlockState(pos);
        Block block = state.getBlock();


            if (minableBlocks.contains(block)) {

            me.world.destroyBlock(pos, false);

        }

        super.startExecuting();
    }

    public boolean shouldContinueExecuting() {
        BlockPos pos = me.getPosition();
        pos = pos.down();
        Block block = me.getEntityWorld().getBlockState(pos).getBlock();

        return minableBlocks.contains(block);
    }
}