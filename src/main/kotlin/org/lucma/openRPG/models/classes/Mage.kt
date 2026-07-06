package org.lucma.openRPG.models.classes

import org.lucma.openRPG.models.PlayerClass
import org.lucma.openRPG.models.conditions.CloseEnemiesCondition
import org.lucma.openRPG.models.conditions.LowHealthCondition
import org.lucma.openRPG.models.conditions.NightTimeCondition
import org.lucma.openRPG.models.data.Modifier
import org.lucma.openRPG.models.effects.CriticalChanceEffect
import org.lucma.openRPG.models.effects.CriticalDamageEffect
import org.lucma.openRPG.models.effects.DamageBonusEffect
import org.lucma.openRPG.models.effects.DefenseBonusEffect
import org.lucma.openRPG.models.effects.FireAuraEffect
import org.lucma.openRPG.models.effects.SpeedBonusEffect

class Mage : PlayerClass() {

    override val id = "mage"
    override val name = "Mago"

    override val modifiers = listOf(
        Modifier(NightTimeCondition(), DamageBonusEffect(0.15)),
        Modifier(NightTimeCondition(), CriticalChanceEffect(0.05)),
        Modifier(LowHealthCondition(), DefenseBonusEffect(0.10)),
        Modifier(LowHealthCondition(), CriticalDamageEffect(0.25)),
        Modifier(CloseEnemiesCondition(), FireAuraEffect(2)),
        Modifier(CloseEnemiesCondition(), SpeedBonusEffect(0.05))
    )
}
