package `in`.aayushgoyal.hackernews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentsAdapter(private val mContext: Context, private val mCommentsList: List<Story>): RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(R.layout.list_item_comment, parent, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val comment = mCommentsList[position]

        holder.tvCommentText.text = comment.text
        holder.tvCommentTime.text = getTime(comment.time)
        val articleBy = " · " + comment.by
        holder.tvCommentUser.text = articleBy
    }

    override fun getItemCount(): Int {
        return mCommentsList.size
    }

    inner class CommentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvCommentText: TextView = itemView.findViewById(R.id.tv_comment_text_list_item_comment)
        val tvCommentTime: TextView = itemView.findViewById(R.id.tv_comment_time_list_item_comment)
        val tvCommentUser: TextView = itemView.findViewById(R.id.tv_comment_user_list_item_comment)
    }

}