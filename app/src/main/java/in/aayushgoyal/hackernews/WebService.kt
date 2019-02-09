package `in`.aayushgoyal.hackernews

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WebService {

    companion object {
        const val BASE_URL = "https://hacker-news.firebaseio.com/v0/"
    }

    @GET("topstories.json?print=pretty")
    fun getStories(): Deferred<Response<List<Long>>>

    @GET("item/{story}.json?print=pretty")
    fun getStory(@Path("story") id: Long): Deferred<Response<Story>>

}