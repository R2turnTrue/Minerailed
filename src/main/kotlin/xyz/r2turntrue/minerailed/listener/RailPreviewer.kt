package xyz.r2turntrue.minerailed.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.packet.server.play.BlockChangePacket
import xyz.r2turntrue.minerailed.coord.BlockPos
import xyz.r2turntrue.minerailed.coord.getBlock
import xyz.r2turntrue.minerailed.coord.toBlockPos
import xyz.r2turntrue.minerailed.coord.toPos
import xyz.r2turntrue.minerailed.rules.placement.RailPlacementRule
import xyz.r2turntrue.minerailed.rules.placement.placedRail
import xyz.r2turntrue.minerailed.rules.placement.railShapeTag
import java.util.concurrent.CompletableFuture

val previewPos = hashMapOf<Player, BlockPos>()
val spawnedPlayers = arrayListOf<Player>()

object RailPreviewer {

    fun register(eventNode: EventNode<Event>) {
        eventNode.addListener(PlayerSpawnEvent::class.java) {
            it.player.sendMessage(Component.text("Hi"))
            spawnedPlayers.add(it.player)
        }
        eventNode.addListener(PlayerDisconnectEvent::class.java) {
            spawnedPlayers.remove(it.player)
        }
        eventNode.addListener(PlayerBlockPlaceEvent::class.java) {
            if (!it.block.compare(Block.RAIL)) return@addListener
            val down = it.blockPosition.sub(0.0, 2.0, 0.0)
            it.isCancelled = true
            //println(it.instance.getBlock(down))
            if (!it.instance.getBlock(down).compare(Block.GRASS_BLOCK)) return@addListener
            val player = it.player
            val shape = player.inventory.itemInMainHand.getTag(railShapeTag)
            player.setHeldItemSlot(7)
            player.inventory.clear()
            if (player in previewPos) {
                previewPos[player]?.let { preview ->
                    player.sendPacket(BlockChangePacket(preview.toPos(), Block.AIR))
                }
                previewPos.remove(player)
            }
            it.instance.setBlock(
                it.blockPosition.sub(0.0, 1.0, 0.0), Block.RAIL.withProperties(
                    mapOf(
                        "waterlogged" to "false",
                        "shape" to shape
                    )
                ).withTag(placedRail, true)
            )
        }
        CompletableFuture.runAsync {
            while (true) {
                spawnedPlayers.forEach { player ->
                    //println("a")
                    val stack = player.inventory.itemInMainHand
                    //println("${stack.material()} ; ${stack.getTag(railShapeTag)} ; ${stack.hasTag(railShapeTag)}")
                    val bfPos = player.getTargetBlockPosition(20)
                    //println("a1.5")
                    fun getBlock(pos: BlockPos): Block
                        = player.instance!!.getBlock(pos)

                    fun goElse() {
                        if(player in previewPos) {
                            previewPos[player]?.let { preview ->
                                player.sendPacket(BlockChangePacket(preview.toPos(), Block.AIR))
                            }
                            previewPos.remove(player)
                        }
                    }
                    if(stack.material() == Material.RAIL && stack.hasTag(railShapeTag) && bfPos != null) {
                        // todo: play sfx_rail_place_{random}
                        // todo: is not placable the rail be red

                        val pos = bfPos.add(0.0, 1.0, 0.0)
                        if(!getBlock(pos.toBlockPos()).hasTag(placedRail) && getBlock(bfPos.toBlockPos()).compare(Block.GRASS_BLOCK)) {

                            player.sendActionBar(
                                Component.text(
                                    "RIGHT CLICK TO PLACE",
                                    NamedTextColor.GREEN,
                                    TextDecoration.BOLD
                                )
                            )
                            previewPos[player]
                                ?.also { preview ->
                                    player.sendPacket(BlockChangePacket(preview.toPos(), Block.AIR))
                                }
                            previewPos[player] = pos.toBlockPos()
                            player.sendPacket(
                                BlockChangePacket(
                                    pos,
                                    RailPlacementRule.buildRail(stack.getTag(railShapeTag))
                                )
                            )
                        } else {
                            goElse()
                        }
                    } else {
                        goElse()
                    }
                }
                Thread.sleep(25)
            }
        }
    }

}