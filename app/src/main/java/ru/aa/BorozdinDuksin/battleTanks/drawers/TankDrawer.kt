package ru.aa.BorozdinDuksin.battleTanks.drawers

import android.view.View
import android.widget.FrameLayout
import ru.aa.BorozdinDuksin.battleTanks.CELL_SIZE
import ru.aa.BorozdinDuksin.battleTanks.binding
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction
import ru.aa.BorozdinDuksin.battleTanks.models.Coordinate
import ru.aa.BorozdinDuksin.battleTanks.models.Element

class TankDrawer(val container: FrameLayout) {
    var currentDirection = Direction.UP

    fun move(myTank: View, direction: Direction,elementsOnContainer:List<Element>){
        val layoutParams = myTank.layoutParams as FrameLayout.LayoutParams
        val currentCoordinate = Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
        currentDirection = direction
        myTank.rotation = direction.rotation
        when(direction){
            Direction.UP -> {
                (myTank.layoutParams as FrameLayout.LayoutParams).topMargin -= CELL_SIZE
            }

            Direction.DOWN ->{
                (myTank.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE
            }

            Direction.RIGHT ->{
                (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE
            }
            Direction.LEFT ->{
                (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin -= CELL_SIZE
            }

        }

        val nextCoordinate = Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
        if (checkTankCanMoveThroughBorder(
                nextCoordinate,
                myTank
            ) && checkTankCanMoveThroughMaterial(nextCoordinate,elementsOnContainer)
        ) {
            binding.container.removeView(myTank)
            binding.container.addView(myTank)
        } else {
            (myTank.layoutParams as FrameLayout.LayoutParams).topMargin = currentCoordinate.top
            (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin = currentCoordinate.left
        }
    }
    private fun checkTankCanMoveThroughMaterial(coordinate: Coordinate,elementsOnContainer:List<Element>): Boolean {
        getTankCoordinates(coordinate).forEach { _ ->
            val element = getElementByCoordinates(coordinate,elementsOnContainer)
            if (element != null && !element.material.tankCanGoThrough) return false
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
    private fun checkTankCanMoveThroughBorder(coordinate: Coordinate, myTank:View):Boolean{
        return coordinate.top >= 0 &&
                coordinate.top + myTank.height <= binding.container.height &&
                coordinate.left >= 0 &&
                coordinate.left + myTank.width <= binding.container.width
    }
    private fun getElementByCoordinates(coordinate: Coordinate,elementsOnContainer: List<Element>) =
        elementsOnContainer.firstOrNull { it.coordinate == coordinate }


}
