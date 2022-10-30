package xyz.r2turntrue.minerailed.rules.placement

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.instance.block.rule.BlockPlacementRule
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

val railShapeTag = Tag.String("RailShape")
val placedRail = Tag.Boolean("MinerailedRail")

// todo: rail placing preview

fun buildRailItem(shape: String): ItemStack =
    ItemStack.of(Material.RAIL, 64)
        .withTag(railShapeTag, shape)
        .withDisplayName(Component.text(shape, NamedTextColor.WHITE)
            .decoration(TextDecoration.ITALIC, false))

object RailPlacementRule : BlockPlacementRule(Block.RAIL) {
    override fun blockUpdate(instance: Instance, blockPosition: Point, currentBlock: Block): Block =
        currentBlock

    fun buildRail(shape: String) =
        Block.RAIL
            .withProperties(
                mapOf(
                    "waterlogged" to "false",
                    "shape" to shape
                )
            )

    override fun blockPlace(
        instance: Instance,
        block: Block,
        blockFace: BlockFace,
        blockPosition: Point,
        pl: Player
    ): Block =
        buildRail(pl.itemInMainHand.getTag(railShapeTag))
}