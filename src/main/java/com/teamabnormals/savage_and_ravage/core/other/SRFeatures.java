package com.teamabnormals.savage_and_ravage.core.other;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.core.util.DataUtil;
import com.teamabnormals.savage_and_ravage.core.SavageAndRavage;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class SRFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, SavageAndRavage.MOD_ID);

	public static void registerPools() {
		Pools.register(new StructureTemplatePool(new ResourceLocation(SavageAndRavage.MOD_ID, "pillager_outpost/pillagers"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.legacy(SavageAndRavage.MOD_ID + ":pillager_outpost/pillager"), 1)), StructureTemplatePool.Projection.RIGID));
		Pools.register(new StructureTemplatePool(new ResourceLocation(SavageAndRavage.MOD_ID, "pillager_outpost/vindicators"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.legacy(SavageAndRavage.MOD_ID + ":pillager_outpost/vindicator"), 1)), StructureTemplatePool.Projection.RIGID));
		//TODO
		DataUtil.addToJigsawPattern(new ResourceLocation("pillager_outpost/features"), StructurePoolElement.legacy(SavageAndRavage.MOD_ID + ":pillager_outpost/feature_targets_arrow").apply(StructureTemplatePool.Projection.RIGID), 2);
		Pools.register(new StructureTemplatePool(new ResourceLocation(SavageAndRavage.MOD_ID, "pillager_outpost/note_blocks"), new ResourceLocation("empty"), noteBlocks(), StructureTemplatePool.Projection.RIGID));
	}

	private static ImmutableList<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> noteBlocks() {
		ImmutableList.Builder<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> builder = ImmutableList.builder();
		for (int i = 0; i <= 24; i++)
			builder.add(Pair.of(StructurePoolElement.legacy(SavageAndRavage.MOD_ID + ":pillager_outpost/note_blocks/note_block" + i), 1));
		return builder.build();
	}

	public static final class SRConfiguredFeatures {
		public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, SavageAndRavage.MOD_ID);

		
		private static <FC extends FeatureConfiguration, F extends Feature<FC>> RegistryObject<ConfiguredFeature<FC, ?>> register(String name, Supplier<ConfiguredFeature<FC, F>> feature) {
			return CONFIGURED_FEATURES.register(name, feature);
		}

	}

	public static final class SRPlacedFeatures {
		public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, SavageAndRavage.MOD_ID);

		@SuppressWarnings("unchecked")
		private static RegistryObject<PlacedFeature> register(String name, RegistryObject<? extends ConfiguredFeature<?, ?>> feature, PlacementModifier... placementModifiers) {
			return PLACED_FEATURES.register(name, () -> new PlacedFeature((Holder<ConfiguredFeature<?, ?>>) feature.getHolder().get(), ImmutableList.copyOf(placementModifiers)));
		}
	}
}