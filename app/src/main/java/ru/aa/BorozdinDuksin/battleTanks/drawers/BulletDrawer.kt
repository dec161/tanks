package ru.aa.BorozdinDuksin.battleTanks.drawers

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import ru.aa.BorozdinDuksin.battleTanks.R
import ru.aa.BorozdinDuksin.battleTanks.CELL_SIZE
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction
import ru.aa.BorozdinDuksin.battleTanks.enums.Material
import ru.aa.BorozdinDuksin.battleTanks.models.Coordinate
import ru.aa.BorozdinDuksin.battleTanks.models.Element
import ru.aa.BorozdinDuksin.battleTanks.models.Tank
import ru.aa.BorozdinDuksin.battleTanks.utils.checkViewCanMoveThroughBorder
import ru.aa.BorozdinDuksin.battleTanks.utils.getElementByCoordinates
import ru.aa.BorozdinDuksin.battleTanks.utils.getTankByCoordinates
import ru.aa.BorozdinDuksin.battleTanks.utils.runOnUiThread

private const val BULLET_HEIGHT = 15
private const val BULLET_WIDTH = 15

class BulletDrawer(
    private val container: FrameLayout,
    private val elements: MutableList<Element>,
    val enemyDrawer: EnemyDrawer
) {
    private var canBulletGoFurther = true
    private var bulletThread: Thread? = null
    private lateinit var tank: Tank

    private fun checkBulletThreadLive() = bulletThread != null && bulletThread!!.isAlive

    fun makeBulletMove(tank: Tank) {
        canBulletGoFurther = true
        this.tank = tank
        val currentDirection = tank.direction
        if (!checkBulletThreadLive()) {
            bulletThread = Thread(Runnable {
                val view = container.findViewById<View>(this.tank.element.viewId) ?: return@Runnable
                val bullet = createBullet(view, currentDirection)
                while (bullet.checkViewCanMoveThroughBorder(
                        Coordinate(bullet.top, bullet.left)
                    ) && canBulletGoFurther
                ) {
                    when (currentDirection) {
                        Direction.UP -> (bullet.layoutParams as FrameLayout.LayoutParams).topMargin -= BULLET_HEIGHT
                        Direction.DOWN -> (bullet.layoutParams as FrameLayout.LayoutParams).topMargin += BULLET_HEIGHT
                        Direction.LEFT -> (bullet.layoutParams as FrameLayout.LayoutParams).leftMargin -= BULLET_HEIGHT
                        Direction.RIGHT -> (bullet.layoutParams as FrameLayout.LayoutParams).leftMargin += BULLET_HEIGHT
                    }
                    Thread.sleep(30)
                    chooseBehaviourInTermsOfDirections(
                        currentDirection,
                        Coordinate(
                            (bullet.layoutParams as FrameLayout.LayoutParams).topMargin,
                            (bullet.layoutParams as FrameLayout.LayoutParams).leftMargin
                        )
                    )
                    container.runOnUiThread {
                        container.removeView(bullet)
                        container.addView(bullet)
                    }
                }
                container.runOnUiThread {
                    container.removeView(bullet)
                }
            })
            bulletThread!!.start()
        }
    }

    private fun chooseBehaviourInTermsOfDirections(
        currentDirection: Direction,
        bulletCoordinate: Coordinate
    ) {
        when (currentDirection) {
            Direction.DOWN, Direction.UP -> {
                compareCollections(getCoordinatesForTopOrBottomDirection(bulletCoordinate))
            }

            Direction.LEFT, Direction.RIGHT -> {
                compareCollections(getCoordinatesForLeftOrRightDirection(bulletCoordinate))
            }
        }
    }

    private fun compareCollections(detectedCoordinatesList: List<Coordinate>) {
        for (coordinate in detectedCoordinatesList) {
            val element = getElementByCoordinates(coordinate, elements)
                ?: getTankByCoordinates(coordinate, enemyDrawer.tanks)
            //if (element == null) element = getTankByCoordinates(coordinate, enemyDrawer.tanks)
            if (element == tank.element) continue

            removeElementsAndStopBullet(element)
        }
    }

    private fun removeElementsAndStopBullet(element: Element?) {
        if (element == null || element.material.bulletCanGoThrough) return

        stopBullet()

        if (tank.element.material == Material.ENEMY_TANK && element.material == Material.ENEMY_TANK)
            return

        if (element.material.simpleBulletCanDestroy) {
            removeView(element)
            elements.remove(element)
            removeTank(element)
        }
    }

    private fun removeTank(element: Element) {
        val tanksElements = enemyDrawer.tanks.map { it.element }
        val tankIndex = tanksElements.indexOf(element)
        enemyDrawer.removeTank(tankIndex)
    }

    private fun stopBullet() {
        canBulletGoFurther = false
    }

    private fun removeView(element: Element?) {
        val activity = container.context as Activity
        activity.runOnUiThread {
            if (element != null) {
                container.removeView(activity.findViewById(element.viewId))
            }
        }
    }

    private fun getCoordinatesForTopOrBottomDirection(bulletCoordinate: Coordinate): List<Coordinate> {
        val leftCell = bulletCoordinate.left - bulletCoordinate.left % CELL_SIZE
        val rightCell = leftCell + CELL_SIZE
        val topCoordinate = bulletCoordinate.top - bulletCoordinate.top % CELL_SIZE
        return listOf(
            Coordinate(topCoordinate, leftCell),
            Coordinate(topCoordinate, rightCell)
        )
    }

    private fun getCoordinatesForLeftOrRightDirection(bulletCoordinate: Coordinate): List<Coordinate> {
        val topCell = bulletCoordinate.top - bulletCoordinate.top % CELL_SIZE
        val bottomCell = topCell + CELL_SIZE
        val leftCoordinate = bulletCoordinate.left - bulletCoordinate.left % CELL_SIZE
        return listOf(
            Coordinate(topCell, leftCoordinate),
            Coordinate(bottomCell, leftCoordinate)
        )
    }

    fun createBullet(myTank: View, currentDirection: Direction): ImageView {
        return ImageView(container.context)
            .apply {
                this.setImageResource(R.drawable.bullet)
                this.layoutParams = FrameLayout.LayoutParams(BULLET_WIDTH, BULLET_HEIGHT)
                val bulletCoordinate = getBulletCoordinates(this, myTank, currentDirection)
                (this.layoutParams as FrameLayout.LayoutParams).topMargin = bulletCoordinate.top
                (this.layoutParams as FrameLayout.LayoutParams).leftMargin = bulletCoordinate.left
                this.rotation = currentDirection.rotation
            }
    }

    private fun getBulletCoordinates(
        bullet: ImageView,
        myTank: View,
        currentDirection: Direction
    ): Coordinate {
        val tankLeftTopCoordinate = Coordinate(myTank.top, myTank.left)

       when(currentDirection){
            Direction.UP -> {
                return Coordinate(
                    top = tankLeftTopCoordinate.top - bullet.layoutParams.height,
                    left = getDistanceToMiddleOfTanks(tankLeftTopCoordinate.left, bullet.layoutParams.width))
        }

            Direction.DOWN -> {
                return Coordinate(
                    top = tankLeftTopCoordinate.top + myTank.layoutParams.height,
                    left = getDistanceToMiddleOfTanks(tankLeftTopCoordinate.left, bullet.layoutParams.width))
            }
            Direction.LEFT -> {
                return Coordinate(
                    top = getDistanceToMiddleOfTanks(tankLeftTopCoordinate.top, bullet.layoutParams.height),
                    left = tankLeftTopCoordinate.left  - bullet.layoutParams.width)
            }
            Direction.RIGHT -> {
                return Coordinate(
                    top = getDistanceToMiddleOfTanks(tankLeftTopCoordinate.top, bullet.layoutParams.height),
                    left = tankLeftTopCoordinate.left  + myTank.layoutParams.width)
            }
        }

        return tankLeftTopCoordinate
    }
    private fun getDistanceToMiddleOfTanks(startCoordinate: Int, bulletSize: Int):Int {
        return startCoordinate + (CELL_SIZE - bulletSize/2)
    }
}