package `in`.aayushgoyal.hackernews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import java.util.*

internal fun getTime(time: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = time*1000
    cal.timeZone = TimeZone.getDefault()
    return (cal.get(Calendar.DAY_OF_MONTH).toString() + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR).toString()
            + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE))
}

class StoriesAdapter(val mContext: Context, val mStoriesList: List<Story>): RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(R.layout.list_item_article, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = mStoriesList[position]

        holder.tvArticleScore.text = story.score.toString()
        holder.tvArticleTitle.text = story.title
        holder.tvArticleTime.text = getTime(story.time)
        val articleBy = " · " + story.by
        holder.tvArticleUser.text = articleBy
        holder.tvArticleCommentsCount.text = story.descendants.toString()
    }

    override fun getItemCount(): Int {
        return mStoriesList.size
    }

    inner class StoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvArticleScore: TextView = itemView.findViewById(R.id.tv_article_score_list_item_article)
        val tvArticleTitle: TextView = itemView.findViewById(R.id.tv_article_title_list_item_article)
        val tvArticleTime: TextView = itemView.findViewById(R.id.tv_article_time_list_item_article)
        val tvArticleUser: TextView = itemView.findViewById(R.id.tv_article_user_list_item_article)
        val tvArticleCommentsCount: TextView = itemView.findViewById(R.id.tv_article_comments_count_list_item_article)

        init {
            itemView.setOnClickListener {
                val intent = Intent(mContext, ArticleActivity::class.java)
                intent.putExtra("story", mStoriesList[adapterPosition])
                mContext.startActivity(intent)
            }
        }

    }
}