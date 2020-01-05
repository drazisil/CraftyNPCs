package com.drazisil.craftynpcs.entity.ai;

import com.drazisil.craftynpcs.entity.NPCEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.EnumSet;
import java.util.List;

public class DefendVillageGoal extends TargetGoal {
    private final NPCEntity field_75305_a;
    private LivingEntity field_75304_b;
    private final EntityPredicate field_223190_c = (new EntityPredicate()).setDistance(64.0D);

    public DefendVillageGoal(NPCEntity npc) {
        super(npc, false, true);
        this.field_75305_a = npc;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean shouldExecute() {
        AxisAlignedBB axisalignedbb = this.field_75305_a.getBoundingBox().grow(10.0D, 8.0D, 10.0D);
        List<LivingEntity> list = this.field_75305_a.world.getTargettableEntitiesWithinAABB(VillagerEntity.class, this.field_223190_c, this.field_75305_a, axisalignedbb);
        List<PlayerEntity> list1 = this.field_75305_a.world.getTargettablePlayersWithinAABB(this.field_223190_c, this.field_75305_a, axisalignedbb);

        for (LivingEntity livingentity : list) {
            VillagerEntity villagerentity = (VillagerEntity) livingentity;

            for (PlayerEntity playerentity : list1) {
                int i = villagerentity.func_223107_f(playerentity);
                if (i <= -100) {
                    this.field_75304_b = playerentity;
                }
            }
        }

        return this.field_75304_b != null;
    }

    public void startExecuting() {
        this.field_75305_a.setAttackTarget(this.field_75304_b);
        super.startExecuting();
    }
}