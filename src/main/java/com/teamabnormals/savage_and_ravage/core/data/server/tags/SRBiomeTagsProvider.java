package com.teamabnormals.savage_and_ravage.core.data.server.tags;

import com.teamabnormals.savage_and_ravage.core.SavageAndRavage;
import com.teamabnormals.savage_and_ravage.core.other.tags.SRBiomeTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SRBiomeTagsProvider extends BiomeTagsProvider {

	public SRBiomeTagsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, SavageAndRavage.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(SRBiomeTags.HAS_ICEOLOGER).addTag(Tags.Biomes.IS_SNOWY);
	}
}