package com.teamabnormals.savage_and_ravage.core.data.server.tags;

import com.teamabnormals.blueprint.core.other.tags.BlueprintBlockTags;
import com.teamabnormals.savage_and_ravage.core.SavageAndRavage;
import com.teamabnormals.savage_and_ravage.core.other.tags.SRBlockTags;
import com.teamabnormals.savage_and_ravage.core.registry.SRBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SRBlockTagsProvider extends BlockTagsProvider {

	public SRBlockTagsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, SavageAndRavage.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
	}
}