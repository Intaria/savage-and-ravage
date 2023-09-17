package com.teamabnormals.savage_and_ravage.core.other;

import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.savage_and_ravage.common.entity.OwnableMob;
import com.teamabnormals.savage_and_ravage.common.entity.ai.goal.CelebrateTargetBlockHitGoal;
import com.teamabnormals.savage_and_ravage.common.entity.ai.goal.ImprovedCrossbowGoal;
import com.teamabnormals.savage_and_ravage.common.entity.decoration.BurningBanner;
import com.teamabnormals.savage_and_ravage.common.entity.monster.*;
import com.teamabnormals.savage_and_ravage.common.item.PottableItem;
import com.teamabnormals.savage_and_ravage.core.SRConfig;
import com.teamabnormals.savage_and_ravage.core.SavageAndRavage;
import com.teamabnormals.savage_and_ravage.core.other.tags.SREntityTypeTags;
import com.teamabnormals.savage_and_ravage.core.other.tags.SRItemTags;
import com.teamabnormals.savage_and_ravage.core.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = SavageAndRavage.MOD_ID)
public class SREvents {
	public static String POOF_KEY = "minecraft:poof";
	public static String NO_KNOCKBACK_KEY = SavageAndRavage.MOD_ID + "no_knockback";

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Mob mob) {
			if (mob instanceof AbstractVillager villager) {
				villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, Iceologer.class, 8.0F, 0.8D, 0.8D));
				villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(villager, Trickster.class, 8.0F, 0.8D, 0.8D));
			} else if (mob instanceof Pillager pillager) {
				mob.goalSelector.availableGoals.stream().map(it -> it.goal).filter(it -> it instanceof RangedCrossbowAttackGoal<?>).findFirst().ifPresent(goal -> {
					mob.goalSelector.removeGoal(goal);
					mob.goalSelector.addGoal(3, new ImprovedCrossbowGoal<>(pillager, 1.0D, 8.0F, 5.0D));
				});
				mob.goalSelector.addGoal(5, new CelebrateTargetBlockHitGoal(pillager));
			} else if (mob instanceof Evoker && SRConfig.COMMON.evokersUseTotems.get())
				mob.goalSelector.addGoal(1, new AvoidEntityGoal<>((Evoker) mob, IronGolem.class, 8.0F, 0.6D, 1.0D) {
					@Override
					public boolean canUse() {
						return SRConfig.COMMON.evokersUseTotems.get() && TrackedDataManager.INSTANCE.getValue(this.mob, SRDataProcessors.TOTEM_SHIELD_TIME) > 0 && super.canUse();
					}
				});
			else if (mob instanceof Vex && SRConfig.COMMON.reducedVexHealth.get()) {
				AttributeInstance maxHealth = mob.getAttribute(Attributes.MAX_HEALTH);
				if (maxHealth != null)
					maxHealth.setBaseValue(2.0);
				if (mob.getHealth() > mob.getMaxHealth())
					mob.setHealth(mob.getMaxHealth());
			}
			
				if (mob instanceof IronGolem)
					mob.targetSelector.availableGoals.stream().map(it -> it.goal).filter(it -> it instanceof NearestAttackableTargetGoal<?>).findFirst().ifPresent(goal -> {
						mob.targetSelector.removeGoal(goal);
						mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, Mob.class, 5, false, false, e -> e instanceof Enemy));
					});
				else if (mob.getType().is(SREntityTypeTags.CREEPERS))
					mob.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(mob, IronGolem.class, true));
			
		}
	}

	private static final String POISON_TAG = SavageAndRavage.MOD_ID + "poison_potato_applied";

	@SubscribeEvent
	public static void onLivingJump(LivingJumpEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.getEffect(SRMobEffects.WEIGHT.get()) != null)
			entity.setDeltaMovement(entity.getDeltaMovement().x(), 0.0D, entity.getDeltaMovement().z());
	}

	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
		LivingEntity entity = event.getEntity();
		LivingEntity target = event.getTarget();
		if (target != null) {
			if (entity instanceof AbstractGolem && !(entity instanceof Shulker) && target instanceof OwnableMob)
				if (((OwnableMob) target).getOwner() instanceof Player && ((Mob) target).getTarget() != entity)
					((AbstractGolem) entity).setTarget(null);
			if (entity instanceof Evoker && SRConfig.COMMON.evokersUseTotems.get() && TrackedDataManager.INSTANCE.getValue(entity, SRDataProcessors.TOTEM_SHIELD_TIME) > 0)
				((Evoker) entity).setTarget(null);
		}
	}

	public static boolean entitySafeFromExplosion(Entity entity, boolean creeperTypeNoGriefing) {
		if (creeperTypeNoGriefing && entity.getType().is(SREntityTypeTags.CREEPER_IMMUNE))
			return true;
		else if (entity instanceof ItemEntity) {
			ItemStack stack = ((ItemEntity) entity).getItem();
			return creeperTypeNoGriefing || stack.is(SRItemTags.EXPLOSION_IMMUNE);
		}
		return false;
	}

	public static boolean isEmptySpace(BlockGetter world, BlockPos pos) {
		return world.getBlockState(pos).isAir() || !world.getFluidState(pos).isEmpty();
	}

	@SubscribeEvent
	public static void onLivingKnockback(LivingKnockBackEvent event) {
		if (event.getEntity().getPersistentData().getBoolean(NO_KNOCKBACK_KEY))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Evoker && SRConfig.COMMON.evokersUseTotems.get()) {
			if (TrackedDataManager.INSTANCE.getValue(entity, SRDataProcessors.TOTEM_SHIELD_TIME) > 0) {
				if (event.getSource().getDirectEntity() instanceof Projectile)
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event) {
		LivingEntity target = event.getEntity();
		if (event.getSource().isExplosion()) {
			double decrease = 0;
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				ItemStack stack = target.getItemBySlot(slot);
				Collection<AttributeModifier> modifiers = stack.getAttributeModifiers(slot).get(SRAttributes.EXPLOSIVE_DAMAGE_REDUCTION.get());
				if (modifiers.isEmpty())
					continue;

				decrease += modifiers.stream().mapToDouble(AttributeModifier::getAmount).sum();
				stack.hurtAndBreak(22 - EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLAST_PROTECTION, stack) * 8, target, onBroken -> onBroken.broadcastBreakEvent(slot));
			}

			if (decrease != 0)
				event.setAmount(event.getAmount() - (float) (event.getAmount() * decrease));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingDamageDelayed(LivingDamageEvent event) {
		LivingEntity target = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (target instanceof Evoker && SRConfig.COMMON.evokersUseTotems.get()) {
			IDataManager data = (IDataManager) target;
			if (target.getHealth() - event.getAmount() <= 0 && event.getSource().getDirectEntity() instanceof Projectile) {
				if (data.getValue(SRDataProcessors.TOTEM_SHIELD_TIME) <= 0 && data.getValue(SRDataProcessors.TOTEM_SHIELD_COOLDOWN) <= 0) {
					event.setCanceled(true);
					target.setHealth(2.0F);
					data.setValue(SRDataProcessors.TOTEM_SHIELD_TIME, 600);
					if (!target.level.isClientSide())
						target.level.broadcastEntityEvent(target, (byte) 35);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onProjectileImpact(ProjectileImpactEvent event) {
		HitResult result = event.getRayTraceResult();
		if (result instanceof BlockHitResult blockResult) {
			Entity entity = event.getEntity();
			if (entity.level.getBlockState(blockResult.getBlockPos()).is(Blocks.TARGET)) {
				if (!entity.level.isClientSide()) {
					IDataManager data = (IDataManager) entity;
					UUID id = data.getValue(SRDataProcessors.CROSSBOW_OWNER).orElse(null);
					if (id != null) {
						Entity crossbowOwner = ((ServerLevel) entity.level).getEntity(id);
						if (crossbowOwner instanceof Raider)
							TrackedDataManager.INSTANCE.setValue(crossbowOwner, SRDataProcessors.TARGET_HIT, true);
						data.setValue(SRDataProcessors.CROSSBOW_OWNER, Optional.empty());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		Level world = event.getLevel();
		BlockPos pos = event.getPos();
		if (world.getBlockState(pos).getBlock() instanceof AbstractBannerBlock) {
			List<BurningBanner> burningBanners = world.getEntitiesOfClass(BurningBanner.class, new AABB(pos));
			for (BurningBanner burningBanner : burningBanners) {
				if (burningBanner.getBannerPosition() != null && burningBanner.getBannerPosition().equals(pos)) {
					burningBanner.extinguishFire();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		Player player = event.getEntity();
		BlockPos pos = event.getPos();
		Level world = event.getLevel();

		if (stack.getItem() instanceof PottableItem && world.getBlockState(pos).getBlock() == Blocks.FLOWER_POT) {
			BlockState pottedState = ((PottableItem) stack.getItem()).getPottedState(player.getDirection().getOpposite());
			if (pottedState == null)
				return;
			world.setBlockAndUpdate(pos, pottedState);
			player.awardStat(Stats.POT_FLOWER);
			if (!player.isCreative()) stack.shrink(1);
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		} else if (isValidBurningBannerPos(world, pos)) {
			boolean isFlintAndSteel = stack.getItem() instanceof FlintAndSteelItem;
			if ((isFlintAndSteel || stack.getItem() instanceof FireChargeItem)) {
				SoundEvent sound = isFlintAndSteel ? SoundEvents.FLINTANDSTEEL_USE : SoundEvents.FIRECHARGE_USE;
				float pitch = isFlintAndSteel ? new Random().nextFloat() * 0.4F + 0.8F : (new Random().nextFloat() - new Random().nextFloat()) * 0.2F + 1.0F;
				world.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, pitch);

				if (isFlintAndSteel) {
					stack.hurtAndBreak(1, player, (p_219998_1_) -> p_219998_1_.broadcastBreakEvent(event.getHand()));
				} else if (!player.isCreative()) {
					stack.shrink(1);
				}

				world.addFreshEntity(new BurningBanner(world, pos, player));
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void livingUpdate(LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		Level world = entity.level;
		IDataManager data = (IDataManager) entity;
		if (!world.isClientSide()) {
			CompoundTag persistentData = entity.getPersistentData();
			if (persistentData.getBoolean(NO_KNOCKBACK_KEY))
				persistentData.putBoolean(NO_KNOCKBACK_KEY, false);
			if (entity instanceof Raider) {
				int celebrationTime = data.getValue(SRDataProcessors.CELEBRATION_TIME);
				if (celebrationTime > 0)
					data.setValue(SRDataProcessors.CELEBRATION_TIME, celebrationTime - 1);
			}
		}
		if (entity instanceof Evoker) {
			int shieldTime = data.getValue(SRDataProcessors.TOTEM_SHIELD_TIME);
			if (shieldTime > 0)
				data.setValue(SRDataProcessors.TOTEM_SHIELD_TIME, shieldTime - 1);
			else if (shieldTime == 0) {
				data.setValue(SRDataProcessors.TOTEM_SHIELD_COOLDOWN, 1800);
				data.setValue(SRDataProcessors.TOTEM_SHIELD_TIME, -1);
			}
			int cooldown = data.getValue(SRDataProcessors.TOTEM_SHIELD_COOLDOWN);
			if (cooldown > 0)
				data.setValue(SRDataProcessors.TOTEM_SHIELD_COOLDOWN, cooldown - 1);
		}
	}

	@SubscribeEvent
	public static void visibilityMultiplierEvent(LivingEvent.LivingVisibilityEvent event) {
		LivingEntity entity = event.getEntity();
		if (TrackedDataManager.INSTANCE.getValue(entity, SRDataProcessors.INVISIBLE_DUE_TO_MASK)) {
			double armorCover = entity.getArmorCoverPercentage();
			if (armorCover < 0.1F) {
				armorCover = 0.1F;
			}
			event.modifyVisibility(1 / armorCover); //potentially slightly inaccurate
			event.modifyVisibility(0.1);
		}
	}

	public static void spawnMaskParticles(Level world, AABB box, int loops) {
		if (!world.isClientSide) {
			RandomSource random = world.getRandom();
			for (int i = 0; i < loops; i++) {
				double x = box.min(Direction.Axis.X) + (random.nextFloat() * box.getXsize());
				double y = box.min(Direction.Axis.Y) + (random.nextFloat() * box.getYsize());
				double z = box.min(Direction.Axis.Z) + (random.nextFloat() * box.getZsize());
				NetworkUtil.spawnParticle(POOF_KEY, world.dimension(), x, y, z, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	public static boolean isValidBurningBannerPos(Level world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() instanceof AbstractBannerBlock) {
			List<BurningBanner> banners = world.getEntitiesOfClass(BurningBanner.class, new AABB(pos));
			boolean noBurningBanners = true;
			for (BurningBanner banner : banners) {
				if (banner.getBannerPosition() != null && banner.getBannerPosition().equals(pos))
					noBurningBanners = false;
			}
			return noBurningBanners;
		}
		return false;
	}

	public static ItemStack createRocket(RandomSource random) {
		ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET, random.nextInt(16) + 1);
		CompoundTag fireworks = rocket.getOrCreateTagElement("Fireworks");
		ListTag explosions = new ListTag();
		CompoundTag explosion = new CompoundTag();
		fireworks.put("Explosions", explosions);
		explosions.add(explosion);
		explosion.putIntArray("Colors", randomColors(random));
		if (random.nextInt(2) == 0)
			explosion.putIntArray("FadeColors", randomColors(random));
		if (random.nextInt(2) == 0)
			explosion.putBoolean("Flicker", true);
		if (random.nextInt(2) == 0)
			explosion.putBoolean("Trail", true);
		FireworkRocketItem.Shape[] values = FireworkRocketItem.Shape.values();
		explosion.putInt("Type", values[random.nextInt(values.length)].getId());
		fireworks.putInt("Flight", random.nextInt(3) + 1);
		return rocket;
	}

	public static int[] randomColors(RandomSource random) {
		int[] colors = new int[random.nextInt(3) + 1];
		DyeColor[] values = DyeColor.values();
		for (int i = 0; i < colors.length; i++) {
			colors[i] = values[random.nextInt(values.length)].getFireworkColor();
		}
		return colors;
	}
}
