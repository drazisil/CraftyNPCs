package com.drazisil.craftynpcs.entity;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.entity.ai.DefendVillageGoal;
import com.drazisil.craftynpcs.entity.ai.DiggyDiggyGoal;
import com.sun.istack.internal.NotNull;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

@SuppressWarnings("EntityConstructor")
public class NPCEntity extends CreatureEntity  {

    private final NPCManager npcManager;
    private final String name;
    public final Integer MAIN_HAND_SLOT = 1;

    public Inventory getInventory() {
        return inventory;
    }

    private final Inventory inventory = new Inventory(18);

    public NPCEntity(EntityType<? extends NPCEntity> type, World worldIn) {
        super(type, worldIn);
        this.name = "miner";

        ItemStack pickaxeStack = new ItemStack(Items.DIAMOND_PICKAXE);

        this.inventory.setInventorySlotContents(MAIN_HAND_SLOT, pickaxeStack);
        this.npcManager = CraftyNPCs.getInstance().getNpcManager();
        npcManager.register(this);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(2, new MoveTowardsVillageGoal(this, 0.6D));
        this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> false));
        this.goalSelector.addGoal(5, new DiggyDiggyGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new DefendVillageGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
//        this.targetSelector.addGoal(3, new NearestDiggableBlockGoal(this, MobEntity.class, 5, false, false, (p_213619_0_) -> {
//            return p_213619_0_ instanceof IMob && !(p_213619_0_ instanceof CreeperEntity);
//        }));
//
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(100.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    public BlockState getBlockUnderFeet() {
        return this.world.getBlockState(this.getPosition().down(1));
    }

    public boolean canHarvestBlock(BlockState state) {
        return state.getMaterial().isToolNotRequired() || this.inventory.getStackInSlot(MAIN_HAND_SLOT).canHarvestBlock(state);
    }


    @Override
    public void remove() {
        super.remove();
        npcManager.deregister(this);
    }


    @Override
    @NotNull
    public ITextComponent getName() {
        return new StringTextComponent(this.name);
    }

}
