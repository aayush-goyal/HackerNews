package `in`.aayushgoyal.hackernews

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar


import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.*

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var webService: WebService
    private lateinit var recyclerView: RecyclerView

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val progressBar: ProgressBar = findViewById(R.id.progress_bar_activity_main)
        recyclerView = findViewById(R.id.recycler_view_activity_main)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
                .baseUrl(WebService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

        webService = retrofit.create(WebService::class.java)

        uiScope.launch {
            val storiesResponse = webService.getStories().await()
            val stories = storiesResponse.body() as List<Long>

            val topStories: MutableList<Story> = ArrayList()

            withContext(Dispatchers.IO) {
                stories.subList(0, 19).forEach {
                    topStories.add(getStory(it))
                }
            }

            val storiesAdapter = StoriesAdapter(this@MainActivity, topStories)
            recyclerView.adapter = storiesAdapter
            storiesAdapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when(item!!.itemId) {
        R.id.action_about_app -> {
            val intent = Intent(this@MainActivity, AboutAppActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.action_profile -> {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private suspend fun getStory(id: Long): Story {
        val storyResponse = webService.getStory(id).await()
        return storyResponse.body() as Story
    }

}