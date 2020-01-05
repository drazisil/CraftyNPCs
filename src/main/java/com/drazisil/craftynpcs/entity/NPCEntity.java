package com.drazisil.craftynpcs.entity;

import com.drazisil.craftynpcs.CraftyNPCs;
import com.drazisil.craftynpcs.entity.ai.DefendVillageGoal;
import com.drazisil.craftynpcs.entity.ai.DiggyDiggyGoal;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.*;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("EntityConstructor")
public class NPCEntity extends CreatureEntity  {

    private NPCManager npcManager;
    private String name;
    public Integer MAIN_HAND_SLOT = 1;

    public Inventory getInventory() {
        return inventory;
    }

    private Inventory inventory = new Inventory(18);

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
        this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> {
            return false;
        }));
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
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        super.updateFallState(y, onGroundIn, state, pos);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return super.getCollisionBoundingBox();
    }

    @Override
    protected void dealFireDamage(int amount) {
        super.dealFireDamage(amount);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return super.canBreatheUnderwater();
    }

    @Override
    public float getSwimAnimation(float partialTicks) {
        return super.getSwimAnimation(partialTicks);
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        return super.createNavigator(worldIn);
    }

    @Override
    public float getPathPriority(PathNodeType nodeType) {
        return super.getPathPriority(nodeType);
    }

    @Override
    public void setPathPriority(PathNodeType nodeType, float priority) {
        super.setPathPriority(nodeType, priority);
    }

    @Override
    protected BodyController createBodyController() {
        return super.createBodyController();
    }

    @Override
    public LookController getLookController() {
        return super.getLookController();
    }

    @Override
    public MovementController getMoveHelper() {
        return super.getMoveHelper();
    }

    @Override
    public JumpController getJumpController() {
        return super.getJumpController();
    }

    @Override
    public PathNavigator getNavigator() {
        return super.getNavigator();
    }

    @Override
    public EntitySenses getEntitySenses() {
        return super.getEntitySenses();
    }

    @Nullable
    @Override
    public LivingEntity getAttackTarget() {
        return super.getAttackTarget();
    }

    @Override
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        super.setAttackTarget(entitylivingbaseIn);
    }

    @Override
    public Brain<?> getBrain() {
        return super.getBrain();
    }

    @Override
    protected Brain<?> createBrain(Dynamic<?> p_213364_1_) {
        return super.createBrain(p_213364_1_);
    }

    @Override
    public boolean isSpectator() {
        return super.isSpectator();
    }

    @Override
    public void func_213312_b(double p_213312_1_, double p_213312_3_, double p_213312_5_) {
        super.func_213312_b(p_213312_1_, p_213312_3_, p_213312_5_);
    }

    @Override
    public EntityType<?> getType() {
        return super.getType();
    }

    @Override
    public int getEntityId() {
        return super.getEntityId();
    }

    @Override
    public void setEntityId(int id) {
        super.setEntityId(id);
    }

    @Override
    public Set<String> getTags() {
        return super.getTags();
    }

    @Override
    public boolean addTag(String tag) {
        return super.addTag(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return super.removeTag(tag);
    }

    @Override
    public void onKillCommand() {
        super.onKillCommand();
    }

    @Override
    public boolean canAttack(EntityType<?> typeIn) {
        return super.canAttack(typeIn);
    }

    @Override
    public void eatGrassBonus() {
        super.eatGrassBonus();
    }

    @Override
    protected void registerData() {
        super.registerData();
    }

    @Override
    public EntityDataManager getDataManager() {
        return super.getDataManager();
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return super.equals(p_equals_1_);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected void preparePlayerToSpawn() {
        super.preparePlayerToSpawn();
    }

    @Override
    public void remove() {
        super.remove();
        npcManager.deregister(this);
    }

    @Override
    public int getTalkInterval() {
        return super.getTalkInterval();
    }

    @Override
    public void playAmbientSound() {
        super.playAmbientSound();
    }

    @Override
    public void baseTick() {
        super.baseTick();
    }

    @Override
    protected void decrementTimeUntilPortal() {
        super.decrementTimeUntilPortal();
    }

    @Override
    public int getMaxInPortalTime() {
        return super.getMaxInPortalTime();
    }

    @Override
    protected void setOnFireFromLava() {
        super.setOnFireFromLava();
    }

    @Override
    public void setFire(int seconds) {
        super.setFire(seconds);
    }

    @Override
    public void func_223308_g(int p_223308_1_) {
        super.func_223308_g(p_223308_1_);
    }

    @Override
    public int func_223314_ad() {
        return super.func_223314_ad();
    }

    @Override
    public void extinguish() {
        super.extinguish();
    }

    @Override
    protected void frostWalk(BlockPos pos) {
        super.frostWalk(pos);
    }

    @Override
    public boolean isChild() {
        return super.isChild();
    }

    @Override
    public float getRenderScale() {
        return super.getRenderScale();
    }

    @Override
    public boolean canBeRiddenInWater() {
        return super.canBeRiddenInWater();
    }

    @Override
    public boolean isSneaking() {
        return super.isSneaking();
    }

    @Override
    public boolean shouldRenderSneaking() {
        return super.shouldRenderSneaking();
    }

    @Override
    public void setSneaking(boolean sneaking) {
        super.setSneaking(sneaking);
    }

    @Override
    public boolean isSprinting() {
        return super.isSprinting();
    }

    @Override
    protected void onDeathUpdate() {
        super.onDeathUpdate();
    }

    @Override
    protected boolean canDropLoot() {
        return super.canDropLoot();
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return super.decreaseAirSupply(air);
    }

    @Override
    protected int determineNextAir(int currentAir) {
        return super.determineNextAir(currentAir);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        super.playHurtSound(source);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
    }

    @Override
    protected void spawnDrops(DamageSource p_213345_1_) {
        super.spawnDrops(p_213345_1_);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return super.getExperiencePoints(player);
    }

    @Override
    protected boolean isPlayer() {
        return super.isPlayer();
    }

    @Override
    public Random getRNG() {
        return super.getRNG();
    }

    @Nullable
    @Override
    public LivingEntity getRevengeTarget() {
        return super.getRevengeTarget();
    }

    @Override
    public int getRevengeTimer() {
        return super.getRevengeTimer();
    }

    @Override
    public void setRevengeTarget(@Nullable LivingEntity livingBase) {
        super.setRevengeTarget(livingBase);
    }

    @Nullable
    @Override
    public LivingEntity getLastAttackedEntity() {
        return super.getLastAttackedEntity();
    }

    @Override
    public int getLastAttackedEntityTime() {
        return super.getLastAttackedEntityTime();
    }

    @Override
    public void setLastAttackedEntity(Entity entityIn) {
        super.setLastAttackedEntity(entityIn);
    }

    @Override
    public int getIdleTime() {
        return super.getIdleTime();
    }

    @Override
    public void setIdleTime(int idleTimeIn) {
        super.setIdleTime(idleTimeIn);
    }

    @Override
    protected void playEquipSound(ItemStack stack) {
        super.playEquipSound(stack);
    }

    @Override
    public void spawnExplosionParticle() {
        super.spawnExplosionParticle();
    }

    @Override
    public void handleStatusUpdate(byte id) {
        super.handleStatusUpdate(id);
    }

    @Override
    protected void outOfWorld() {
        super.outOfWorld();
    }

    @Override
    public boolean isOffsetPositionInLiquid(double x, double y, double z) {
        return super.isOffsetPositionInLiquid(x, y, z);
    }

    @Override
    public void move(MoverType typeIn, Vec3d pos) {
        super.move(typeIn, pos);
    }

    @Override
    protected Vec3d handleSneakMovement(Vec3d pos, MoverType typeIn) {
        return super.handleSneakMovement(pos, typeIn);
    }

    @Override
    protected Vec3d handlePistonMovement(Vec3d pos) {
        return super.handlePistonMovement(pos);
    }

    @Override
    protected float determineNextStepDistance() {
        return super.determineNextStepDistance();
    }

    @Override
    public void resetPositionToBB() {
        super.resetPositionToBB();
    }

    @Override
    protected SoundEvent getSwimSound() {
        return super.getSwimSound();
    }

    @Override
    protected SoundEvent getSplashSound() {
        return super.getSplashSound();
    }

    @Override
    protected SoundEvent getHighspeedSplashSound() {
        return super.getHighspeedSplashSound();
    }

    @Override
    protected void doBlockCollisions() {
        super.doBlockCollisions();
    }

    @Override
    protected void onInsideBlock(BlockState p_191955_1_) {
        super.onInsideBlock(p_191955_1_);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        super.playStepSound(pos, blockIn);
    }

    @Override
    protected void playSwimSound(float volume) {
        super.playSwimSound(volume);
    }

    @Override
    protected float playFlySound(float volume) {
        return super.playFlySound(volume);
    }

    @Override
    protected boolean makeFlySound() {
        return super.makeFlySound();
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        super.playSound(soundIn, volume, pitch);
    }

    @Override
    public boolean isSilent() {
        return super.isSilent();
    }

    @Override
    public void setSilent(boolean isSilent) {
        super.setSilent(isSilent);
    }

    @Override
    public boolean hasNoGravity() {
        return super.hasNoGravity();
    }

    @Override
    public void setNoGravity(boolean noGravity) {
        super.setNoGravity(noGravity);
    }

    @Override
    protected boolean canTriggerWalking() {
        return super.canTriggerWalking();
    }

    @Override
    protected void updateArmSwingProgress() {
        super.updateArmSwingProgress();
    }

    @Override
    public IAttributeInstance getAttribute(IAttribute attribute) {
        return super.getAttribute(attribute);
    }

    @Override
    public AbstractAttributeMap getAttributes() {
        return super.getAttributes();
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return super.getCreatureAttribute();
    }

    @Override
    public ItemStack getHeldItemMainhand() {
        return super.getHeldItemMainhand();
    }

    @Override
    public ItemStack getHeldItemOffhand() {
        return super.getHeldItemOffhand();
    }

    @Override
    public ItemStack getHeldItem(Hand hand) {
        return super.getHeldItem(hand);
    }

    @Override
    public void setHeldItem(Hand hand, ItemStack stack) {
        super.setHeldItem(hand, stack);
    }

    @Override
    public boolean hasItemInSlot(EquipmentSlotType p_190630_1_) {
        return super.hasItemInSlot(p_190630_1_);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void func_213385_F() {
        super.func_213385_F();
    }

    @Override
    protected float updateDistance(float p_110146_1_, float p_110146_2_) {
        return super.updateDistance(p_110146_1_, p_110146_2_);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
    }

    @Override
    protected ListNBT newDoubleNBTList(double... numbers) {
        return super.newDoubleNBTList(numbers);
    }

    @Override
    protected ListNBT newFloatNBTList(float... numbers) {
        return super.newFloatNBTList(numbers);
    }

    @Nullable
    @Override
    public ItemEntity entityDropItem(IItemProvider p_199703_1_) {
        return super.entityDropItem(p_199703_1_);
    }

    @Nullable
    @Override
    public ItemEntity entityDropItem(IItemProvider p_199702_1_, int offset) {
        return super.entityDropItem(p_199702_1_, offset);
    }

    @Nullable
    @Override
    public ItemEntity entityDropItem(ItemStack p_199701_1_) {
        return super.entityDropItem(p_199701_1_);
    }

    @Nullable
    @Override
    public ItemEntity entityDropItem(ItemStack stack, float offsetY) {
        return super.entityDropItem(stack, offsetY);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
    }

    @Override
    protected void updatePotionEffects() {
        super.updatePotionEffects();
    }

    @Override
    protected void updatePotionMetadata() {
        super.updatePotionMetadata();
    }

    @Override
    public double getVisibilityMultiplier(@Nullable Entity lookingEntity) {
        return super.getVisibilityMultiplier(lookingEntity);
    }

    @Override
    protected void dropLoot(DamageSource p_213354_1_, boolean p_213354_2_) {
        super.dropLoot(p_213354_1_, p_213354_2_);
    }

    @Override
    protected LootContext.Builder func_213363_a(boolean p_213363_1_, DamageSource p_213363_2_) {
        return super.func_213363_a(p_213363_1_, p_213363_2_);
    }

    @Override
    public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
        super.knockBack(entityIn, strength, xRatio, zRatio);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return super.getHurtSound(damageSourceIn);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    protected SoundEvent getFallSound(int heightIn) {
        return super.getFallSound(heightIn);
    }

    @Override
    protected SoundEvent getDrinkSound(ItemStack p_213351_1_) {
        return super.getDrinkSound(p_213351_1_);
    }

    @Override
    public SoundEvent getEatSound(ItemStack itemStackIn) {
        return super.getEatSound(itemStackIn);
    }

    @Override
    public boolean isOnLadder() {
        return super.isOnLadder();
    }

    @Override
    public BlockState getBlockState() {
        return super.getBlockState();
    }

    @Override
    public boolean isAlive() {
        return super.isAlive();
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, damageMultiplier);
    }

    @Override
    public boolean isInWater() {
        return super.isInWater();
    }

    @Override
    public boolean isWet() {
        return super.isWet();
    }

    @Override
    public boolean isInWaterRainOrBubbleColumn() {
        return super.isInWaterRainOrBubbleColumn();
    }

    @Override
    public boolean isInWaterOrBubbleColumn() {
        return super.isInWaterOrBubbleColumn();
    }

    @Override
    public boolean canSwim() {
        return super.canSwim();
    }

    @Override
    public void updateSwimming() {
        super.updateSwimming();
    }

    @Override
    public boolean handleWaterMovement() {
        return super.handleWaterMovement();
    }

    @Override
    protected void doWaterSplashEffect() {
        super.doWaterSplashEffect();
    }

    @Override
    public void spawnRunningParticles() {
        super.spawnRunningParticles();
    }

    @Override
    protected void createRunningParticles() {
        super.createRunningParticles();
    }

    @Override
    public boolean areEyesInFluid(Tag<Fluid> tagIn) {
        return super.areEyesInFluid(tagIn);
    }

    @Override
    public boolean areEyesInFluid(Tag<Fluid> p_213290_1_, boolean checkChunkLoaded) {
        return super.areEyesInFluid(p_213290_1_, checkChunkLoaded);
    }

    @Override
    public void setInLava() {
        super.setInLava();
    }

    @Override
    public boolean isInLava() {
        return super.isInLava();
    }

    @Override
    public void moveRelative(float p_213309_1_, Vec3d relative) {
        super.moveRelative(p_213309_1_, relative);
    }

    @Override
    public int getBrightnessForRender() {
        return super.getBrightnessForRender();
    }

    @Override
    public float getBrightness() {
        return super.getBrightness();
    }

    @Override
    public void setWorld(World worldIn) {
        super.setWorld(worldIn);
    }

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        super.setPositionAndRotation(x, y, z, yaw, pitch);
    }

    @Override
    public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn) {
        super.moveToBlockPosAndAngles(pos, rotationYawIn, rotationPitchIn);
    }

    @Override
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
        super.setLocationAndAngles(x, y, z, yaw, pitch);
    }

    @Override
    public float getDistance(Entity entityIn) {
        return super.getDistance(entityIn);
    }

    @Override
    public double getDistanceSq(double x, double y, double z) {
        return super.getDistanceSq(x, y, z);
    }

    @Override
    public double getDistanceSq(Entity entityIn) {
        return super.getDistanceSq(entityIn);
    }

    @Override
    public double getDistanceSq(Vec3d p_195048_1_) {
        return super.getDistanceSq(p_195048_1_);
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        super.onCollideWithPlayer(entityIn);
    }

    @Override
    public void performHurtAnimation() {
        super.performHurtAnimation();
    }

    @Override
    public int getTotalArmorValue() {
        return super.getTotalArmorValue();
    }

    @Override
    protected void damageArmor(float damage) {
        super.damageArmor(damage);
    }

    @Override
    protected void damageShield(float damage) {
        super.damageShield(damage);
    }

    @Override
    protected float applyArmorCalculations(DamageSource source, float damage) {
        return super.applyArmorCalculations(source, damage);
    }

    @Override
    protected float applyPotionDamageCalculations(DamageSource source, float damage) {
        return super.applyPotionDamageCalculations(source, damage);
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        super.damageEntity(damageSrc, damageAmount);
    }

    @Override
    public CombatTracker getCombatTracker() {
        return super.getCombatTracker();
    }

    @Nullable
    @Override
    public LivingEntity getAttackingEntity() {
        return super.getAttackingEntity();
    }

    @Override
    public void swingArm(Hand hand) {
        super.swingArm(hand);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setMoveForward(float amount) {
        super.setMoveForward(amount);
    }

    @Override
    public void setMoveVertical(float amount) {
        super.setMoveVertical(amount);
    }

    @Override
    public void setMoveStrafing(float amount) {
        super.setMoveStrafing(amount);
    }

    @Override
    public void setAIMoveSpeed(float speedIn) {
        super.setAIMoveSpeed(speedIn);
    }

    @Override
    public void livingTick() {
        super.livingTick();
    }

    @Override
    protected void collideWithNearbyEntities() {
        super.collideWithNearbyEntities();
    }

    @Override
    protected void updateSpinAttack(AxisAlignedBB p_204801_1_, AxisAlignedBB p_204801_2_) {
        super.updateSpinAttack(p_204801_1_, p_204801_2_);
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        super.collideWithEntity(entityIn);
    }

    @Override
    protected void spinAttack(LivingEntity p_204804_1_) {
        super.spinAttack(p_204804_1_);
    }

    @Override
    public void startSpinAttack(int p_204803_1_) {
        super.startSpinAttack(p_204803_1_);
    }

    @Override
    public boolean isSpinAttacking() {
        return super.isSpinAttacking();
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return super.canFitPassenger(passenger);
    }

    @Override
    public void updateRidden() {
        super.updateRidden();
    }

    @Override
    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
    }

    @Override
    public void applyOrientationToEntity(Entity entityToUpdate) {
        super.applyOrientationToEntity(entityToUpdate);
    }

    @Override
    public double getYOffset() {
        return super.getYOffset();
    }

    @Override
    public double getMountedYOffset() {
        return super.getMountedYOffset();
    }

    @Override
    public boolean startRiding(Entity entityIn) {
        return super.startRiding(entityIn);
    }

    @Override
    public boolean isLiving() {
        return super.isLiving();
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        super.setPositionAndRotationDirect(x, y, z, yaw, pitch, posRotationIncrements, teleport);
    }

    @Override
    public void setHeadRotation(float yaw, int pitch) {
        super.setHeadRotation(yaw, pitch);
    }

    @Override
    public float getCollisionBorderSize() {
        return super.getCollisionBorderSize();
    }

    @Override
    public Vec3d getLookVec() {
        return super.getLookVec();
    }

    @Override
    public Vec2f getPitchYaw() {
        return super.getPitchYaw();
    }

    @Override
    public Vec3d getForward() {
        return super.getForward();
    }

    @Override
    public void setPortal(BlockPos pos) {
        super.setPortal(pos);
    }

    @Override
    protected void updatePortal() {
        super.updatePortal();
    }

    @Override
    public int getPortalCooldown() {
        return super.getPortalCooldown();
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        super.setVelocity(x, y, z);
    }

    @Override
    public void setJumping(boolean jumping) {
        super.setJumping(jumping);
    }

    @Override
    public void onItemPickup(Entity entityIn, int quantity) {
        super.onItemPickup(entityIn, quantity);
    }

    @Override
    public boolean canEntityBeSeen(Entity entityIn) {
        return super.canEntityBeSeen(entityIn);
    }

    @Override
    public float getYaw(float partialTicks) {
        return super.getYaw(partialTicks);
    }

    @Override
    public Vec3d getEyePosition(float partialTicks) {
        return super.getEyePosition(partialTicks);
    }

    @Override
    public RayTraceResult func_213324_a(double p_213324_1_, float p_213324_3_, boolean p_213324_4_) {
        return super.func_213324_a(p_213324_1_, p_213324_3_, p_213324_4_);
    }

    @Override
    public float getSwingProgress(float partialTickTime) {
        return super.getSwingProgress(partialTickTime);
    }

    @Override
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        super.updateEquipmentIfNeeded(itemEntity);
    }

    @Override
    protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing, EquipmentSlotType p_208003_3_) {
        return super.shouldExchangeEquipment(candidate, existing, p_208003_3_);
    }

    @Override
    protected boolean canEquipItem(ItemStack stack) {
        return super.canEquipItem(stack);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return super.canDespawn(distanceToClosestPlayer);
    }

    @Override
    public boolean preventDespawn() {
        return super.preventDespawn();
    }

    @Override
    protected void checkDespawn() {
        super.checkDespawn();
    }

    @Override
    protected void func_213387_K() {
        super.func_213387_K();
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
    }

    @Override
    public int getVerticalFaceSpeed() {
        return super.getVerticalFaceSpeed();
    }

    @Override
    public int getHorizontalFaceSpeed() {
        return super.getHorizontalFaceSpeed();
    }

    @Override
    public int func_213396_dB() {
        return super.func_213396_dB();
    }

    @Override
    public void faceEntity(Entity entityIn, float maxYawIncrease, float maxPitchIncrease) {
        super.faceEntity(entityIn, maxYawIncrease, maxPitchIncrease);
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return super.canSpawn(worldIn, spawnReasonIn);
    }

    @Override
    public boolean isNotColliding(IWorldReader worldIn) {
        return super.isNotColliding(worldIn);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return super.getMaxSpawnedInChunk();
    }

    @Override
    public boolean func_204209_c(int p_204209_1_) {
        return super.func_204209_c(p_204209_1_);
    }

    @Override
    public int getMaxFallHeight() {
        return super.getMaxFallHeight();
    }

    @Override
    public Vec3d getLastPortalVec() {
        return super.getLastPortalVec();
    }

    @Override
    public Direction getTeleportDirection() {
        return super.getTeleportDirection();
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return super.doesEntityNotTriggerPressurePlate();
    }

    @Override
    public void fillCrashReport(CrashReportCategory category) {
        super.fillCrashReport(category);
    }

    @Override
    public boolean canRenderOnFire() {
        return super.canRenderOnFire();
    }

    @Override
    public void setUniqueId(UUID uniqueIdIn) {
        super.setUniqueId(uniqueIdIn);
    }

    @Override
    public UUID getUniqueID() {
        return super.getUniqueID();
    }

    @Override
    public String getCachedUniqueIdString() {
        return super.getCachedUniqueIdString();
    }

    @Override
    public String getScoreboardName() {
        return super.getScoreboardName();
    }

    @Override
    public boolean isPushedByWater() {
        return super.isPushedByWater();
    }

    @Override
    public ITextComponent getDisplayName() {
        return super.getDisplayName();
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return super.getCustomName();
    }

    @Override
    public boolean hasCustomName() {
        return super.hasCustomName();
    }

    @Override
    public void setCustomNameVisible(boolean alwaysRenderNameTag) {
        super.setCustomNameVisible(alwaysRenderNameTag);
    }

    @Override
    public boolean isCustomNameVisible() {
        return super.isCustomNameVisible();
    }

    @Override
    public void setPositionAndUpdate(double x, double y, double z) {
        super.setPositionAndUpdate(x, y, z);
    }

    @Override
    public Iterable<ItemStack> getHeldEquipment() {
        return super.getHeldEquipment();
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return super.getArmorInventoryList();
    }

    @Override
    public Iterable<ItemStack> getEquipmentAndArmor() {
        return super.getEquipmentAndArmor();
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return super.getItemStackFromSlot(slotIn);
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
        super.setItemStackToSlot(slotIn, stack);
    }

    @Override
    public boolean isBurning() {
        return super.isBurning();
    }

    @Override
    public boolean isPassenger() {
        return super.isPassenger();
    }

    @Override
    public boolean isBeingRidden() {
        return super.isBeingRidden();
    }

    @Override
    public float func_213343_cS() {
        return super.func_213343_cS();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
    }

    @Override
    public boolean isSwimming() {
        return super.isSwimming();
    }

    @Override
    protected float getSoundVolume() {
        return super.getSoundVolume();
    }

    @Override
    protected float getSoundPitch() {
        return super.getSoundPitch();
    }

    @Override
    protected boolean isMovementBlocked() {
        return super.isMovementBlocked();
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        super.applyEntityCollision(entityIn);
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        super.addVelocity(x, y, z);
    }

    @Override
    public void dismountEntity(Entity entityIn) {
        super.dismountEntity(entityIn);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return super.getAlwaysRenderNameTagForRender();
    }

    @Override
    protected float getJumpUpwardsMotion() {
        return super.getJumpUpwardsMotion();
    }

    @Override
    protected void jump() {
        super.jump();
    }

    @Override
    protected void handleFluidSneak() {
        super.handleFluidSneak();
    }

    @Override
    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropSpecialItems(source, looting, recentlyHitIn);
    }

    @Override
    protected float getDropChance(EquipmentSlotType slotIn) {
        return super.getDropChance(slotIn);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);
    }

    @Override
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEnchantmentBasedOnDifficulty(difficulty);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public boolean canBeSteered() {
        return super.canBeSteered();
    }

    @Override
    public void enablePersistence() {
        super.enablePersistence();
    }

    @Override
    public void setDropChance(EquipmentSlotType slotIn, float chance) {
        super.setDropChance(slotIn, chance);
    }

    @Override
    public boolean canPickUpLoot() {
        return super.canPickUpLoot();
    }

    @Override
    public void setCanPickUpLoot(boolean canPickup) {
        super.setCanPickUpLoot(canPickup);
    }

    @Override
    public boolean func_213365_e(ItemStack p_213365_1_) {
        return super.func_213365_e(p_213365_1_);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return super.createSpawnPacket();
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return super.getSize(poseIn);
    }

    @Override
    public Vec3d getPositionVec() {
        return super.getPositionVec();
    }

    @Override
    public Vec3d getMotion() {
        return super.getMotion();
    }

    @Override
    public void setMotion(Vec3d p_213317_1_) {
        super.setMotion(p_213317_1_);
    }

    @Override
    public void setMotion(double p_213293_1_, double p_213293_3_, double p_213293_5_) {
        super.setMotion(p_213293_1_, p_213293_3_, p_213293_5_);
    }

    @Override
    public void canUpdate(boolean value) {
        super.canUpdate(value);
    }

    @Override
    public Entity getEntity() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }

    @Override
    public CompoundNBT serializeNBT() {
        return null;
    }

    @Override
    public boolean canUpdate() {
        return super.canUpdate();
    }

    @Override
    public Collection<ItemEntity> captureDrops() {
        return super.captureDrops();
    }

    @Override
    public Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
        return super.captureDrops(value);
    }

    @Override
    public CompoundNBT getPersistentData() {
        return super.getPersistentData();
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return null;
    }

    @Override
    public boolean canRiderInteract() {
        return false;
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider) {
        return false;
    }

    @Override
    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return super.canTrample(state, pos, fallDistance);
    }

    @Override
    public EntityClassification getClassification(boolean forSpawnCount) {
        return null;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
    }

    @Override
    public void revive() {
        super.revive();
    }

    @Override
    public Optional<BlockPos> getBedPosition() {
        return super.getBedPosition();
    }

    @Override
    public void setBedPosition(BlockPos p_213369_1_) {
        super.setBedPosition(p_213369_1_);
    }

    @Override
    public void clearBedPosition() {
        super.clearBedPosition();
    }

    @Override
    public boolean isSleeping() {
        return super.isSleeping();
    }

    @Override
    public void startSleeping(BlockPos p_213342_1_) {
        super.startSleeping(p_213342_1_);
    }

    @Override
    public void wakeUp() {
        super.wakeUp();
    }

    @Nullable
    @Override
    public Direction getBedDirection() {
        return super.getBedDirection();
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return super.isEntityInsideOpaqueBlock();
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return super.getCollisionBox(entityIn);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return super.getStandingEyeHeight(poseIn, sizeIn);
    }

    @Override
    public ItemStack findAmmo(ItemStack shootable) {
        return super.findAmmo(shootable);
    }

    @Override
    public ItemStack onFoodEaten(World p_213357_1_, ItemStack p_213357_2_) {
        return super.onFoodEaten(p_213357_1_, p_213357_2_);
    }

    @Override
    public void sendBreakAnimation(EquipmentSlotType p_213361_1_) {
        super.sendBreakAnimation(p_213361_1_);
    }

    @Override
    public void sendBreakAnimation(Hand p_213334_1_) {
        super.sendBreakAnimation(p_213334_1_);
    }

    @Override
    public boolean curePotionEffects(ItemStack curativeItem) {
        return super.curePotionEffects(curativeItem);
    }

    @Override
    public boolean shouldRiderFaceForward(PlayerEntity player) {
        return super.shouldRiderFaceForward(player);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
    }

    @Override
    protected void reviveCaps() {
        super.reviveCaps();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        return super.getCapability(capability, facing);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return null;
    }

    @Override
    public void remove(boolean keepData) {
        super.remove(keepData);
    }

    @Override
    protected void setPose(Pose p_213301_1_) {
        super.setPose(p_213301_1_);
    }

    @Override
    public Pose getPose() {
        return super.getPose();
    }

    @Override
    protected void setRotation(float yaw, float pitch) {
        super.setRotation(yaw, pitch);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
    }

    @Override
    public void rotateTowards(double yaw, double pitch) {
        super.rotateTowards(yaw, pitch);
    }

    @Override
    public boolean isNoDespawnRequired() {
        return super.isNoDespawnRequired();
    }

    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        return super.processInteract(player, hand);
    }

    @Override
    public boolean isWithinHomeDistanceCurrentPosition() {
        return super.isWithinHomeDistanceCurrentPosition();
    }

    @Override
    public boolean isWithinHomeDistanceFromPosition(BlockPos pos) {
        return super.isWithinHomeDistanceFromPosition(pos);
    }

    @Override
    public void setHomePosAndDistance(BlockPos pos, int distance) {
        super.setHomePosAndDistance(pos, distance);
    }

    @Override
    public BlockPos getHomePosition() {
        return super.getHomePosition();
    }

    @Override
    public float getMaximumHomeDistance() {
        return super.getMaximumHomeDistance();
    }

    @Override
    public boolean detachHome() {
        return super.detachHome();
    }

    @Override
    protected void updateLeashedState() {
        super.updateLeashedState();
    }

    @Override
    public void clearLeashed(boolean sendPacket, boolean dropLead) {
        super.clearLeashed(sendPacket, dropLead);
    }

    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
        return super.canBeLeashedTo(player);
    }

    @Override
    public boolean getLeashed() {
        return super.getLeashed();
    }

    @Nullable
    @Override
    public Entity getLeashHolder() {
        return super.getLeashHolder();
    }

    @Override
    public void setLeashHolder(Entity entityIn, boolean sendAttachNotification) {
        super.setLeashHolder(entityIn, sendAttachNotification);
    }

    @Override
    public void func_213381_d(int leashHolderIDIn) {
        super.func_213381_d(leashHolderIDIn);
    }

    @Override
    public boolean startRiding(Entity entityIn, boolean force) {
        return super.startRiding(entityIn, force);
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        return super.canBeRidden(entityIn);
    }

    @Override
    protected boolean isPoseClear(Pose p_213298_1_) {
        return super.isPoseClear(p_213298_1_);
    }

    @Override
    public void removePassengers() {
        super.removePassengers();
    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        return super.replaceItemInInventory(inventorySlot, itemStackIn);
    }

    @Override
    public void sendMessage(ITextComponent component) {
        super.sendMessage(component);
    }

    @Override
    public BlockPos getPosition() {
        return super.getPosition();
    }

    @Override
    public Vec3d getPositionVector() {
        return super.getPositionVector();
    }

    @Override
    public World getEntityWorld() {
        return super.getEntityWorld();
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return super.getServer();
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        return super.applyPlayerInteraction(player, vec, hand);
    }

    @Override
    public boolean isImmuneToExplosions() {
        return super.isImmuneToExplosions();
    }

    @Override
    protected void applyEnchantments(LivingEntity entityLivingBaseIn, Entity entityIn) {
        super.applyEnchantments(entityLivingBaseIn, entityIn);
    }

    @Override
    public void addTrackingPlayer(ServerPlayerEntity player) {
        super.addTrackingPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(ServerPlayerEntity player) {
        super.removeTrackingPlayer(player);
    }

    @Override
    public float getRotatedYaw(Rotation transformRotation) {
        return super.getRotatedYaw(transformRotation);
    }

    @Override
    public float getMirroredYaw(Mirror transformMirror) {
        return super.getMirroredYaw(transformMirror);
    }

    @Override
    public boolean ignoreItemEntityData() {
        return super.ignoreItemEntityData();
    }

    @Override
    public boolean setPositionNonDirty() {
        return super.setPositionNonDirty();
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return super.getControllingPassenger();
    }

    @Override
    public List<Entity> getPassengers() {
        return super.getPassengers();
    }

    @Override
    public boolean isPassenger(Entity entityIn) {
        return super.isPassenger(entityIn);
    }

    @Override
    public boolean isPassenger(Class<? extends Entity> p_205708_1_) {
        return super.isPassenger(p_205708_1_);
    }

    @Override
    public Collection<Entity> getRecursivePassengers() {
        return super.getRecursivePassengers();
    }

    @Override
    public boolean isOnePlayerRiding() {
        return super.isOnePlayerRiding();
    }

    @Override
    public Entity getLowestRidingEntity() {
        return super.getLowestRidingEntity();
    }

    @Override
    public boolean isRidingSameEntity(Entity entityIn) {
        return super.isRidingSameEntity(entityIn);
    }

    @Override
    public boolean isRidingOrBeingRiddenBy(Entity entityIn) {
        return super.isRidingOrBeingRiddenBy(entityIn);
    }

    @Override
    public boolean canPassengerSteer() {
        return super.canPassengerSteer();
    }

    @Nullable
    @Override
    public Entity getRidingEntity() {
        return super.getRidingEntity();
    }

    @Override
    public PushReaction getPushReaction() {
        return super.getPushReaction();
    }

    @Override
    public SoundCategory getSoundCategory() {
        return super.getSoundCategory();
    }

    @Override
    protected int getFireImmuneTicks() {
        return super.getFireImmuneTicks();
    }

    @Override
    public CommandSource getCommandSource() {
        return super.getCommandSource();
    }

    @Override
    protected int getPermissionLevel() {
        return super.getPermissionLevel();
    }

    @Override
    public boolean hasPermissionLevel(int p_211513_1_) {
        return super.hasPermissionLevel(p_211513_1_);
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return super.shouldReceiveFeedback();
    }

    @Override
    public boolean shouldReceiveErrors() {
        return super.shouldReceiveErrors();
    }

    @Override
    public boolean allowLogging() {
        return super.allowLogging();
    }

    @Override
    public boolean isServerWorld() {
        return super.isServerWorld();
    }

    @Override
    public boolean canBeCollidedWith() {
        return super.canBeCollidedWith();
    }

    @Override
    public boolean canBePushed() {
        return super.canBePushed();
    }

    @Override
    public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
        super.awardKillScore(p_191956_1_, p_191956_2_, p_191956_3_);
    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return super.isInRangeToRender3d(x, y, z);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return super.isInRangeToRenderDist(distance);
    }

    @Override
    public boolean writeUnlessRemoved(CompoundNBT compound) {
        return super.writeUnlessRemoved(compound);
    }

    @Override
    public boolean writeUnlessPassenger(CompoundNBT compound) {
        return super.writeUnlessPassenger(compound);
    }

    @Override
    public CompoundNBT writeWithoutTypeId(CompoundNBT compound) {
        return super.writeWithoutTypeId(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
    }

    @Override
    protected boolean shouldSetPosAfterLoading() {
        return super.shouldSetPosAfterLoading();
    }

    @Override
    protected void markVelocityChanged() {
        super.markVelocityChanged();
    }

    @Override
    public float getRotationYawHead() {
        return super.getRotationYawHead();
    }

    @Override
    public void setRotationYawHead(float rotation) {
        super.setRotationYawHead(rotation);
    }

    @Override
    public void setRenderYawOffset(float offset) {
        super.setRenderYawOffset(offset);
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return super.canBeAttackedWithItem();
    }

    @Override
    public boolean hitByEntity(Entity entityIn) {
        return super.hitByEntity(entityIn);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean isInvulnerable() {
        return super.isInvulnerable();
    }

    @Override
    public void setInvulnerable(boolean isInvulnerable) {
        super.setInvulnerable(isInvulnerable);
    }

    @Override
    public void copyLocationAndAnglesFrom(Entity entityIn) {
        super.copyLocationAndAnglesFrom(entityIn);
    }

    @Override
    public void copyDataFromOld(Entity entityIn) {
        super.copyDataFromOld(entityIn);
    }

    @Nullable
    @Override
    public Entity changeDimension(DimensionType destination) {
        return super.changeDimension(destination);
    }

    @Override
    public boolean isNonBoss() {
        return super.isNonBoss();
    }

    @Override
    public float getExplosionResistance(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, IFluidState p_180428_5_, float p_180428_6_) {
        return super.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn, p_180428_5_, p_180428_6_);
    }

    @Override
    public boolean canExplosionDestroyBlock(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, float p_174816_5_) {
        return super.canExplosionDestroyBlock(explosionIn, worldIn, pos, blockStateIn, p_174816_5_);
    }

    @Override
    public float getAbsorptionAmount() {
        return super.getAbsorptionAmount();
    }

    @Override
    public void setAbsorptionAmount(float amount) {
        super.setAbsorptionAmount(amount);
    }

    @Override
    public void sendEnterCombat() {
        super.sendEnterCombat();
    }

    @Override
    public void sendEndCombat() {
        super.sendEndCombat();
    }

    @Override
    protected void markPotionsDirty() {
        super.markPotionsDirty();
    }

    @Override
    public void setNoAI(boolean disable) {
        super.setNoAI(disable);
    }

    @Override
    public void setLeftHanded(boolean leftHanded) {
        super.setLeftHanded(leftHanded);
    }

    @Override
    public void setAggroed(boolean p_213395_1_) {
        super.setAggroed(p_213395_1_);
    }

    @Override
    public boolean isAIDisabled() {
        return super.isAIDisabled();
    }

    @Override
    public boolean isLeftHanded() {
        return super.isLeftHanded();
    }

    @Override
    public boolean isAggressive() {
        return super.isAggressive();
    }

    @Override
    public HandSide getPrimaryHand() {
        return super.getPrimaryHand();
    }

    @Override
    public boolean isHandActive() {
        return super.isHandActive();
    }

    @Override
    public Hand getActiveHand() {
        return super.getActiveHand();
    }

    @Override
    protected void setLivingFlag(int key, boolean value) {
        super.setLivingFlag(key, value);
    }

    @Override
    public void setActiveHand(Hand hand) {
        super.setActiveHand(hand);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
    }

    @Override
    public void recalculateSize() {
        super.recalculateSize();
    }

    @Override
    public Direction getHorizontalFacing() {
        return super.getHorizontalFacing();
    }

    @Override
    public Direction getAdjustedHorizontalFacing() {
        return super.getAdjustedHorizontalFacing();
    }

    @Override
    protected HoverEvent getHoverEvent() {
        return super.getHoverEvent();
    }

    @Override
    public boolean isSpectatedByPlayer(ServerPlayerEntity player) {
        return super.isSpectatedByPlayer(player);
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return super.getBoundingBox();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox();
    }

    @Override
    protected AxisAlignedBB getBoundingBox(Pose p_213321_1_) {
        return super.getBoundingBox(p_213321_1_);
    }

    @Override
    public void setBoundingBox(AxisAlignedBB bb) {
        super.setBoundingBox(bb);
    }

    @Override
    public float getEyeHeight(Pose p_213307_1_) {
        return super.getEyeHeight(p_213307_1_);
    }

    @Override
    public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
        super.lookAt(p_200602_1_, p_200602_2_);
    }

    @Override
    public boolean handleFluidAcceleration(Tag<Fluid> p_210500_1_) {
        return super.handleFluidAcceleration(p_210500_1_);
    }

    @Override
    public double getSubmergedHeight() {
        return super.getSubmergedHeight();
    }

    @Override
    protected void updateItemUse(ItemStack stack, int eatingParticleCount) {
        super.updateItemUse(stack, eatingParticleCount);
    }

    @Override
    protected void onItemUseFinish() {
        super.onItemUseFinish();
    }

    @Override
    public ItemStack getActiveItemStack() {
        return super.getActiveItemStack();
    }

    @Override
    public int getItemInUseCount() {
        return super.getItemInUseCount();
    }

    @Override
    public int getItemInUseMaxCount() {
        return super.getItemInUseMaxCount();
    }

    @Override
    public void stopActiveHand() {
        super.stopActiveHand();
    }

    @Override
    public void resetActiveHand() {
        super.resetActiveHand();
    }

    @Override
    public boolean isActiveItemStackBlocking() {
        return super.isActiveItemStackBlocking();
    }

    @Override
    public boolean isElytraFlying() {
        return super.isElytraFlying();
    }

    @Override
    public boolean func_213314_bj() {
        return super.func_213314_bj();
    }

    @Override
    public boolean func_213300_bk() {
        return super.func_213300_bk();
    }

    @Override
    public void setSwimming(boolean p_204711_1_) {
        super.setSwimming(p_204711_1_);
    }

    @Override
    public boolean isGlowing() {
        return super.isGlowing();
    }

    @Override
    public void setGlowing(boolean glowingIn) {
        super.setGlowing(glowingIn);
    }

    @Override
    public boolean isInvisible() {
        return super.isInvisible();
    }

    @Override
    public boolean isInvisibleToPlayer(PlayerEntity player) {
        return super.isInvisibleToPlayer(player);
    }

    @Nullable
    @Override
    public Team getTeam() {
        return super.getTeam();
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        return super.isOnSameTeam(entityIn);
    }

    @Override
    public boolean isOnScoreboardTeam(Team teamIn) {
        return super.isOnScoreboardTeam(teamIn);
    }

    @Override
    public void setInvisible(boolean invisible) {
        super.setInvisible(invisible);
    }

    @Override
    protected boolean getFlag(int flag) {
        return super.getFlag(flag);
    }

    @Override
    protected void setFlag(int flag, boolean set) {
        super.setFlag(flag, set);
    }

    @Override
    public int getMaxAir() {
        return super.getMaxAir();
    }

    @Override
    public int getAir() {
        return super.getAir();
    }

    @Override
    public void setAir(int air) {
        super.setAir(air);
    }

    @Override
    public void onStruckByLightning(LightningBoltEntity lightningBolt) {
        super.onStruckByLightning(lightningBolt);
    }

    @Override
    public void onEnterBubbleColumnWithAirAbove(boolean downwards) {
        super.onEnterBubbleColumnWithAirAbove(downwards);
    }

    @Override
    public void onEnterBubbleColumn(boolean downwards) {
        super.onEnterBubbleColumn(downwards);
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
    }

    @Override
    protected void pushOutOfBlocks(double x, double y, double z) {
        super.pushOutOfBlocks(x, y, z);
    }

    @Override
    public void setMotionMultiplier(BlockState p_213295_1_, Vec3d p_213295_2_) {
        super.setMotionMultiplier(p_213295_1_, p_213295_2_);
    }

    @Override
    public ITextComponent getName() {
        return new StringTextComponent(this.name);
    }

    @Override
    public boolean isEntityEqual(Entity entityIn) {
        return super.isEntityEqual(entityIn);
    }

    @Override
    public int getTicksElytraFlying() {
        return super.getTicksElytraFlying();
    }

    @Override
    public boolean attemptTeleport(double p_213373_1_, double p_213373_3_, double p_213373_5_, boolean p_213373_7_) {
        return super.attemptTeleport(p_213373_1_, p_213373_3_, p_213373_5_, p_213373_7_);
    }

    @Override
    public boolean canBeHitWithPotion() {
        return super.canBeHitWithPotion();
    }

    @Override
    public boolean attackable() {
        return super.attackable();
    }

    @Override
    public void setPartying(BlockPos pos, boolean isPartying) {
        super.setPartying(pos, isPartying);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target);
    }

    @Override
    public boolean func_213344_a(LivingEntity p_213344_1_, EntityPredicate p_213344_2_) {
        return super.func_213344_a(p_213344_1_, p_213344_2_);
    }

    @Override
    protected void resetPotionEffectMetadata() {
        super.resetPotionEffectMetadata();
    }

    @Override
    public boolean clearActivePotions() {
        return super.clearActivePotions();
    }

    @Override
    public Collection<EffectInstance> getActivePotionEffects() {
        return super.getActivePotionEffects();
    }

    @Override
    public Map<Effect, EffectInstance> getActivePotionMap() {
        return super.getActivePotionMap();
    }

    @Override
    public boolean isPotionActive(Effect potionIn) {
        return super.isPotionActive(potionIn);
    }

    @Nullable
    @Override
    public EffectInstance getActivePotionEffect(Effect potionIn) {
        return super.getActivePotionEffect(potionIn);
    }

    @Override
    public boolean addPotionEffect(EffectInstance p_195064_1_) {
        return super.addPotionEffect(p_195064_1_);
    }

    @Override
    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public boolean isEntityUndead() {
        return super.isEntityUndead();
    }

    @Nullable
    @Override
    public EffectInstance removeActivePotionEffect(@Nullable Effect potioneffectin) {
        return super.removeActivePotionEffect(potioneffectin);
    }

    @Override
    public boolean removePotionEffect(Effect p_195063_1_) {
        return super.removePotionEffect(p_195063_1_);
    }

    @Override
    protected void onNewPotionEffect(EffectInstance id) {
        super.onNewPotionEffect(id);
    }

    @Override
    protected void onChangedPotionEffect(EffectInstance id, boolean p_70695_2_) {
        super.onChangedPotionEffect(id, p_70695_2_);
    }

    @Override
    protected void onFinishedPotionEffect(EffectInstance effect) {
        super.onFinishedPotionEffect(effect);
    }

    @Override
    public void heal(float healAmount) {
        super.heal(healAmount);
    }

    @Override
    public float getHealth() {
        return super.getHealth();
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public float getPitch(float partialTicks) {
        return super.getPitch(partialTicks);
    }

    @Override
    protected void blockUsingShield(LivingEntity p_190629_1_) {
        super.blockUsingShield(p_190629_1_);
    }

    @Override
    protected void func_213371_e(LivingEntity p_213371_1_) {
        super.func_213371_e(p_213371_1_);
    }

    @Nullable
    @Override
    public DamageSource getLastDamageSource() {
        return super.getLastDamageSource();
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        return super.attackEntityAsMob(entityIn);
    }

    @Override
    protected boolean isInDaylight() {
        return super.isInDaylight();
    }

    @Override
    protected void handleFluidJump(Tag<Fluid> fluidTag) {
        super.handleFluidJump(fluidTag);
    }

    @Override
    protected float getWaterSlowDown() {
        return super.getWaterSlowDown();
    }

    @Override
    public void travel(Vec3d p_213352_1_) {
        super.travel(p_213352_1_);
    }

    @Override
    public float getAIMoveSpeed() {
        return super.getAIMoveSpeed();
    }

    @Override
    public boolean isHolding(Item itemIn) {
        return super.isHolding(itemIn);
    }
}
