package xyz.r2turntrue.minerailed.utils

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import xyz.r2turntrue.minerailed.coord.BlockPos

fun Point.nearBlockPos(): List<Pos> =
    listOf(
        Pos((blockX() - 0).toDouble(), blockY().toDouble(), (blockZ() - 1).toDouble()),
        Pos((blockX() - 1).toDouble(), blockY().toDouble(), (blockZ() - 0).toDouble()), Pos((blockX() + 1).toDouble(), blockY().toDouble(), (blockZ() - 0).toDouble()),
        Pos((blockX() - 0).toDouble(), blockY().toDouble(), (blockZ() - 1).toDouble())
    )

fun BlockPos.nearBlockPos(yOff: Int): List<BlockPos> =
    listOf(
        BlockPos(this.blockX, this.blockY + 1, this.blockZ + 1),
        BlockPos(this.blockX - 1, this.blockY + yOff, this.blockZ), BlockPos(this.blockX + 1, this.blockY + yOff, this.blockZ),
        BlockPos(this.blockX, this.blockY + yOff, this.blockZ - 1)
    )