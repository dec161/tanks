package ru.aa.BorozdinDuksin.battleTanks.utils

import android.view.View
import ru.aa.BorozdinDuksin.battleTanks.binding
import ru.aa.BorozdinDuksin.battleTanks.models.Coordinate

fun View.checkViewCanMoveThroughBorder(coordinate: Coordinate):Boolean{
    return coordinate.top >= 0 &&
            coordinate.top + this.height <= binding.container.height &&
            coordinate.left >= 0 &&
            coordinate.left + this.width <= binding.container.width
}
