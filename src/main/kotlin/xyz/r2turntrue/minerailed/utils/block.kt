package xyz.r2turntrue.minerailed.utils

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos

fun Point.nearBlockPos(): List<Pos> =
    listOf(
        Pos((blockX() - 0).toDouble(), blockY().toDouble(), (blockZ() - 1).toDouble()),
        Pos((blockX() - 1).toDouble(), blockY().toDouble(), (blockZ() - 0).toDouble()), Pos((blockX() + 1).toDouble(), blockY().toDouble(), (blockZ() - 0).toDouble()),
        Pos((blockX() - 0).toDouble(), blockY().toDouble(), (blockZ() - 1).toDouble())
    )