package `in`.aayushgoyal.hackernews

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

// Activity that presents to the user phone number sing-in or sign-up flow of the app to the user.
class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var userUID: String

    lateinit var mProgressBar: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    lateinit var mVerificationId: String
    lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    companion object {
        private val TAG = "PhoneAuthActivity"
        var mVerificationInProgress = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        if(savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)

        mProgressBar = ProgressDialog(this)
        mProgressBar.setMessage("\tSetting you up...")
        mProgressBar.setCancelable(false)

        mAuth = FirebaseAuth.getInstance()

        val mPhoneNumberFragment = PhoneNumberFragment()
        val fragmentTag = "PHONE_NUMBER"
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragment_holder_phone_auth, mPhoneNumberFragment, fragmentTag).commit()

        mCallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted: $credential")
                mVerificationInProgress = false

                // Sign-in the user.
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                mVerificationInProgress = false

                if(e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(this@PhoneAuthActivity, "Invalid phone number.", Toast.LENGTH_LONG).show()
                } else if(e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent: $verificationId")

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
            }

        }

    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        mProgressBar.show()
                        userUID = mAuth.currentUser?.uid!!
                        db.collection("users")
                                .document(userUID)
                                .get()
                                .addOnCompleteListener { task ->
                                    if(task.isSuccessful) {
                                        // Check if the document already exists with userUID and that document has fields filled up.
                                        if(task.result!!.exists() && task.result!!.getString("firstName")!!.isNotBlank()) {
                                            // A document was found for this user, therefore he is a returning user.
                                            // First, save the basic details of the user into the SharedPreference.
                                            createSignInSession()
                                            saveUsersBasicDetailsInSharedPreference(task.result!!)
                                            mProgressBar.hide()
                                            val intent = Intent(this, MainActivity::class.java)
                                            intent.putExtra("isUserSignedIn", false)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            /* No document was found for this user, therefore he is a new user.
                                             * Or he is a returning user that didn't complete the registration process in the first time.
                                             * Navigate him to the NewUserDetailsFillUpActivity.
                                             */
                                            mProgressBar.hide()
                                            val intent = Intent(this, NewUserDetailsFillUpActivity::class.java)
                                                    intent.putExtra("phoneNumber", PhoneNumberFragment.phoneNumber)
                                            startActivity(intent)
                                        }
                                    } else {
                                        mProgressBar.hide()
                                        Log.e(TAG, task.exception.toString())
                                        Toast.makeText(this, "Sorry, something happened on our side. Please try again.",
                                                Toast.LENGTH_LONG).show()
                                    }
                                }
                    } else {
                        // Sign in failed, display a message and update the UI
                        mProgressBar.hide()
                        Toast.makeText(applicationContext, "Sorry, could not log you in.", Toast.LENGTH_LONG).show()
                        if (it.exception is FirebaseAuthInvalidCredentialsException)
                            // The verification code entered was invalid
                            Toast.makeText(applicationContext, "Sorry, You entered an invalid code entered. Retry.",
                                    Toast.LENGTH_LONG).show()
                    }
                }
    }

    // Save the basic details of the user in the SharedPreference.
    private fun saveUsersBasicDetailsInSharedPreference(document: DocumentSnapshot) {
        val userDetailsSharedPreference: SharedPreferences = applicationContext
                .getSharedPreferences("in.aayushgoyal.hackernews.USER_BASIC_DETAILS", Context.MODE_PRIVATE)

        val country = document.getString("country")
        val dateOfBirth = document.getLong("dateOfBirth")
        val email = document.getString("email")
        val firstName = document.getString("firstName")
        val lastName = document.getString("lastName")
        val middleName = document.getString("middleName")
        val mobileNumber = document.getString("mobileNumber")
        val uid = document.getString("uid")

        val editor = userDetailsSharedPreference.edit()

        editor.putString("country", country)
        editor.putLong("dateOfBirth", dateOfBirth!!)
        editor.putString("email", email)
        editor.putString("firstName", firstName)
        editor.putString("lastName", lastName)
        editor.putString("middleName", middleName)
        editor.putString("mobileNumber", mobileNumber)
        editor.putString("uid", uid)

        editor.apply()
    }

    private fun createSignInSession() {
        val signInCredentialsSharedPreference = applicationContext
                .getSharedPreferences("in.aayushgoyal.hackernews.USER_SIGN_IN_CREDENTIALS", Context.MODE_PRIVATE)
        val editor = signInCredentialsSharedPreference.edit()

        // Save whether the user is signed-in and if he is signed-in save his userUID for faster loading of UI next time he comes.
        editor.putBoolean("isUserSignedIn", true)
        editor.putString("userUID", userUID)

        editor.apply()
    }

}