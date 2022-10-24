package xyz.r2turntrue.minerailed.commands

import net.minestom.server.command.builder.Command
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.minecart.MinecartMeta
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

object Minecart: Command("minecartdebug") {

    init {
        // todo: cart follow rail
        setDefaultExecutor { sender, _ ->
            (sender as Player).inventory.addItemStack(ItemStack.of(Material.RAIL, 64))
            sender.instance?.setBlock(sender.position.blockX(), sender.position.blockY(), sender.position.blockZ(), Block.RAIL)
            val minecart = Entity(EntityType.MINECART)
            minecart.setInstance(sender.instance!!, Pos(
                sender.position.blockX().toDouble() + 0.5,
                sender.position.blockY().toDouble(),
                sender.position.blockZ().toDouble() + 0.5
            ))
        }
    }

}