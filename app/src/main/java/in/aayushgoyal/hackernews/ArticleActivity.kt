package `in`.aayushgoyal.hackernews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class ArticleActivity : AppCompatActivity() {

    private lateinit var mTabs: TabLayout
    private lateinit var mViewPager: ViewPager
    private lateinit var articlePagerAdapter: FragmentStatePagerAdapter
    private lateinit var story: Story

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        mTabs = findViewById(R.id.tabs_activity_article)

        story = intent.getSerializableExtra("story") as Story

        val bundle = Bundle()
        bundle.putSerializable("story", story)

        mViewPager = findViewById(R.id.viewpager_activity_article)
        articlePagerAdapter = ArticlePagerAdapter(supportFragmentManager, story)
        mViewPager.adapter = articlePagerAdapter

        mTabs.setupWithViewPager(mViewPager)
    }

}