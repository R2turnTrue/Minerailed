package xyz.r2turntrue.minerailed.commands

import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import xyz.r2turntrue.minerailed.dim
import kotlin.random.Random

object NewGame : Command("newgame") {

    init {
        setDefaultExecutor { sender, _ ->
            sender.sendMessage("Creating instance")
            val instance = MinecraftServer.getInstanceManager().createInstanceContainer(dim)
            val layers = arrayOf(
                newNoise(Random.nextInt(), 0.1F), // water
                newNoise(Random.nextInt(), 0.3F), // stone
                newNoise(Random.nextInt(), 0.13F), // iron
                newNoise(Random.nextInt(), 0.13F), // trees
            )
            instance.setGenerator { unit ->
                val start = unit.absoluteStart()
                val size = unit.size()
                if(unit.absoluteStart().blockX() / 16 != 0)
                    return@setGenerator
                for (x in 0 until size.blockX()) {
                    for (z in 0 until size.blockZ()) {
                        val bottom = start.add(x.toDouble(), 0.0, z.toDouble())

                        // surface building
                        val c = layers[0].GetNoise(bottom.x().toFloat(), bottom.z().toFloat()) + 1.0F
                        //println("${bottom.x()} ; ${bottom.z()} = ${noise.GetNoise(bottom.x().toFloat(), bottom.z().toFloat()) + 1.0F}")
                        var waterBuilded = false
                        if(c < 0.6) {
                            unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 0.0 + 2, z.toDouble()), Block.BARRIER)
                            unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 0.0 + 1, z.toDouble()), Block.BARRIER)
                            unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 0.0, z.toDouble()), Block.WATER)
                            unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 0.0 - 1, z.toDouble()), Block.SAND)
                            waterBuilded = true
                        } else {
                            unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 0.0, z.toDouble()), Block.GRASS_BLOCK)
                        }

                        if(!waterBuilded) {
                            val stoneNoise = layers[1].GetNoise(bottom.x().toFloat(), bottom.z().toFloat()) + 1.0F
                            val ironNoise = layers[2].GetNoise(bottom.x().toFloat(), bottom.z().toFloat()) + 1.0F
                            val treeNoise = layers[3].GetNoise(bottom.x().toFloat(), bottom.z().toFloat()) + 1.0F

                            if(stoneNoise < 0.4) {
                                unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 1.0, z.toDouble()), Block.STONE)
                                unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 2.0, z.toDouble()), Block.STONE)
                            } else if(ironNoise < 0.5) {
                                unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 2.0, z.toDouble()), Block.BARRIER)
                                unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 1.0, z.toDouble()), Block.IRON_BLOCK)
                            } else if(treeNoise < 0.5) {
                                unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 2.0, z.toDouble()), Block.BARRIER)
                                unit.modifier().setBlock(start.add(x.toDouble(), Math.min(40 - start.blockY(), size.blockY()) + 1.0, z.toDouble()), Block.OAK_LOG)
                            }
                        }
                    }
                }
            }
            (sender as Player).setInstance(instance)
            sender.teleport(Pos(0.0, 64.0, 0.0))
        }
    }

}