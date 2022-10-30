package xyz.r2turntrue.minerailed.commands

import fastnoise.FastNoiseLite
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player
import kotlin.random.Random

object Noise : Command("noise") {
    init {
        setDefaultExecutor { sender, _ ->
            val pos = (sender as Player).position
            val noise = newNoise(Random.nextInt(), 0.025F)
            sender.sendMessage("${noise.GetNoise(pos.x.toFloat(), pos.y.toFloat())}")
        }
    }
}

fun newNoise(seed: Int, freq: Float): FastNoiseLite =
    FastNoiseLite().apply {
        SetSeed(seed)
        SetNoiseType(FastNoiseLite.NoiseType.Perlin)
        SetFractalType(FastNoiseLite.FractalType.None)
        SetFrequency(freq)
    }