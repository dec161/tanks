package ru.aa.BorozdinDuksin.battleTanks.models

import android.view.View
import ru.aa.BorozdinDuksin.battleTanks.enums.Material

data class Element(
    val viewId: Int = View.generateViewId(),
    val material: Material,
    var coordinate: Coordinate,
    val width: Int = material.width,
    val height: Int = material.height
)
