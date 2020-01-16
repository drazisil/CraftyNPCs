package com.drazisil.craftynpcs.entity;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.WorldLocation;
import com.drazisil.craftynpcs.entity.ai.NPCManager;
import com.drazisil.craftynpcs.entity.ai.brain.Brain;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Iterator;

import static net.minecraft.block.ChestBlock.getDirectionToAttached;
import static net.minecraft.state.properties.ChestType.SINGLE;

@SuppressWarnings("EntityConstructor")
public class NPCEntity extends MobEntity {

    private final NPCManager npcManager;
    private final String name;
    public final Integer MAIN_HAND_SLOT = 0;

    private BlockPos targetPos;
    public boolean isDigging = false;

    // This is the block the entity is looking at
    private WorldLocation rayTraceBlock = new WorldLocation(0, 0, 0);

    private Brain brain = new Brain(CraftyNPCs.LOGGER, this);

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

    @OnlyIn(Dist.CLIENT)
    public boolean isWearing(PlayerModelPart part) {
//        switch (part) {
//            case LEFT_SLEEVE:
//            case RIGHT_SLEEVE:
//                return false;
//        }
        return true;
    }

    public WorldLocation getLookPos() {
        return rayTraceBlock;
    }


    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        if (slotIn == EquipmentSlotType.MAINHAND) {
            return this.getEquipmentInventory().getStackInSlot(MAIN_HAND_SLOT);
        } else if (slotIn == EquipmentSlotType.OFFHAND) {
            return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        compound.putFloat("Health", this.getHealth());
        compound.putShort("HurtTime", (short)this.hurtTime);
        compound.putShort("DeathTime", (short)this.deathTime);
        compound.putFloat("AbsorptionAmount", this.getAbsorptionAmount());

        compound.put("OreInventory", this.equipmentInventory.serializeNBT());


        EquipmentSlotType[] var2 = EquipmentSlotType.values();
        int var3 = var2.length;

        int var4;
        EquipmentSlotType equipmentslottype1;
        ItemStack itemstack1;
        for(var4 = 0; var4 < var3; ++var4) {
            equipmentslottype1 = var2[var4];
            itemstack1 = this.getItemStackFromSlot(equipmentslottype1);
            if (!itemstack1.isEmpty()) {
                this.getAttributes().removeAttributeModifiers(itemstack1.getAttributeModifiers(equipmentslottype1));
            }
        }

        compound.put("Attributes", SharedMonsterAttributes.writeAttributes(this.getAttributes()));
        var2 = EquipmentSlotType.values();
        var3 = var2.length;

        for(var4 = 0; var4 < var3; ++var4) {
            equipmentslottype1 = var2[var4];
            itemstack1 = this.getItemStackFromSlot(equipmentslottype1);
            if (!itemstack1.isEmpty()) {
                this.getAttributes().applyAttributeModifiers(itemstack1.getAttributeModifiers(equipmentslottype1));
            }
        }

        if (!this.getActivePotionMap().isEmpty()) {
            ListNBT listnbt = new ListNBT();
            Iterator var8 = this.getActivePotionMap().values().iterator();

            while(var8.hasNext()) {
                EffectInstance effectinstance = (EffectInstance)var8.next();
                listnbt.add(effectinstance.write(new CompoundNBT()));
            }

            compound.put("ActiveEffects", listnbt);
        }

        compound.putBoolean("FallFlying", this.isElytraFlying());

//        compound.put("Brain", (INBT)this.brain.serialize(NBTDynamicOps.INSTANCE));
    }

