package xyz.r2turntrue.minerailed.rules.placement

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.instance.block.rule.BlockPlacementRule
import xyz.r2turntrue.minerailed.coord.*
import xyz.r2turntrue.minerailed.utils.nearBlockPos

object RailPlacementRule_Old : BlockPlacementRule(Block.RAIL) {
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
    ): Block {
        println("Block Place Handle at: ${blockPosition.toBlockPos()}")
        return block.withProperties(buildProperties(getShape(pl, blockPosition, instance)))
    }

    private fun buildProperties(shape: Shape): Map<String, String> {
        println("sh: ${shape.toString().lowercase()}")
        return mapOf(
            "shape" to shape.toString().lowercase(),
            "waterlogged" to "false"
        )
    }

    private fun trackRail(root: BlockPos, instance: Instance, exclude: List<BlockPos> = listOf(), yOff: Int = 1): BlockPos? {
        return root.nearBlockPos(yOff)
            .map { Pair(it, instance.getBlock(it)) }
            .firstOrNull { !exclude.contains(it.first) && it.second.compare(Block.RAIL) }
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
            Shape.EAST_WEST
        } else if (45 <= degrees && degrees < 135) {
            Shape.NORTH_SOUTH
        } else if (135 <= degrees && degrees < 225) {
            Shape.EAST_WEST
        } else if (225 <= degrees && degrees < 315) {
            Shape.NORTH_SOUTH
        } else { // 315 <= degrees && degrees < 360
            Shape.EAST_WEST
        }

        val blockPos = blockPosition.toBlockPos()

        println(trackRail(blockPos, instance))

        trackRail(blockPos, instance)
            ?.also { pre ->
                val wh = whereIsFrom(blockPos, pre)
                /*
                if(wh == WhereIsFrom.POSITIVE_X || wh == WhereIsFrom.NEGATIVE_X) {
                    temp = Shape.EAST_WEST
                    instance.setBlock(pre, Block.RAIL
                        .withProperties(buildProperties(Shape.EAST_WEST)))
                }
                if(wh == WhereIsFrom.POSITIVE_Z || wh == WhereIsFrom.NEGATIVE_Z) {
                    temp = Shape.NORTH_SOUTH
                    instance.setBlock(pre, Block.RAIL
                        .withProperties(buildProperties(Shape.NORTH_SOUTH)))
                }

                 */
                /*
                pre.nearBlockPos()
                    .map { Pair(it, instance.getBlock(it)) }
                    .filter { it.second.compare(Block.RAIL) }
                    .forEach { prepre ->
                        println("prpr2: ${prepre.first}")
                    }
                 */
                trackRail(pre, instance, listOf(blockPos), 0)
                    ?.also { prepre ->
                        val prepreWh = whereIsFrom(prepre, pre)
                        println("prpr: $prepre ; wh: $prepreWh")
                        MinecraftServer.getSchedulerManager().scheduleNextTick {
                            if (wh == WhereIsFrom.POSITIVE_X || wh == WhereIsFrom.NEGATIVE_X) {
                                println("test1")
                                if (prepreWh == WhereIsFrom.POSITIVE_Z) {
                                    println("test2")
                                    instance.setBlock(
                                        pre, Block.RAIL
                                            .withProperties(buildProperties(Shape.SOUTH_WEST))
                                    )
                                } else if (prepreWh == WhereIsFrom.NEGATIVE_Z) {
                                    println("test3")
                                    instance.setBlock(
                                        pre, Block.RAIL
                                            .withProperties(buildProperties(Shape.NORTH_WEST))
                                    )
                                }
                            } else if (wh == WhereIsFrom.POSITIVE_Z || wh == WhereIsFrom.NEGATIVE_Z) {
                                println("test4")
                                if (prepreWh == WhereIsFrom.POSITIVE_X) {
                                    println("test5")
                                    instance.setBlock(
                                        pre, Block.RAIL
                                            .withProperties(buildProperties(Shape.SOUTH_EAST))
                                    )
                                } else if (prepreWh == WhereIsFrom.NEGATIVE_X) {
                                    println("test6")
                                    instance.setBlock(
                                        pre, Block.RAIL
                                            .withProperties(buildProperties(Shape.NORTH_EAST))
                                    )
                                }
                            }
                        }
                    }
            }

        return temp
    }
}