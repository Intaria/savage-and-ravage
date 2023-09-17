package com.teamabnormals.savage_and_ravage.core.registry;

import com.teamabnormals.blueprint.core.util.registry.EntitySubRegistryHelper;
import com.teamabnormals.savage_and_ravage.common.entity.decoration.BurningBanner;
import com.teamabnormals.savage_and_ravage.common.entity.monster.*;
import com.teamabnormals.savage_and_ravage.common.entity.projectile.*;
import com.teamabnormals.savage_and_ravage.core.SavageAndRavage;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = SavageAndRavage.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SREntityTypes {
	public static final EntitySubRegistryHelper HELPER = SavageAndRavage.REGISTRY_HELPER.getEntitySubHelper();
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SavageAndRavage.MOD_ID);

	public static final RegistryObject<EntityType<Iceologer>> ICEOLOGER = ENTITIES.register("iceologer", () -> EntityType.Builder.of(Iceologer::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build(SavageAndRavage.MOD_ID + ":iceologer"));
	public static final RegistryObject<EntityType<Trickster>> TRICKSTER = HELPER.createLivingEntity("trickster", Trickster::new, MobCategory.MONSTER, 0.6F, 1.89F);

	public static final RegistryObject<EntityType<BurningBanner>> BURNING_BANNER = ENTITIES.register("burning_banner", () -> EntityType.Builder.<BurningBanner>of(BurningBanner::new, MobCategory.MISC).fireImmune().sized(1.0F, 2.0F).noSummon().build(SavageAndRavage.MOD_ID + ":burning_banner"));
	public static final RegistryObject<EntityType<IceChunk>> ICE_CHUNK = ENTITIES.register("ice_chunk", () -> EntityType.Builder.<IceChunk>of(IceChunk::new, MobCategory.MISC).sized(2.2F, 1.0F).clientTrackingRange(8).updateInterval(Integer.MAX_VALUE).build(SavageAndRavage.MOD_ID + ":ice_chunk"));
	public static final RegistryObject<EntityType<IceCloud>> ICE_CLOUD = ENTITIES.register("ice_cloud", () -> EntityType.Builder.<IceCloud>of(IceCloud::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(8).build(SavageAndRavage.MOD_ID + ":ice_cloud"));
	public static final RegistryObject<EntityType<RunePrison>> RUNE_PRISON = ENTITIES.register("rune_prison", () -> EntityType.Builder.<RunePrison>of(RunePrison::new, MobCategory.MISC).fireImmune().sized(1.35F, 0.7F).build(SavageAndRavage.MOD_ID + ":rune_prison"));
	public static final RegistryObject<EntityType<ConfusionBolt>> CONFUSION_BOLT = ENTITIES.register("confusion_bolt", () -> EntityType.Builder.<ConfusionBolt>of(ConfusionBolt::new, MobCategory.MISC).fireImmune().sized(1.0F, 1.0F).build(SavageAndRavage.MOD_ID + ":confusion_bolt"));

	public static void registerEntitySpawns() {
		SpawnPlacements.register(ICEOLOGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Iceologer::canIceologerSpawn);
		SpawnPlacements.register(TRICKSTER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
	}

	public static void registerWaveMembers() {
		Raid.RaiderType.create("TRICKSTER", TRICKSTER.get(), new int[]{0, 0, 0, 0, 0, 1, 1, 2});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(ICEOLOGER.get(), Iceologer.registerAttributes().build());
		event.put(TRICKSTER.get(), Trickster.registerAttributes().build());
	}
}
