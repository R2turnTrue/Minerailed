package xyz.r2turntrue.minerailed

import net.minestom.server.MinecraftServer
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extensions.Extension
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import xyz.r2turntrue.minerailed.commands.Minecart
import xyz.r2turntrue.minerailed.commands.NewGame
import xyz.r2turntrue.minerailed.commands.Noise
import xyz.r2turntrue.minerailed.listener.RailPreviewer
import xyz.r2turntrue.minerailed.rules.placement.RailPlacementRule
import java.util.*
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames


lateinit var dim: DimensionType
lateinit var defaultInstance: InstanceContainer

class MinerailedExtension : Extension() {

    override fun initialize(): LoadStatus {
        dim = DimensionType.builder(NamespaceID.from("fullbright"))
            .ambientLight(10000.0F)
            .height(416)
            .logicalHeight(200)
            .minY(-64)
            .build()
        MinecraftServer.getDimensionTypeManager().addDimension(dim)
        defaultInstance = InstanceContainer(UUID.randomUUID(), dim)
        defaultInstance.setGenerator { unit ->
            val start = unit.absoluteStart()
            val size = unit.size()
            for (x in 0 until size.blockX()) {
                for (z in 0 until size.blockZ()) {
                    unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 0.0, z.toDouble()), Block.GRASS_BLOCK)
                    for (y in 0 until Math.min(40 - start.blockY(), size.blockY())) {
                        unit.modifier().setBlock(start.add(x.toDouble(), y.toDouble(), z.toDouble()), Block.DIRT)
                    }
                }
            }
        }
        MinecraftServer.getInstanceManager().registerInstance(defaultInstance)

        MinecraftServer.getCommandManager().register(NewGame)
        MinecraftServer.getCommandManager().register(Minecart)
        MinecraftServer.getCommandManager().register(Noise)

        MinecraftServer.getBlockManager().registerBlockPlacementRule(RailPlacementRule)

        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent::class.java) {
            it.setSpawningInstance(defaultInstance)
        }

        EventNode.all("previewer").apply {
            MinecraftServer.getGlobalEventHandler().addChild(this)
            RailPreviewer.register(this)
        }

        logger().info("[MinerailedExtension] has been enabled!")

        return LoadStatus.SUCCESS
    }

    override fun terminate() {
        logger().info("[MinerailedExtension] has been disabled!")
    }

}
