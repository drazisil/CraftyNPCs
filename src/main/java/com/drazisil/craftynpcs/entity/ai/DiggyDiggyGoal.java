package com.drazisil.craftynpcs.entity.ai;

import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ToolType;

import java.util.EnumSet;
import java.util.Objects;

public class DiggyDiggyGoal extends TargetGoal {
    private final NPCEntity me;
    private LivingEntity field_75304_b;
    private final EntityPredicate field_223190_c = (new EntityPredicate()).setDistance(64.0D);

    public DiggyDiggyGoal(NPCEntity npc) {
        super(npc, false, true);
        this.me = npc;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean shouldExecute() {

        BlockPos pos = me.getPosition();
        Block block = me.getBlockUnderFeet().getBlock();

        return Objects.equals(block.getTranslationKey(), "block.minecraft.stone");
    }

    public void startExecuting() {

        me.swingArm(Hand.MAIN_HAND);
//        System.out.println("Diggy diggy hole!");
        ItemStack mainHandItemStack = me.getInventory().getStackInSlot(me.MAIN_HAND_SLOT);
        if (mainHandItemStack == ItemStack.EMPTY) {
            return;
        }

        Item mainHandItem = mainHandItemStack.getItem();

        if (!(mainHandItem instanceof PickaxeItem)) {
            return;
        }

        PickaxeItem mainHandTool = (PickaxeItem) mainHandItem;

        BlockPos pos = me.getPosition().down(1);
        BlockPos lookPos = new BlockPos(me.getLookVec());
        BlockState state = me.world.getBlockState(lookPos);
        System.out.println(state.toString());
//        BlockState state = me.getBlockUnderFeet();
        Block block = state.getBlock();
        ToolType toolToBreak = block.getHarvestTool(state);

        if (mainHandTool.canHarvestBlock(state)) {

//            net.minecraft.world.storage.loot.LootContext.Builder lootcontext$builder = (new net.minecraft.world.storage.loot.LootContext.Builder(worldIn)).withRandom(worldIn.rand).withParameter(LootParameters.POSITION, pos).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, null);
//            List<ItemStack> drops = state.getDrops(lootcontext$builder);

            me.world.destroyBlock(pos, false);

//            state.getBlock().harvestBlock();
        }

        super.startExecuting();
    }

//    public boolean shouldContinueExecuting() {
//        BlockPos pos = me.getPosition();
//        pos = pos.down();
//        Block block = me.getEntityWorld().getBlockState(pos).getBlock();
//
//        return Objects.equals(block.getTranslationKey(), "block.minecraft.stone");
//    }
}