package com.drazisil.craftynpcs.entity;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.entity.ai.DiggyDiggyGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.minecraft.block.ChestBlock.getDirectionToAttached;
import static net.minecraft.state.properties.ChestType.SINGLE;

@SuppressWarnings("EntityConstructor")
public class NPCEntity extends CreatureEntity  {

    private final NPCManager npcManager;
    private final String name;
    public final Integer MAIN_HAND_SLOT = 0;
    private int numPlayersUsing;

    public NPCTileEntity getEquipmentInventory() {
        return equipmentInventory;
    }

    public static final EnumProperty<ChestType> TYPE;
    private final NPCTileEntity equipmentInventory = new NPCTileEntity();

    public static InventoryFactory<INamedContainerProvider> getInventory() {
        return inventory;
    }

    private static final NPCEntity.InventoryFactory<INamedContainerProvider> inventory;

    public NPCEntity(EntityType<? extends NPCEntity> type, World worldIn) {
        super(type, worldIn);
        this.name = "miner";

        ItemStack pickaxeStack = new ItemStack(Items.DIAMOND_PICKAXE);

        this.equipmentInventory.setInventorySlotContents(MAIN_HAND_SLOT, pickaxeStack);
        this.npcManager = CraftyNPCs.getInstance().getNpcManager();
        npcManager.register(this);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
//        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
//        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
//        this.goalSelector.addGoal(2, new MoveTowardsVillageGoal(this, 0.6D));
//        this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> false));
        this.goalSelector.addGoal(5, new DiggyDiggyGoal(this));
//        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
//        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
//        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
//        this.targetSelector.addGoal(1, new DefendVillageGoal(this));
//        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
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
        return state.getMaterial().isToolNotRequired() || this.equipmentInventory.getStackInSlot(MAIN_HAND_SLOT).canHarvestBlock(state);
    }


    @Override
    public void remove() {
        super.remove();
        npcManager.deregister(this);
    }


    @Override
    public ITextComponent getName() {
        return new StringTextComponent(this.name);
    }



    @Nullable
    public static <T> T func_220106_a(BlockState p_220106_0_, IWorld p_220106_1_, BlockPos p_220106_2_, boolean allowBlocked, NPCEntity.InventoryFactory<T> p_220106_4_) {
        TileEntity tileentity = p_220106_1_.getTileEntity(p_220106_2_);
        if (!(tileentity instanceof ChestTileEntity)) {
            return null;
        } else {
            ChestTileEntity chesttileentity = (ChestTileEntity)tileentity;
            ChestType chesttype = (ChestType)p_220106_0_.get(TYPE);
            if (chesttype == SINGLE) {
                return p_220106_4_.forSingle(chesttileentity);
            } else {
                BlockPos blockpos = p_220106_2_.offset(getDirectionToAttached(p_220106_0_));
                BlockState blockstate = p_220106_1_.getBlockState(blockpos);

                return p_220106_4_.forSingle(chesttileentity);
            }
        }
    }



    interface InventoryFactory<T> {
        T forSingle(INamedContainerProvider var1);
    }

    static {
        TYPE = BlockStateProperties.CHEST_TYPE;
        inventory = new NPCEntity.InventoryFactory<INamedContainerProvider>() {
            public INamedContainerProvider forSingle(INamedContainerProvider p_212856_1_) {
                return p_212856_1_;
            }
        };
    }

}
