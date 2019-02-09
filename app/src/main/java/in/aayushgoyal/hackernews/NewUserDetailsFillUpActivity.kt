package `in`.aayushgoyal.hackernews

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewUserDetailsFillUpActivity : AppCompatActivity() {

    private lateinit var mCountry: String
    private lateinit var mDOB: String
    private lateinit var mFirstName: String
    private lateinit var mMiddleName: String
    private lateinit var mLastName: String
    private lateinit var mMobileNumber: String
    private lateinit var mEmail: String

    private lateinit var mButtonCreateProfile: Button
    private lateinit var mETFirstName: TextInputEditText
    private lateinit var mETMiddleName: TextInputEditText
    private lateinit var mETLastName: TextInputEditText
    private lateinit var mETMobileNumber: TextInputEditText
    private lateinit var mETEmail: TextInputEditText
    private lateinit var mETDOB: TextInputEditText
    private lateinit var mImageButtonDatePicker: ImageButton
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mSpinnerCountries: Spinner

    private val db = FirebaseFirestore.getInstance()
    private val userUID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user_details_fill_up)

        mETFirstName = findViewById(R.id.tv_user_fname_activity_new_user_details_fill_up)
        mETMiddleName = findViewById(R.id.tv_user_mname_activity_new_user_details_fill_up)
        mETLastName = findViewById(R.id.tv_user_lname_activity_new_user_details_fill_up)
        mETMobileNumber = findViewById(R.id.tv_user_mobile_no_activity_new_user_details_fill_up)
        mETEmail = findViewById(R.id.tv_user_email_activity_new_user_details_fill_up)
        mETDOB = findViewById(R.id.et_dob_activity_new_user_details_fill_up)
        mImageButtonDatePicker = findViewById(R.id.ib_user_dob_activity_new_user_details_fill_up)
        mButtonCreateProfile = findViewById(R.id.button_create_profile_activity_new_user_details_fill_up)
        mSpinnerCountries = findViewById(R.id.spinner_country_activity_new_user_details_fill_up)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("\tSetting you up...")
        mProgressDialog.setCancelable(false)

        val intent = intent
        mMobileNumber = intent.getStringExtra("phoneNumber")
        mETMobileNumber.setText(mMobileNumber)

        ArrayAdapter.createFromResource(this,
                R.array.countries,
                R.layout.spinner_item)
                .also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    mSpinnerCountries.adapter = it
                }

        mImageButtonDatePicker.setOnClickListener {
            val dialogFragment = DatePickerFragment()
            val bundle = Bundle()
            bundle.putString("activityName", "NewUserDetailsFillUpActivity")
            mDOB = mETDOB.text.toString()
            bundle.putString("dob", mDOB)
            dialogFragment.arguments = bundle
            dialogFragment.show(supportFragmentManager, "DATE_PICKER")
        }

        mSpinnerCountries.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                mCountry = mSpinnerCountries.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        mButtonCreateProfile.setOnClickListener {
            if(isFormValid()) {
                mProgressDialog.show()
                // Get the basic details of the user from the form.
                mFirstName = mETFirstName.text.toString()
                mMiddleName = mETMiddleName.text.toString()
                mLastName = mETLastName.text.toString()
                mEmail = mETEmail.text.toString()

                /* Set-up the profile of the new user and navigate the user to HomeFragment, and pass these details to
                 * HomeFragment where his profile will be set.
                 */
                setUpNewUser(mCountry, getLongFromDate(mDOB), mEmail, mMobileNumber, mFirstName, mMiddleName, mLastName, userUID)
            }
        }
    }

    private fun isFormValid() = if (mCountry.isBlank()) {
        Toast.makeText(this, "Please select a country.", Toast.LENGTH_LONG).show()
        false
    } else if (mETFirstName.text.toString().isEmpty()) {
        mETFirstName.error = "Please enter your first name"
        false
    } else if (mETLastName.text.toString().isEmpty()) {
        mETLastName.error = "Please enter your last name"
        false
    } else if (mETMobileNumber.text.toString().isEmpty()) {
        mETMobileNumber.error = "Please enter your mobile number"
        false
    } else if (mETEmail.text.toString().isEmpty()) {
        mETEmail.error = "Please enter your email ID"
        false
    } else if (mETDOB.text.toString().isEmpty()) {
        Toast.makeText(this, "Please select your DOB", Toast.LENGTH_LONG).show()
        false
    } else if (!isUserAboveThirteen()) {
        Toast.makeText(this, "You must be at least 13 years old to continue.", Toast.LENGTH_LONG).show()
        false
    } else if (!isConnected()) {
        Toast.makeText(this, "You are not connected to internet. Please connect to internet, and try again.",
                Toast.LENGTH_LONG).show()
        false
    } else
        true

    private fun isConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun isUserAboveThirteen(): Boolean {
        val dateOfBirth = mETDOB.text.toString().split("/")
        val dateDOB = Integer.parseInt(dateOfBirth[0])
        val monthDOB = Integer.parseInt(dateOfBirth[1])
        val yearDOB = Integer.parseInt(dateOfBirth[2])

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DAY_OF_WEEK)

        return !((year - yearDOB == 13) && (month + 1 - monthDOB == 0) && (date - dateDOB < 0))
                && !((year - yearDOB == 13) && (month + 1 - monthDOB < 0))
                && (year - yearDOB > 13)
    }

    private fun getLongFromDate(date: String): Long {
        val dateComponents = date.split("/").toTypedArray()

        when(Integer.parseInt(dateComponents[1])) {
            1 -> dateComponents[1] = "January"
            2 -> dateComponents[1] = "February"
            3 -> dateComponents[1] = "March"
            4 -> dateComponents[1] = "April"
            5 -> dateComponents[1] = "May"
            6 -> dateComponents[1] = "June"
            7 -> dateComponents[1] = "July"
            8 -> dateComponents[1] = "August"
            9 -> dateComponents[1] = "September"
            10 -> dateComponents[1] = "October"
            11 -> dateComponents[1] = "November"
            12 -> dateComponents[1] = "December"
        }

        val stringDate = dateComponents[0] + "-" + dateComponents[1] + "-" + dateComponents[2]

        val format = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
        return try {
            val dateLong = format.parse(stringDate)
            dateLong.time
        } catch (pe: ParseException) {
            Toast.makeText(this, "Something happened on our end.", Toast.LENGTH_LONG).show()
            1
        }
    }

    /* Set-up the new user and all the related contents on Firebase for the user.
     * Set-up new user profile under users collection.
     * Set-up users' friends under users -> friends collection. - It will be setup in FriendsFragment. - Not included in initial versions.
     * Set-up transactions collection under users collection. - Not necessary.
     */
    private fun setUpNewUser(country: String, dateOfBirth: Long, email: String, mobileNumber: String, firstName: String,
                             middleName: String, lastName: String, userUID: String) {
        // Create a new user with details of the user under the users colection.
        val userDetails: HashMap<String, Any> = HashMap()
        userDetails["country"] = country
        userDetails["dateOfBirth"] = dateOfBirth
        userDetails["email"] = email
        userDetails["mobileNumber"] = mobileNumber
        userDetails["firstName"] = firstName
        userDetails["middleName"] = middleName
        userDetails["lastName"] = lastName
        userDetails["uid"] = userUID

        // Create a new document in the Firestore with the details provided by the user.
        db.collection("users")
                .document(userUID)
                .set(userDetails)
                .addOnCompleteListener {
                    mProgressDialog.hide()
                    if(it.isSuccessful) {
                        // The profile was successfully set-up. Navigate him to HomeFragment.
                        // First, save the basic details of the user into the SharedPreference.
                        createSignInSession()
                        saveUsersBasicDetailsInSharedPreference(userDetails)
                        Toast.makeText(this, "You are all set to go.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("isUserSigned", false)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    else
                        Toast.makeText(this, "Sorry, could not set up your profile. Try again.", Toast.LENGTH_LONG).show()
                }
    }

    // Save the basic details of the user in the SharedPreference.
    private fun saveUsersBasicDetailsInSharedPreference(userDetails: HashMap<String, Any>) {
        val userDetailsSharedPreference = applicationContext
                .getSharedPreferences("in.aayushgoyal.hackernews.USER_BASIC_DETAILS", Context.MODE_PRIVATE)

        val country = userDetails["country"].toString()
        val dateOfBirth = userDetails["dateOfBirth"] as Long
        val email = userDetails["email"].toString()
        val firstName = userDetails["firstName"].toString()
        val lastName = userDetails["lastName"].toString()
        val middleName = userDetails["middleName"].toString()
        val mobileNumber = userDetails["mobileNumber"].toString()
        val uid = userDetails["uid"].toString()

        val editor = userDetailsSharedPreference.edit()

        editor.putString("country", country)
        editor.putLong("dateOfBirth", dateOfBirth)
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