    public void readAdditional(CompoundNBT compound) {

        this.equipmentInventory.deserializeNBT((CompoundNBT) compound.get("OreInventory"));

        this.setAbsorptionAmount(compound.getFloat("AbsorptionAmount"));
        if (compound.contains("Attributes", 9) && this.world != null && !this.world.isRemote) {
            SharedMonsterAttributes.readAttributes(this.getAttributes(), compound.getList("Attributes", 10));
        }

        if (compound.contains("ActiveEffects", 9)) {
            ListNBT listnbt = compound.getList("ActiveEffects", 10);

            for(int i = 0; i < listnbt.size(); ++i) {
                CompoundNBT compoundnbt = listnbt.getCompound(i);
                EffectInstance effectinstance = EffectInstance.read(compoundnbt);
                if (effectinstance != null) {
                    this.getActivePotionMap().put(effectinstance.getPotion(), effectinstance);
                }
            }
        }

        if (compound.contains("Health", 99)) {
            this.setHealth(compound.getFloat("Health"));
        }

        this.hurtTime = compound.getShort("HurtTime");
        this.deathTime = compound.getShort("DeathTime");
        if (compound.contains("Team", 8)) {
            String s = compound.getString("Team");
            ScorePlayerTeam scoreplayerteam = this.world.getScoreboard().getTeam(s);
            boolean flag = scoreplayerteam != null && this.world.getScoreboard().addPlayerToTeam(this.getCachedUniqueIdString(), scoreplayerteam);
            if (!flag) {
                LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", s);
            }
        }

        if (compound.getBoolean("FallFlying")) {
            this.setFlag(7, true);
        }

        if (compound.contains("Brain", 10)) {
//            this.brain = this.createBrain(new Dynamic(NBTDynamicOps.INSTANCE, compound.get("Brain")));
        }

    }

    @Override
    public void livingTick() {
        super.livingTick();
        BlockPos lookingBlockPos = ((BlockRayTraceResult) this.func_213324_a(20.0D, 0.0F, false)).getPos();
        this.rayTraceBlock.update(lookingBlockPos);
        this.brain.tick();
    }

    /*
        Default call is         this.rayTraceBlock = entity.func_213324_a(20.0D, 0.0F, false);
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public RayTraceResult func_213324_a(double p_213324_1_, float p_213324_3_, boolean isFluid) {
        Vec3d vec3d = this.getEyePosition(p_213324_3_);
        Vec3d vec3d1 = this.getLook(p_213324_3_);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * p_213324_1_, vec3d1.y * p_213324_1_, vec3d1.z * p_213324_1_);
        return this.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.OUTLINE, isFluid ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, this));
    }

    protected void registerGoals() {
        super.registerGoals();

////        this.goalSelector.addGoal(1, new LocateMineableBlockGoal(this, getMineableBlocks(), 0.5D));
//        float maxScanDistance = 50.0f;
//        this.goalSelector.addGoal(1, new LookAtTargetBlock(this, getMineableBlocks(), maxScanDistance));
//        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.5D, maxScanDistance));
//        this.goalSelector.addGoal(3, new DiggyDiggyGoal(this, getMineableBlocks()));
//        this.goalSelector.addGoal(3, new MakeStructure(this, new AIStructure()));
//        this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.5D));
////        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
////        this.goalSelector.addGoal(2, new MoveTowardsVillageGoal(this, 0.6D));
////        this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> false));
////        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
////        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
////        this.targetSelector.addGoal(1, new DefendVillageGoal(this));
////        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
////        this.targetSelector.addGoal(3, new NearestDiggableBlockGoal(this, MobEntity.class, 5, false, false, (p_213619_0_) -> {
////            return p_213619_0_ instanceof IMob && !(p_213619_0_ instanceof CreeperEntity);
////        }));
////
    }

    public boolean isNoDespawnRequired() {
        return true;
    }

    public float getBlockPathWeight(BlockPos pos) {
        return this.getBlockPathWeight(pos, this.world);
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return 0.0F;
    }



    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(100.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType equipmentSlotType, ItemStack itemStack) {

    }

    @Override
    public HandSide getPrimaryHand() {
        return null;
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

    public BlockPos getTargetPos() {
        return targetPos;
    }

    public void setTargetPos(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    public NPCTileEntity getEquipmentInventory() {
        return equipmentInventory;
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
