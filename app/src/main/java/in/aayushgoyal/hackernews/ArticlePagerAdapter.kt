package `in`.aayushgoyal.hackernews

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ArticlePagerAdapter(fm: FragmentManager, private val story: Story): FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int  = 2

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putSerializable("story", story)

        return if(position == 0) {
            val articleFragment = ArticleFragment()
            articleFragment.arguments = bundle
            articleFragment
        } else {
            val articleCommentsFragment = ArticleCommentsFragment()
            articleCommentsFragment.arguments = bundle
            articleCommentsFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence? = if(position == 0)
            "Article"
        else
            "Comments"

}