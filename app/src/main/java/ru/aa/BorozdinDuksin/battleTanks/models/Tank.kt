package ru.aa.BorozdinDuksin.battleTanks.models

import android.view.View
import android.widget.FrameLayout
import ru.aa.BorozdinDuksin.battleTanks.CELL_SIZE
import ru.aa.BorozdinDuksin.battleTanks.binding
import ru.aa.BorozdinDuksin.battleTanks.drawers.BulletDrawer
import ru.aa.BorozdinDuksin.battleTanks.drawers.EnemyDrawer
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction
import ru.aa.BorozdinDuksin.battleTanks.enums.Material
import ru.aa.BorozdinDuksin.battleTanks.utils.*
import kotlin.random.Random

class Tank(
    val element: Element,
    var direction: Direction,
    private val enemyDrawer: EnemyDrawer
) {
    fun move(
        direction: Direction,
        container: FrameLayout,
        elementsOnContainer:List<Element>
    ) {
        val view = container.findViewById<View>(element.viewId) ?: return
        val currentCoordinate = view.getViewCoordinate()
        this.direction = direction
        view.rotation = direction.rotation
        val nextCoordinate = getTankNextCoordinate(view)
        if (view.checkViewCanMoveThroughBorder(nextCoordinate)
            && element.checkTankCanMoveThroughMaterial(nextCoordinate, elementsOnContainer)
        ) {
            emulateViewMoving(container, view)
            element.coordinate = nextCoordinate
            generateRandomDirectionForEnemyTank()
        } else {
            element.coordinate = currentCoordinate
            (view.layoutParams as FrameLayout.LayoutParams).topMargin = currentCoordinate.top
            (view.layoutParams as FrameLayout.LayoutParams).leftMargin = currentCoordinate.left
            changeDirectionForEnemyTank()
        }
    }

    private fun generateRandomDirectionForEnemyTank() {
        if (element.material != Material.ENEMY_TANK) return

        if (checkIfChanceBiggerThanRandom(10))
            changeDirectionForEnemyTank()
    }

    private fun changeDirectionForEnemyTank() {
        if (element.material != Material.ENEMY_TANK) return

        val randomDirection = Direction.values()[Random.nextInt(Direction.values().size)]
        this.direction = randomDirection
    }

    private fun emulateViewMoving(container: FrameLayout, view: View) {
        container.runOnUiThread {
            binding.container.removeView(view)
            binding.container.addView(view, 0)
        }
    }

    private fun getTankNextCoordinate(view: View): Coordinate {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        when(direction){
            Direction.UP -> {
                (view.layoutParams as FrameLayout.LayoutParams).topMargin -= CELL_SIZE
            }

            Direction.DOWN ->{
                (view.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE
            }

            Direction.RIGHT ->{
                (view.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE
            }
            Direction.LEFT ->{
                (view.layoutParams as FrameLayout.LayoutParams).leftMargin -= CELL_SIZE
            }

        }

        return Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
    }

    private fun Element.checkTankCanMoveThroughMaterial(
        coordinate: Coordinate,
        elementsOnContainer:List<Element>
    ): Boolean {
        for (anyCoordinate in getTankCoordinates(coordinate)) {
            val element = getElementByCoordinates(coordinate, elementsOnContainer)
                ?: getTankByCoordinates(anyCoordinate, enemyDrawer.tanks)
            if (element != null && !element.material.tankCanGoThrough) {
                if (this == element) continue
                return false
            }
        }
        return true
    }

    private fun getTankCoordinates(topLeftCoordinate: Coordinate):List<Coordinate>{
        val coordinateList = mutableListOf<Coordinate>()
        coordinateList.add(topLeftCoordinate)
        coordinateList.add(Coordinate(topLeftCoordinate.top+ CELL_SIZE,topLeftCoordinate.left))
        coordinateList.add(Coordinate(topLeftCoordinate.top, topLeftCoordinate.left + CELL_SIZE))
        coordinateList.add(
            Coordinate(
                topLeftCoordinate.top + CELL_SIZE,
                topLeftCoordinate.left
            )
        )
        return coordinateList
    }

    private fun getElementByCoordinates(coordinate: Coordinate,elementsOnContainer: List<Element>) =
        elementsOnContainer.firstOrNull { it.coordinate == coordinate }
}
