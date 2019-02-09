package `in`.aayushgoyal.hackernews

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {

    private lateinit var buttonSignInWithPhone: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        supportActionBar?.hide()
        buttonSignInWithPhone = findViewById(R.id.button_sign_in_with_phone_activity_sign_in)

        buttonSignInWithPhone.setOnClickListener {
            val intent = Intent(this@SignInActivity, PhoneAuthActivity::class.java)
            startActivity(intent)
        }
    }
}