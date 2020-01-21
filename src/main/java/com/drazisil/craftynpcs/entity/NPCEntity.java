package com.drazisil.craftynpcs.entity;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.WorldLocation;
import com.drazisil.craftynpcs.entity.ai.NPCManager;
import com.drazisil.craftynpcs.entity.ai.brain.Brain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import com.drazisil.craftynpcs.entity.ai.LookController;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.pathfinding.PathNavigator;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import static net.minecraft.block.ChestBlock.getDirectionToAttached;
import static net.minecraft.inventory.container.ContainerType.GENERIC_9X3;
import static net.minecraft.state.properties.ChestType.SINGLE;

@SuppressWarnings("EntityConstructor")
public class NPCEntity extends LivingEntity {

    private final NPCManager npcManager;
    private final String name;
    private final Integer MAIN_HAND_SLOT = 0;
    public final NPCInventoryContainer container;

    protected PathNavigator navigator;



    private BlockPos targetPos;
    public boolean isDigging = false;

    // This is the block the entity is looking at
    private final WorldLocation rayTraceBlock = new WorldLocation(0, 0, 0);

    private final Brain brain = new Brain(CraftyNPCs.LOGGER, this);

    private static final EnumProperty<ChestType> TYPE;
    private final NPCInventory equipmentInventory = new NPCInventory();
    private int durabilityRemainingOnBlock;
    private int digTicks;
    private int initialBlockDamage;
    private int initialDamage;
    private float maximumHomeDistance;
    private BlockPos homePosition;
    protected LookController lookController;


    public static InventoryFactory<INamedContainerProvider> getInventory() {
        return inventory;
    }

    private static final NPCEntity.InventoryFactory<INamedContainerProvider> inventory;

