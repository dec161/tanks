package ru.aa.BorozdinDuksin.battleTanks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction.DOWN
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction.UP
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction.RIGHT
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction.LEFT
import ru.aa.BorozdinDuksin.battleTanks.databinding.ActivityMainBinding
import ru.aa.BorozdinDuksin.battleTanks.drawers.*
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction
import ru.aa.BorozdinDuksin.battleTanks.enums.Material
import ru.aa.BorozdinDuksin.battleTanks.models.Coordinate
import ru.aa.BorozdinDuksin.battleTanks.models.Element
import ru.aa.BorozdinDuksin.battleTanks.models.Tank

const val CELL_SIZE = 50

lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var editMode =false

    private val playerTank = Tank(
        Element(
            R.id.myTank,
            Material.PLAYER_TANK,
            Coordinate(0, 0),
            Material.PLAYER_TANK.width,
            Material.PLAYER_TANK.height
        ), UP
    )

    private val gridDrawer by lazy {
        GridDrawer(binding.container)
    }

    private val elementsDrawer by lazy {
        ElementsDrawer(binding.container)
    }

    private val bulletDrawer by lazy {
        BulletDrawer(binding.container)
    }

    private val levelStorage by lazy {
        LevelStorage (this)
    }

    private val enemyDrawer by lazy {
        EnemyDrawer(binding.container, elementsDrawer.elementsOnContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Menu"

        binding.editorClear.setOnClickListener { elementsDrawer.currentMaterial = Material.EMPTY }
        binding.editorBrick.setOnClickListener { elementsDrawer.currentMaterial = Material.BRICK }
        binding.editorConcrete.setOnClickListener {
            elementsDrawer.currentMaterial = Material.CONCRETE
        }
        binding.editorGrass.setOnClickListener { elementsDrawer.currentMaterial = Material.GRASS }
        binding.editorEagle.setOnClickListener { elementsDrawer.currentMaterial = Material.EAGLE }

        binding.container.setOnTouchListener { _, event ->
            elementsDrawer.onTouchContainer(event.x, event.y)
            return@setOnTouchListener true
        }
        elementsDrawer.drawElementsList(levelStorage.loadLevel())
        hideSettings()
        elementsDrawer.elementsOnContainer.add(playerTank.element)
    }

    private fun switchEditMode() {
        editMode = !editMode
        if (editMode) {
            showSettings()
        } else {
            hideSettings()
        }
    }

    private fun showSettings() {
        gridDrawer.drawGrid()
        binding.materialsContainer.visibility = VISIBLE
    }

    private fun hideSettings() {
        gridDrawer.removeGrid()
        binding.materialsContainer.visibility = INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                gridDrawer.drawGrid()
                switchEditMode()
                true
            }

            R.id.menu_save -> {
                levelStorage.saveLevel(elementsDrawer.elementsOnContainer)
                true
            }

            R.id.menu_play -> {
                startTheGame()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startTheGame() {
        if (editMode) return

        enemyDrawer.startEnemyCreation()
        enemyDrawer.moveEnemyTanks()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KEYCODE_DPAD_UP -> move(UP)
            KEYCODE_DPAD_DOWN -> move(DOWN)
            KEYCODE_DPAD_RIGHT -> move(RIGHT)
            KEYCODE_DPAD_LEFT -> move(LEFT)
            KEYCODE_SPACE -> bulletDrawer.makeBulletMove(binding.myTank, playerTank.direction, elementsDrawer.elementsOnContainer)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun move(direction: Direction) {
        playerTank.move(direction, binding.container, elementsDrawer.elementsOnContainer)
    }
}
