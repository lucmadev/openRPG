package org.lucma.openRPG.models.classes

import org.lucma.openRPG.models.PlayerClass
import org.lucma.openRPG.models.conditions.CloseEnemiesCondition
import org.lucma.openRPG.models.data.Modifier
import org.lucma.openRPG.models.effects.CriticalChanceEffect
import org.lucma.openRPG.models.effects.CriticalDamageEffect
import org.lucma.openRPG.models.effects.DamageBonusEffect
import org.lucma.openRPG.models.effects.DefenseBonusEffect
import org.lucma.openRPG.models.effects.SpeedBonusEffect

class Warrior : PlayerClass() {

    override val id = "warrior"
    override val name = "Guerrero"

    override val modifiers = listOf(
        Modifier(CloseEnemiesCondition(), DamageBonusEffect(0.10)),
        Modifier(CloseEnemiesCondition(), DefenseBonusEffect(0.05)),
        Modifier(CloseEnemiesCondition(), SpeedBonusEffect(0.05)),
        Modifier(CloseEnemiesCondition(), CriticalChanceEffect(0.03)),
        Modifier(CloseEnemiesCondition(), CriticalDamageEffect(0.20))
    )
}