    public NPCEntity(EntityType<? extends NPCEntity> type, World worldIn) {
        super(type, worldIn);

        this.name = "miner";

        ItemStack pickaxeStack = new ItemStack(Items.DIAMOND_PICKAXE);
        this.container = new NPCInventoryContainer(GENERIC_9X3, 12);

        this.maximumHomeDistance = -1.0F;
        this.homePosition = BlockPos.ZERO;
        this.lookController = new LookController(this);




        this.equipmentInventory.setInventorySlotContents(MAIN_HAND_SLOT, pickaxeStack);
        this.npcManager = CraftyNPCs.getInstance().getNpcManager();
        npcManager.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isWearing(PlayerModelPart part) {
        return true;
    }

    public WorldLocation getLookPos() {
        return rayTraceBlock;
    }

    public PathNavigator getNavigator() {
        return navigator;
    }

    public int func_213396_dB() {
        return 10;
    }

    public int getVerticalFaceSpeed() {
        return 40;
    }

    public int getHorizontalFaceSpeed() {
        return 75;
    }

    public float getMaximumHomeDistance() {
        return this.maximumHomeDistance;
    }

    public boolean isWithinHomeDistanceFromPosition(BlockPos pos) {
        if (this.maximumHomeDistance == -1.0F) {
            return true;
        } else {
            return this.homePosition.distanceSq(pos) < (double)(this.maximumHomeDistance * this.maximumHomeDistance);
        }
    }

    public boolean detachHome() {
        return this.maximumHomeDistance != -1.0F;
    }

    public BlockPos getHomePosition() {
        return this.homePosition;
    }

    @Override
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

            for (EffectInstance effectinstance : this.getActivePotionMap().values()) {
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
    public Iterable<ItemStack> getArmorInventoryList() {
        return null;
    }

    public LookController getLookController() {
        return lookController;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        BlockPos lookingBlockPos = ((BlockRayTraceResult) this.func_213324_a(20.0D, 0.0F, false)).getPos();
        this.rayTraceBlock.update(lookingBlockPos);
        this.brain.tick();

//        collideEntities();
    }


//    private void collideEntities() {
//
//
//        AxisAlignedBB axisalignedbb;
//        axisalignedbb = this.getBoundingBox().grow(1.0D, 0.5D, 1.0D);
//
//        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb);
//
//        for (Entity aList : list) {
//            Entity entity = (Entity) aList;
//            if (entity.isAlive() && entity instanceof ItemEntity) {
//                this.itemCollideWithNPC(this, (ItemEntity) entity);
//            }
//        }
//    }

    private void itemCollideWithNPC(NPCEntity npcEntity, ItemEntity itemIn) {
//        if (!itemIn.world.isRemote) {

            ItemStack itemstack = itemIn.getItem();
            Item item = itemstack.getItem();
            int i = itemstack.getCount();

            ItemStack copy = itemstack.copy();

            NPCInventory inventory = npcEntity.getEquipmentInventory();
            int nextEmptySlot = inventory.getFirstEmptyStack();
//            if (nextEmptySlot == -1) {
//                this.sendMessage("I'm out of space!");
//                this.brain.stop();
//                return;
//            }
//            System.out.println("Next Slot: " + nextEmptySlot);
            inventory.addItemStackToInventory(itemstack);
//            inventory.addInventorySlotContents(nextEmptySlot, itemstack);
            copy.setCount(copy.getCount() - itemIn.getItem().getCount());
            if (itemstack.isEmpty()) {
                npcEntity.onItemPickup(itemIn, i);
                itemIn.remove();
                itemstack.setCount(i);
            }


        }

//    }

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
//        super.registerGoals();

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

    private boolean canHarvestBlock(BlockState state) {
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

//    public void doDigBlock(BlockPos blockPosToDig) {
//        double d0 = this.posX - ((double)blockPosToDig.getX() + 0.5D);
//        double d1 = this.posY - ((double)blockPosToDig.getY() + 0.5D) + 1.5D;
//        double d2 = this.posZ - ((double)blockPosToDig.getZ() + 0.5D);
//        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
//        double dist = this.getAttribute(PlayerEntity.REACH_DISTANCE).getValue() + 1.0D;
//            dist *= dist;
//                BlockState blockstate;
//                    this.initialDamage = this.digTicks;
//                    float f = 1.0F;
//                    blockstate = this.world.getBlockState(blockPosToDig);
//                    if (!blockstate.isAir(this.world, blockPosToDig)) {
//
//                        f = blockstate.getBlockHardness(world, blockPosToDig);
//                    }
//
//                    checkIfBlockDestroyed(this, world, blockPosToDig);
//
//                    this.isDigging = true;
//                    this.destroyPos = blockPosToDig;
//                    int i = (int)(f * 10.0F);
//                    this.durabilityRemainingOnBlock = i;
//
//    }
//
//    private void checkIfBlockDestroyed(NPCEntity digger, World world, BlockPos loc) {
//        int j = this.digTicks - this.initialDamage;
//        BlockState blockstate = this.world.getBlockState(loc);
//        if (!(blockstate.getMaterial() == Material.AIR)) {
//            float f1 = blockstate.getBlockHardness(world, loc)  * (float)(j + 1);
//
//            this.isDigging = false;
//            this.initialBlockDamage = this.initialDamage;
//        }
//    }

    public void tryHarvestBlock(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        Block block = blockstate.getBlock();
        ItemStack itemstack = this.getHeldItemMainhand();
        ItemStack copy = itemstack.copy();
        boolean canHarvestBlock = canHarvestBlock(blockstate);

        boolean wasBlockRemoved = this.removeBlock(pos, canHarvestBlock);
        if (wasBlockRemoved && canHarvestBlock) {
            ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();

            if (!world.isRemote) {
                Block.getDrops(blockstate, (ServerWorld)this.world, pos, null).forEach((p_220057_2_) -> {
                    itemCollideWithNPC(this, new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), p_220057_2_));
//                spawnAsEntity(worldIn, pos, p_220057_2_);
                });

            }

//            Block.spawnDrops(blockstate, this.world, pos, null, this, itemstack1);
        }

    }

    private boolean removeBlock(BlockPos pos, boolean canHarvest) {
        BlockState state = this.world.getBlockState(pos);
        boolean removed = world.removeBlock(pos, false);
        if (removed) {
            state.getBlock().onPlayerDestroy(this.world, pos, state);
        }

        return removed;
    }

    @OnlyIn(Dist.CLIENT)
    public void sendMessage(String msg) {
        StringTextComponent component = new StringTextComponent(msg);
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(component);
    }

    public BlockPos getTargetPos() {
        return targetPos;
    }

    public void setTargetPos(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    public NPCInventory getEquipmentInventory() {
        return equipmentInventory;
    }

    interface InventoryFactory<T> {
        T forSingle(INamedContainerProvider var1);
    }

    static {
        TYPE = BlockStateProperties.CHEST_TYPE;
        inventory = p_212856_1_ -> p_212856_1_;
    }

}
