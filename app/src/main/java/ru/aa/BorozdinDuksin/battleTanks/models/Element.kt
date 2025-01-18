package ru.aa.BorozdinDuksin.battleTanks.models

import ru.aa.BorozdinDuksin.battleTanks.enums.Material

data class Element(
    val viewId: Int,
    val material: Material,
    val coordinate: Coordinate
)
