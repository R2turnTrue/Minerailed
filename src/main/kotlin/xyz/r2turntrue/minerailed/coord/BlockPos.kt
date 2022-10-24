package xyz.r2turntrue.minerailed.coord

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos

data class BlockPos(var blockX: Int, var blockY: Int, var blockZ: Int)

fun Point.toBlockPos(): BlockPos {
    return BlockPos(blockX(), blockY(), blockZ())
}

fun BlockPos.toPos(): Pos {
    return Pos(blockX.toDouble(), blockY.toDouble(), blockZ.toDouble())
}