package org.lucma.openRPG.models.classes

import org.lucma.openRPG.models.PlayerClass
import org.lucma.openRPG.models.conditions.LowHealthCondition
import org.lucma.openRPG.models.conditions.SneakingCondition
import org.lucma.openRPG.models.data.Modifier
import org.lucma.openRPG.models.effects.CriticalChanceEffect
import org.lucma.openRPG.models.effects.CriticalDamageEffect
import org.lucma.openRPG.models.effects.DamageBonusEffect
import org.lucma.openRPG.models.effects.LifeStealEffect
import org.lucma.openRPG.models.effects.SpeedBonusEffect

class Assassin : PlayerClass() {

    override val id = "assassin"
    override val name = "Asesino"

    override val modifiers = listOf(
        Modifier(SneakingCondition(), DamageBonusEffect(0.20)),
        Modifier(SneakingCondition(), CriticalChanceEffect(0.08)),
        Modifier(SneakingCondition(), CriticalDamageEffect(0.30)),
        Modifier(SneakingCondition(), SpeedBonusEffect(0.10)),
        Modifier(LowHealthCondition(), LifeStealEffect(0.05))
    )
}
