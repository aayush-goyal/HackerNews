package `in`.aayushgoyal.hackernews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArticleCommentsFragment : Fragment() {

    private lateinit var webService: WebService

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_article_comments, container, false)

        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar_fragment_article_comments)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_fragment_article_comments)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val story = arguments?.getSerializable("story") as Story

        val retrofit = Retrofit.Builder()
                .baseUrl(WebService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

        webService = retrofit.create(WebService::class.java)

        uiScope.launch {
            var comments: MutableList<Story> = ArrayList()

            story.kids.forEach {
                comments = getComments(it, comments)
            }

            val commentsAdapter = CommentsAdapter(activity!!, comments)
            recyclerView.adapter = commentsAdapter
            commentsAdapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        return view
    }

    private suspend fun getComments(id: Long, comments: MutableList<Story>): MutableList<Story> {
        val commentResponse = webService.getStory(id).await()
        val comment = commentResponse.body() as Story
        comments.add(comment)
        if(!comment.kids.isNullOrEmpty()) {
            comment.kids.forEach {
                getComments(it, comments)
            }
        }

        return comments
    }
}
