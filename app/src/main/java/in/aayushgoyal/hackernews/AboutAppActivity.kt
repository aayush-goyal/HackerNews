package `in`.aayushgoyal.hackernews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)

        val tvVersionNumber: TextView = findViewById(R.id.about_us_version_number)
        tvVersionNumber.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://github.com/aayush-goyal/HackerNews")
            startActivity(intent)
        }

        val tvMadeInIndia: TextView = findViewById(R.id.about_us_made_in_India)
        val textMadeInIndia = "<font color=#000000>Made with </font>" +
        "<font color=#FF0000>\u2665 </font>" +
                "<font color=#000000> in India.</font>"
        tvMadeInIndia.text = Html.fromHtml(textMadeInIndia)
    }

}
