package ru.aa.BorozdinDuksin.battleTanks.drawers

import android.widget.FrameLayout
import ru.aa.BorozdinDuksin.battleTanks.CELL_SIZE
//import ru.aa.BorozdinDuksin.battleTanks.binding
import ru.aa.BorozdinDuksin.battleTanks.enums.CELLS_TANKS_SIZE
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction
import ru.aa.BorozdinDuksin.battleTanks.enums.Material.ENEMY_TANK
import ru.aa.BorozdinDuksin.battleTanks.models.Coordinate
import ru.aa.BorozdinDuksin.battleTanks.models.Element
import ru.aa.BorozdinDuksin.battleTanks.models.Tank
import ru.aa.BorozdinDuksin.battleTanks.utils.checkIfChanceBiggerThanRandom
import ru.aa.BorozdinDuksin.battleTanks.utils.drawElement

private const val MAX_ENEMY_AMOUNT = 20

class EnemyDrawer(
    private val container: FrameLayout,
    private val elements: MutableList<Element>,
) {
    private val respawnList: List<Coordinate>
    private var enemyAmount = 0
    private var currentCoordinate: Coordinate
    private var moveAllTanksThread: Thread? = null
    val tanks = mutableListOf<Tank>()
    lateinit var bulletDrawer: BulletDrawer

    init {
        respawnList = getRespawnList()
        currentCoordinate = respawnList[0]
    }

    private fun getRespawnList(): List<Coordinate> {
        val respawnList = mutableListOf<Coordinate>()
        respawnList.add(Coordinate(0, 0))
        respawnList.add(
            Coordinate(
                0,
                ((container.width - container.width % CELL_SIZE) / CELL_SIZE -
                        (container.width - container.width % CELL_SIZE) / CELL_SIZE % 2) *
                        CELL_SIZE / 2 - CELL_SIZE * CELLS_TANKS_SIZE
            )
        )
        respawnList.add(
            Coordinate(
                0,
                (container.width - container.width % CELL_SIZE) - CELL_SIZE * CELLS_TANKS_SIZE
            )
        )
        return respawnList
    }

    private fun drawEnemy() {
        var index = respawnList.indexOf(currentCoordinate) + 1
        if (index == respawnList.size) {
            index = 0
        }
        currentCoordinate = respawnList[index]
        val enemyTank = Tank(
            Element(
            material = ENEMY_TANK,
            coordinate = currentCoordinate
            ), Direction.DOWN,
            this
        )
        enemyTank.element.drawElement(container)
        tanks.add(enemyTank)
    }

    fun moveEnemyTanks() {
        Thread(Runnable {
            while (true) {
                goThroughAllTanks()
                Thread.sleep(400)
            }
        }).start()
    }

    private fun goThroughAllTanks() {
        moveAllTanksThread = Thread(Runnable {
            tanks.forEach {
                it.move(it.direction, container, elements)
                if (checkIfChanceBiggerThanRandom(10))
                    bulletDrawer.addNewBulletForTank(it)
            }
        })
        moveAllTanksThread?.start()
    }

    fun startEnemyCreation() {
        Thread(Runnable {
            while (enemyAmount < MAX_ENEMY_AMOUNT) {
                drawEnemy()
                enemyAmount++
                Thread.sleep(3000)
            }
        })
    }

    fun removeTank(tankIndex: Int) {
        if (tankIndex < 0) return

        moveAllTanksThread?.join()
        tanks.removeAt(tankIndex)
    }
}
