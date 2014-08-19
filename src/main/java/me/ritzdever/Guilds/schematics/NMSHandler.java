package me.ritzdever.Guilds.schematics;


import net.minecraft.server.v1_7_R3.*;


import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHandler {
    public static boolean setBlockFast(org.bukkit.World world, int x, int y, int z, Block blockId, byte data) {
	World w = ((CraftWorld) world).getHandle();
	Chunk chunk = w.getChunkAt(x >> 4, z >> 4);

	return chunk.a(x & 0xF, y, z & 0xF, blockId, data);
    }

    public void forceBlockLightLevel(org.bukkit.World world, int x, int y, int z, int level) {
	World w = ((CraftWorld) world).getHandle();
	w.b(EnumSkyBlock.BLOCK, x, y, z, level);
    }

    public static void queueChunkForUpdate(Player player, int cx, int cz) {
	((CraftPlayer) player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(cx, cz));
    }

    public static void recalculateBlockLighting(org.bukkit.World world, int x, int y, int z) {
	World w = ((CraftWorld) world).getHandle();
	w.z(x, y, z);
    }
}
