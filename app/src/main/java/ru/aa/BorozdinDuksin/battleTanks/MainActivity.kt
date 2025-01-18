package ru.aa.BorozdinDuksin.battleTanks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_DPAD_UP
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_RIGHT
import android.view.KeyEvent.KEYCODE_DPAD_LEFT
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import android.widget.FrameLayout
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction.DOWN
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction.UP
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction.RIGHT
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction.LEFT
import ru.aa.BorozdinDuksin.battleTanks.databinding.ActivityMainBinding
import ru.aa.BorozdinDuksin.battleTanks.drawers.ElementsDrawer
import ru.aa.BorozdinDuksin.battleTanks.drawers.GridDrawer
import ru.aa.BorozdinDuksin.battleTanks.enums.Direction
import ru.aa.BorozdinDuksin.battleTanks.enums.Material
import ru.aa.BorozdinDuksin.battleTanks.models.Coordinate


const val CELL_SIZE = 50

lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var editMode =false
    private val gridDrawer by lazy {
        GridDrawer(binding.container)
    }

    private val elementsDrawer by lazy {
        ElementsDrawer(binding.container)
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
        binding.container.setOnTouchListener { _, event ->
            elementsDrawer.onTouchContainer(event.x, event.y)
            return@setOnTouchListener true
        }
    }

    private fun switchEditMode (){
        if(editMode){
            gridDrawer.removeGrid()
            binding.materialsContainer.visibility = INVISIBLE

        }
        else{
            gridDrawer.drawGrid()
            binding.materialsContainer.visibility = VISIBLE
        }
        editMode =!editMode
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
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KEYCODE_DPAD_UP -> elementsDrawer.move(binding.myTank,UP)
            KEYCODE_DPAD_UP -> elementsDrawer.move(binding.myTank,DOWN)
            KEYCODE_DPAD_UP -> elementsDrawer.move(binding.myTank,RIGHT)
            KEYCODE_DPAD_UP -> elementsDrawer.move(binding.myTank,LEFT)
        }
        return super.onKeyDown(keyCode, event)
    }


}
