package com.possible_triangle.create_jetpack.item

import com.possible_triangle.create_jetpack.Content.JETPACK_CAPABILITY
import com.possible_triangle.create_jetpack.capability.IJetpack
import com.possible_triangle.create_jetpack.capability.IJetpack.Context
import com.possible_triangle.create_jetpack.config.Configs
import com.simibubi.create.content.curiosities.armor.BackTankUtil
import com.simibubi.create.content.curiosities.armor.CopperBacktankItem
import com.simibubi.create.repack.registrate.util.entry.ItemEntry
import net.minecraft.core.Direction
import net.minecraft.world.item.Rarity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional

class BronzeJetpack(properties: Properties, blockItem: ItemEntry<CopperBacktankBlockItem>) :
    CopperBacktankItem(properties.rarity(Rarity.RARE), blockItem), IJetpack, ICapabilityProvider {
    private val capability = LazyOptional.of<IJetpack> { this }

    override fun hoverSpeed(context: Context): Double {
        return Configs.SERVER.HOVER_SPEED.get()
    }

    override fun verticalSpeed(context: Context): Double {
        return Configs.SERVER.VERTICAL_SPEED.get()
    }

    override fun activeType(context: Context): ControlType {
        return ControlType.TOGGLE
    }

    override fun hoverType(context: Context): ControlType {
        return ControlType.TOGGLE
    }

    override fun horizontalSpeed(context: Context): Double {
        return Configs.SERVER.HORIZONTAL_SPEED.get()
    }

    override fun acceleration(context: Context): Double {
        return Configs.SERVER.ACCELERATION.get()
    }

    override fun swimModifier(context: Context): Double {
        return Configs.SERVER.SWIM_MODIFIER.get()
    }

    private val thrusters = listOf(-0.35, 0.35).map { offset ->
        Vec3(offset, 0.7, -0.5)
    }

    override fun getThrusters(context: Context) = thrusters

    override fun onUse(context: Context) {
        if (!isThrusting(context)) return
        BackTankUtil.canAbsorbDamage(context.entity, usesPerTank(context))
    }

    private fun usesPerTank(context: Context): Int {
        return if (isHovering(context)) Configs.SERVER.USES_PER_TANK_HOVER.get()
        else Configs.SERVER.USES_PER_TANK.get()
    }

    override fun isUsable(context: Context): Boolean {
        val tank = BackTankUtil.get(context.entity)
        if (tank.isEmpty) return false
        val air = BackTankUtil.getAir(tank)
        val cost = BackTankUtil.maxAirWithoutEnchants() / usesPerTank(context)
        return air >= cost
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == JETPACK_CAPABILITY) return capability.cast()
        return LazyOptional.empty()
    }

    enum class ControlType {
        ALWAYS, NEVER, TOGGLE
    }

}