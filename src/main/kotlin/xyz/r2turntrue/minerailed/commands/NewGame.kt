package xyz.r2turntrue.minerailed.commands

import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction
import de.articdive.jnoise.generators.noise_parameters.interpolation.Interpolation
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator
import de.articdive.jnoise.pipeline.JNoise
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import xyz.r2turntrue.minerailed.dim
import java.util.*
import kotlin.math.roundToInt

object NewGame : Command("newgame") {

    init {
        setDefaultExecutor { sender, _ ->
            sender.sendMessage("Creating instance")
            val instance = MinecraftServer.getInstanceManager().createInstanceContainer(dim)
            val noise = JNoise.newBuilder()
                .perlin(PerlinNoiseGenerator.newBuilder()
                    .setSeed(Random().nextLong())
                    .setInterpolation(Interpolation.COSINE)
                    .setFadeFunction(FadeFunction.IMPROVED_PERLIN_NOISE))
                .build()
            instance.setGenerator { unit ->
                val start = unit.absoluteStart()

                for(x in 0 until unit.size().blockX()) {
                    for(z in 0 until unit.size().blockZ()) {
                        val bottom = start.add(x.toDouble(), 0.0, z.toDouble())

                        synchronized(noise) {
                            unit.modifier().fill(bottom, bottom, Block.STONE)
                            println(noise.evaluateNoise(x.toDouble(), z.toDouble()).roundToInt())
                        }
                    }
                }
            }
            (sender as Player).setInstance(instance)
            sender.teleport(Pos(0.0, 64.0, 0.0))
        }
    }

}