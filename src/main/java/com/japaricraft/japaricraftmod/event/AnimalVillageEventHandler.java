package com.japaricraft.japaricraftmod.event;

import com.japaricraft.japaricraftmod.world.structure.MapGenAnimalVillage;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AnimalVillageEventHandler {
    MapGenAnimalVillage mapGenAnimalVillage = new MapGenAnimalVillage();

    public static BlockPos getHeight(World world, BlockPos pos) {
        for (int y = 0; y < 256; y++) {
            BlockPos pos1 = pos.up(y);
            if (world.getBlockState(pos1.up()).getBlock() == Blocks.AIR && world.getBlockState(pos1.down()).getBlock() != Blocks.AIR) {
                return pos1;
            }
        }
        return pos;
    }

    // generateStructuresInChunk相当
    @SubscribeEvent
    public void onPopulateChunkEvent(PopulateChunkEvent.Pre event) {

        int x = (event.getChunkX() * 16) + event.getRand().nextInt(16);
        int z = (event.getChunkZ() * 16) + event.getRand().nextInt(16);
        BlockPos height = getHeight(event.getWorld(), new BlockPos(x, 0, z));
        // 通常世界(Overworld)に生成する
        if (event.getWorld().provider.getDimension() == 0 && BiomeDictionary.hasType(event.getWorld().getBiome(height), BiomeDictionary.Type.FOREST) && !BiomeDictionary.hasType(event.getWorld().getBiome(height), BiomeDictionary.Type.DEAD)) {
            mapGenAnimalVillage.generate(event.getWorld(), event.getChunkX(), event.getChunkZ(), null);

            mapGenAnimalVillage.generateStructure(event.getWorld(), event.getRand(), event.getWorld().getChunkFromChunkCoords(event.getChunkX(), event.getChunkZ()).getPos());
        }
    }
}
