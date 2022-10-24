package xyz.r2turntrue.minerailed.rules.placement

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.instance.block.rule.BlockPlacementRule
import xyz.r2turntrue.minerailed.coord.BlockPos
import xyz.r2turntrue.minerailed.coord.toBlockPos
import xyz.r2turntrue.minerailed.coord.toPos
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

        blockPosition.nearBlockPos()
            .map { Pair(it.toBlockPos(), instance.getBlock(it)) }
            .firstOrNull { it.second.compare(Block.RAIL) }
            ?.also {
                val xOff = blockPos.blockX - it.first.blockX
                val zOff = blockPos.blockZ - it.first.blockZ

                if(abs(xOff) == 1) {
                    temp = Shape.EAST_WEST
                    if(xOff == 1) {
                        instance.setBlock(blockPosition.sub(1.0, 0.0, 0.0), Block.RAIL.withProperties(
                            buildProperties(Shape.EAST_WEST)
                        ))
                    } else if(xOff == -1) {
                        instance.setBlock(blockPosition.sub(-1.0, 0.0, 0.0), Block.RAIL.withProperties(
                            buildProperties(Shape.EAST_WEST)
                        ))
                    }
                } else {
                    temp = Shape.NORTH_SOUTH
                    if(zOff == 1) {
                        instance.setBlock(blockPosition.sub(0.0, 0.0, 1.0), Block.RAIL.withProperties(
                            buildProperties(Shape.NORTH_SOUTH)
                        ))
                    } else if(zOff == -1) {
                        instance.setBlock(blockPosition.sub(0.0, 0.0, -1.0), Block.RAIL.withProperties(
                            buildProperties(Shape.NORTH_SOUTH)
                        ))
                    }
                }

                // check there's curve in previous rail
                if(xOff == -1) { /* Left/Right in z as down */
                    checkCurve(player, it.first, instance, -1, 0)
                } else if (xOff == 1) {
                    checkCurve(player, it.first, instance, 1, 0)
                } else if (zOff == -1) { /* Up/Down */
                    checkCurve(player, it.first, instance, 0, -1)
                } else if (zOff == 1) {
                    checkCurve(player, it.first, instance, 0, 1)
                }
            }

        return temp
    }

    private fun checkCurve(player: Player, previousBlockPosition: BlockPos, instance: Instance, xOff: Int, zOff: Int) {
        val pos = previousBlockPosition.toPos()
        previousBlockPosition.toPos().nearBlockPos()
            .map { it.toBlockPos() }
            .firstOrNull() // find preprevious rail
            ?.also { preprevious ->
                if(abs(zOff) == 1) { // xOff == 0 have difference on Z?
                    if(previousBlockPosition.blockX - preprevious.blockX == 1) {
                        instance.setBlock(pos, Block.RAIL.withProperties(
                            buildProperties(Shape.SOUTH_EAST)
                        ))
                    } else if(previousBlockPosition.blockX - preprevious.blockX == -1) {
                        instance.setBlock(pos, Block.RAIL.withProperties(
                            buildProperties(Shape.SOUTH_WEST)
                        ))
                    }
                } else if(abs(xOff) == 1) { // zOff == 0 have difference on X?
                    if(previousBlockPosition.blockZ - preprevious.blockZ == 1) {
                        instance.setBlock(pos, Block.RAIL.withProperties(
                            buildProperties(Shape.NORTH_EAST)
                        ))
                    } else if(previousBlockPosition.blockZ - preprevious.blockZ == -1) {
                        instance.setBlock(pos, Block.RAIL.withProperties(
                            buildProperties(Shape.NORTH_WEST)
                        ))
                    }
                }
            }
    }
}