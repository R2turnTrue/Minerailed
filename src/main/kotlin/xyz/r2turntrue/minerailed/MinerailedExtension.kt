package xyz.r2turntrue.minerailed

import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension;
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import xyz.r2turntrue.minerailed.commands.Minecart
import xyz.r2turntrue.minerailed.commands.NewGame
import xyz.r2turntrue.minerailed.rules.placement.RailPlacementRule

lateinit var dim: DimensionType

class MinerailedExtension : Extension() {

    override fun initialize(): LoadStatus {
        dim = DimensionType.builder(NamespaceID.from("fullbright"))
            .ambientLight(10000.0F)
            .height(416)
            .logicalHeight(200)
            .minY(-64)
            .build()
        MinecraftServer.getDimensionTypeManager().addDimension(dim)

        MinecraftServer.getCommandManager().register(NewGame)
        MinecraftServer.getCommandManager().register(Minecart)

        MinecraftServer.getBlockManager().registerBlockPlacementRule(RailPlacementRule)

        logger().info("[MinerailedExtension] has been enabled!")

        return LoadStatus.SUCCESS
    }

    override fun terminate() {
        logger().info("[MinerailedExtension] has been disabled!")
    }

}
