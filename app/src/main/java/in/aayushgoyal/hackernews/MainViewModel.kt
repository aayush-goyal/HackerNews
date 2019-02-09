package `in`.aayushgoyal.hackernews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel: ViewModel() {
    private lateinit var story: LiveData<Story>
    private lateinit var stories: LiveData<List<Long>>

    private lateinit var webService: WebService

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    fun init() {
        val retrofit = Retrofit.Builder()
                .baseUrl(WebService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

        webService = retrofit.create(WebService::class.java)
    }

    fun getStories() {
        uiScope.launch {
            val storiesResponse = webService.getStories().await()
            val stories = storiesResponse.body() as List<Long>

            val topStories: MutableList<Story> = ArrayList()

            stories.subList(0, 9).forEach {
                val storyResponse = webService.getStory(it).await()
                val story = storyResponse.body() as Story
                topStories.add(story)
            }
        }

        return
    }

    fun getStory(storyID: Long) {

    }

}