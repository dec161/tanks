package ru.aa.BorozdinDuksin.battleTanks.drawers

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import ru.aa.BorozdinDuksin.battleTanks.CELL_SIZE
import ru.aa.BorozdinDuksin.battleTanks.enums.Material
import ru.aa.BorozdinDuksin.battleTanks.models.Coordinate
import ru.aa.BorozdinDuksin.battleTanks.R
import ru.aa.BorozdinDuksin.battleTanks.binding
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction
import ru.aa.BorozdinDuksin.battleTanks.models.Element

class ElementsDrawer(val container: FrameLayout) {
    var currentMaterial = Material.EMPTY
    private val elementsOnContainer = mutableListOf<Element>()

    fun onTouchContainer(x: Float, y: Float) {
        val topMargin = y.toInt() - (y.toInt() % CELL_SIZE)
        val leftMargin = x.toInt() - (x.toInt() % CELL_SIZE)
        val coordinate = Coordinate(topMargin, leftMargin)
        drawView(coordinate)
    }
     fun move(myTank: View, direction: Direction){
         val layoutParams = myTank.layoutParams as FrameLayout.LayoutParams
         val currentCoordinate = Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
        when(direction){
            Direction.UP ->{
                myTank.rotation = 0f

                (myTank.layoutParams as FrameLayout.LayoutParams).topMargin -= CELL_SIZE

            }

            Direction.DOWN ->{
                myTank.rotation = 180f

                (myTank.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE

            }

            Direction.RIGHT ->{
                myTank.rotation = 90f

                (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE

            }
            Direction.LEFT ->{
                myTank.rotation = 270f

                (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin -= CELL_SIZE

            }

        }

        val nextCoordinate = Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
         if (checkTankCanMoveThroughBorder(
                 nextCoordinate,
                 myTank
             ) && checkTankCanMoveThroughMaterial(nextCoordinate)
         ) {
             binding.container.removeView(myTank)
             binding.container.addView(myTank)
         } else {
             (myTank.layoutParams as FrameLayout.LayoutParams).topMargin = currentCoordinate.top
             (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin = currentCoordinate.left
         }
    }

    private fun checkTankCanMoveThroughBorder(coordinate: Coordinate, myTank:View):Boolean{
        return (coordinate.top >0 &&
            coordinate.top +myTank.height < binding.container.height / CELL_SIZE* CELL_SIZE &&
            coordinate.left >0 &&
            coordinate.left + myTank.width < binding.container.width / CELL_SIZE * CELL_SIZE

                )
    }

    private fun getElementByCoordinates(coordinate: Coordinate) =
        elementsOnContainer.firstOrNull { it.coordinate == coordinate }

    private fun checkTankCanMoveThroughMaterial(coordinate: Coordinate): Boolean {
        getTankCoordinates(coordinate).forEach {
            val element = getElementByCoordinates(coordinate)
            if (element != null) return false
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

    fun drawView(coordinate: Coordinate) {
        val view = ImageView(container.context)
        val layoutParams = FrameLayout.LayoutParams(CELL_SIZE, CELL_SIZE)
        when (currentMaterial) {
            Material.EMPTY -> { }
            Material.BRICK -> view.setImageResource(R.drawable.brick)
            Material.CONCRETE -> view.setImageResource(R.drawable.concrete)
            Material.GRASS -> view.setImageResource(R.drawable.grass)
        }
        layoutParams.topMargin = coordinate.top
        layoutParams.leftMargin = coordinate.left
        val viewId = View.generateViewId()
        view.id = viewId
        view.layoutParams = layoutParams
        container.addView(view)
        elementsOnContainer.add(Element(viewId, currentMaterial, coordinate))
    }
}
