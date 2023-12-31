package com.mobdeve.tiongquico.exercise3lifecyclesp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    // Data for the application
    private lateinit var data: ArrayList<PostModel>

    // Shared preferences
    private lateinit var prefs: SharedPreferences

    // RecyclerView components
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter

    // Indicators for what Layout should be used or if the like buttons should be hidden
    private var recyclerViewDefaultView = LayoutType.LINEAR_VIEW_TYPE.ordinal // int of LayoutType.LINEAR_VIEW_TYPE (default) or LayoutType.GRID_VIEW_TYPE
    private var hideLikeButtons = false // true = hide buttons; false = shown buttons (default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Instantiation of the SharedPreferences
        this.prefs = getSharedPreferences("Exercise3LifecycleSP", MODE_PRIVATE)

        // Set the default values of the indicators based on the values stored in the SharedPreferences
        this.recyclerViewDefaultView = this.prefs.getInt("viewSelected", this.recyclerViewDefaultView)
        this.hideLikeButtons = this.prefs.getBoolean("hideLikeSelected", this.hideLikeButtons)

        // Initialize the data, recyclerView and adapter
        this.data = DataHelper.initializeData()
        this.recyclerView = findViewById(R.id.recyclerView)
        this.myAdapter = MyAdapter(this.data)

        // Set the layout manager according to the default view
        this.recyclerView.layoutManager = getLayoutManager(this.recyclerViewDefaultView)

        // Initialize the view type and hide like button settings
        this.myAdapter.setViewType(this.recyclerViewDefaultView)
        this.myAdapter.setHideLikeBtn(this.hideLikeButtons)

        // Sets the adapter of the recycler view
        this.recyclerView.adapter = this.myAdapter
    }

    /*
     * Just a method to return a specific LayoutManager based on the ViewType provided.
     * */
    private fun getLayoutManager(value: Int): RecyclerView.LayoutManager {
        if (value == LayoutType.LINEAR_VIEW_TYPE.ordinal)
            return LinearLayoutManager(this)
        else
            return GridLayoutManager(this, 2)
    }

    /*
    * Responsible for inflating the options menu on the upper right corner of the screen.
    * */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /*
     * A little overkill tbh, but this method is responsible for handling the selection of items
     * in the options menu. There's only one item anyway -- Settings, which leads the user to the
     * Settings activity.
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val i = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(i)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*
     * This method is called when the user returns to the MainActivity from the SettingsActivity.
     * This is where the changes in the settings are applied.
     * */
    override fun onResume() {
        super.onResume()

        // Get the values of the SharedPreferences
        val viewSelected = this.prefs.getInt("viewSelected", this.recyclerViewDefaultView)
        val hideLikeSelected = this.prefs.getBoolean("hideLikeSelected", this.hideLikeButtons)

        // Set the layout manager according to the view selected
        this.recyclerView.layoutManager = getLayoutManager(viewSelected)

        // Set the view type and hide like button settings
        this.myAdapter.setViewType(viewSelected)
        this.myAdapter.setHideLikeBtn(hideLikeSelected)

        // Notify the adapter that the data has changed
        this.myAdapter.notifyDataSetChanged()

        // Recycle the view
        this.recyclerView.recycledViewPool.clear()
    }
}