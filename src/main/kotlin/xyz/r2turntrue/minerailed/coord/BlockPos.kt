package xyz.r2turntrue.minerailed.coord

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block

data class BlockPos(var blockX: Int, var blockY: Int, var blockZ: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockPos

        if (blockX != other.blockX) return false
        if (blockY != other.blockY) return false
        if (blockZ != other.blockZ) return false

        return true
    }

    override fun hashCode(): Int {
        var result = blockX
        result = 31 * result + blockY
        result = 31 * result + blockZ
        return result
    }

    override fun toString(): String {
        return "BlockPos(blockX=$blockX, blockY=$blockY, blockZ=$blockZ)"
    }
}

fun Point.toBlockPos(): BlockPos {
    return BlockPos(blockX(), blockY(), blockZ())
}

fun BlockPos.toPos(): Pos {
    return Pos(blockX.toDouble(), blockY.toDouble(), blockZ.toDouble())
}

fun Instance.getBlock(pos: BlockPos) =
    this.getBlock(pos.toPos())

fun Instance.setBlock(pos: BlockPos, block: Block) {
    this.setBlock(pos.toPos(), block)
}