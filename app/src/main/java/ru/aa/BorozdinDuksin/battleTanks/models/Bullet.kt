package ru.aa.BorozdinDuksin.battleTanks.models

import android.view.View
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction

data class Bullet(
    val view: View,
    val direction: Direction,
    val tank: Tank,
    var canMoveFurther: Boolean = true
)
