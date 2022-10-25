package xyz.r2turntrue.minerailed.rules.placement

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.instance.block.rule.BlockPlacementRule
import xyz.r2turntrue.minerailed.coord.*
import xyz.r2turntrue.minerailed.utils.nearBlockPos
import kotlin.math.abs

object RailPlacementRule : BlockPlacementRule(Block.RAIL) {
    private enum class Shape {
        EAST_WEST,
        NORTH_EAST,
        NORTH_SOUTH,
        NORTH_WEST,
        SOUTH_EAST,
        SOUTH_WEST/*, // don't needed because there's not ascending rail in unrailed

        ASCENDING_EAST,
        ASCENDING_NORTH,
        ASCENDING_SOUTH,
        ASCENDING_WEST
        */
    }

    override fun blockUpdate(instance: Instance, blockPosition: Point, currentBlock: Block): Block {
        // ???
        return currentBlock
    }

    override fun blockPlace(
        instance: Instance,
        block: Block,
        blockFace: BlockFace,
        blockPosition: Point,
        pl: Player
    ): Block =
        block.withProperties(buildProperties(getShape(pl, blockPosition, instance)))

    private fun buildProperties(shape: Shape) =
        mapOf(
            "shape" to shape.toString().lowercase(),
            "waterlogged" to "false"
        )

    private fun trackRail(root: BlockPos, instance: Instance, exclude: List<BlockPos> = listOf()): BlockPos? {
        return root.nearBlockPos()
            .map { Pair(it, instance.getBlock(it)) }
            .firstOrNull { !exclude.contains(it.first) || it.second.compare(Block.RAIL) }
            ?.let { it.first }
    }

    private enum class WhereIsFrom {
        POSITIVE_X, // +X
        NEGATIVE_X, // -X
        POSITIVE_Z, // +Z
        NEGATIVE_Z, // -Z
        UNKNOWN
    }

    private fun whereIsFrom(root: BlockPos, other: BlockPos): WhereIsFrom {
        val xOff = root.blockX - other.blockX
        val zOff = root.blockZ - other.blockZ

        return if(xOff >= 1)
            WhereIsFrom.POSITIVE_X
        else if(xOff < 0)
            WhereIsFrom.NEGATIVE_X
        else if(zOff >= 1)
            WhereIsFrom.POSITIVE_Z
        else if(zOff < 0)
            WhereIsFrom.NEGATIVE_Z
        else
            WhereIsFrom.UNKNOWN
    }

    private fun getShape(player: Player, blockPosition: Point, instance: Instance): Shape {
        var degrees = (player.position.yaw() - 90) % 360
        if (degrees < 0) {
            degrees += 360f
        }
        var temp = if (0 <= degrees && degrees < 45) {
            Shape.NORTH_SOUTH
        } else if (45 <= degrees && degrees < 135) {
            Shape.EAST_WEST
        } else if (135 <= degrees && degrees < 225) {
            Shape.NORTH_SOUTH
        } else if (225 <= degrees && degrees < 315) {
            Shape.EAST_WEST
        } else { // 315 <= degrees && degrees < 360
            Shape.NORTH_SOUTH
        }

        val blockPos = blockPosition.toBlockPos()

        trackRail(blockPos, instance)
            ?.also {
                val wh = whereIsFrom(blockPos, it)
                if(wh == WhereIsFrom.POSITIVE_X || wh == WhereIsFrom.NEGATIVE_X)
                    temp = Shape.EAST_WEST
                if(wh == WhereIsFrom.POSITIVE_Z || wh == WhereIsFrom.NEGATIVE_Z)
                    temp = Shape.EAST_WEST

                trackRail(it, instance, listOf(blockPos, it))
                    ?.also { prepre ->
                        val prepreWh = whereIsFrom(it, prepre)
                        if (wh == WhereIsFrom.POSITIVE_X) {
                            if (prepreWh == WhereIsFrom.POSITIVE_Z) {
                                instance.setBlock(
                                    it, Block.RAIL
                                        .withProperties(buildProperties(Shape.SOUTH_WEST))
                                )
                            } else if (prepreWh == WhereIsFrom.NEGATIVE_Z) {
                                instance.setBlock(
                                    it, Block.RAIL
                                        .withProperties(buildProperties(Shape.NORTH_WEST))
                                )
                            }
                        } else if (wh == WhereIsFrom.POSITIVE_Z) {
                            if (prepreWh == WhereIsFrom.POSITIVE_X) {
                                instance.setBlock(
                                    it, Block.RAIL
                                        .withProperties(buildProperties(Shape.SOUTH_EAST))
                                )
                            } else if (prepreWh == WhereIsFrom.NEGATIVE_X) {
                                instance.setBlock(
                                    it, Block.RAIL
                                        .withProperties(buildProperties(Shape.NORTH_EAST))
                                )
                            }
                        }
                    }
            }

        return temp
    }
}