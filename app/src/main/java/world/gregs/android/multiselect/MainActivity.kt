package world.gregs.android.multiselect

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import world.gregs.android.multiselect.Model.Companion.models

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    Adapter.AdapterRowListener {

    private var adapter: Adapter? = null
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Create button
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            addData()
        }

        //Drawer
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        //List
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = Adapter(this)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
            adapter = this@MainActivity.adapter
        }
        actionModeCallback = ActionModeCallback(recyclerView)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
            }
            R.id.nav_gallery -> {
            }
            R.id.nav_slideshow -> {
            }
            R.id.nav_tools -> {
            }
            R.id.nav_share -> {
            }
            R.id.nav_send -> {
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRowAction(position: Int) {
        val model = models[position]
        models[position] = model
        adapter?.notifyDataSetChanged()

        Toast.makeText(applicationContext, "Clicked: ${model.id}", Toast.LENGTH_SHORT).show()
    }

    override fun onRowSelection(position: Int) {
        toggleSelection(position)
    }

    override fun onRowLongClicked(position: Int) {
        //Enable action mode on long press
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback!!)
        }
        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {
        adapter?.apply {
            toggleSelection(position)
            val count = selectionCount()

            actionMode?.apply {
                if (count == 0) {
                    finish()
                } else {
                    title = count.toString()
                    invalidate()
                }
            }
        }
    }

    private inner class ActionModeCallback(private val view: View) : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.delete -> deleteSelection(view)
                else -> return false
            }
            mode.finish()
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            adapter!!.clearSelections()
            actionMode = null
        }
    }

    private fun addData() {
        models.add(Model("Item", "Description"))
        adapter?.notifyItemChanged(models.size)
    }

    private fun deleteSelection(view: View) {
        adapter?.apply {
            clearBackup()
            val items = getSelectedItems()
            for (i in items.indices.reversed()) {
                removeData(items[i])
            }
            val count = items.size
            notifyDataSetChanged()
            //Notify user
            Snackbar.make(view, "Item${if (count > 1) "s" else ""} deleted.", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    //Undo deletion
                    restoreSelection(view)
                }.show()
        }
    }

    private fun restoreSelection(view: View) {
        adapter?.apply {
            val count = restoreData()
            notifyDataSetChanged()
            Snackbar.make(view, "Item${if (count > 1) "s" else ""} restored.", Snackbar.LENGTH_LONG).show()
        }
    }
}
