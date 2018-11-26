package `in`.aayushgoyal.hackernews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        supportActionBar?.hide()
    }
}
