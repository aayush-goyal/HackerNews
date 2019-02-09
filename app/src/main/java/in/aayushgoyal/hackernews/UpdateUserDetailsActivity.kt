package `in`.aayushgoyal.hackernews

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class UpdateUserDetailsActivity : AppCompatActivity() {

    private lateinit var mFirstName: String
    private lateinit var mMiddleName: String
    private lateinit var mLastName: String
    private lateinit var mMobileNo: String
    private lateinit var mEmail: String
    private lateinit var mDOB: String
    private lateinit var mCountry: String

    private lateinit var mFirstNameChanged: String
    private lateinit var mMiddleNameChanged: String
    private lateinit var mLastNameChanged: String
    private lateinit var mMobileNoChanged: String
    private lateinit var mEmailChanged: String
    private lateinit var mDOBChanged: String
    private lateinit var mCountryChanged: String

    private lateinit var mETFirstName: EditText
    private lateinit var mETMiddleName: EditText
    private lateinit var mETLastName: EditText
    private lateinit var mETMobileNumber: EditText
    private lateinit var mETEmail: EditText
    private lateinit var mETDOB: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user_details)
        title = getString(R.string.activity_update_details)

        mETFirstName = findViewById(R.id.et_user_fname_activity_update_user_details)
        mETMiddleName = findViewById(R.id.et_user_mname_activity_update_user_details)
        mETLastName = findViewById(R.id.et_user_lname_activity_update_user_details)
        mETMobileNumber = findViewById(R.id.et_user_mobile_no_activity_update_user_details)
        mETEmail = findViewById(R.id.et_user_email_activity_update_user_details)
        mETDOB = findViewById(R.id.et_dob_activity_update_user_details)
        val mImageButtonDOB: ImageButton = findViewById(R.id.ib_user_dob_activity_update_user_details)
        val mSpinnerCountries: Spinner = findViewById(R.id.spinner_country_activity_update_user_details)
        val mButtonUpdate: Button = findViewById(R.id.button_update_activity_update_user_details)

        val intent = intent
        mFirstName = intent.getStringExtra("firstName")
        mMiddleName = intent.getStringExtra("middleName")
        mLastName = intent.getStringExtra("lastName")
        mMobileNo = intent.getStringExtra("mobileNumber")
        mEmail = intent.getStringExtra("email")
        mDOB = intent.getStringExtra("dateOfBirth")
        mCountry = intent.getStringExtra("country")

        val adapter = ArrayAdapter.createFromResource(this,
                R.array.countries,
                R.layout.spinner_item)
                .also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    mSpinnerCountries.adapter = it
                }

        mETFirstName.setText(mFirstName)
        if(mMiddleName.isNotEmpty())
            mETMiddleName.setText(mMiddleName)
        mETLastName.setText(mLastName)
        mETMobileNumber.setText(mMobileNo)
        mETEmail.setText(mEmail)
        mETDOB.setText(mDOB)
        mSpinnerCountries.setSelection(adapter.getPosition(mCountry))

        mImageButtonDOB.setOnClickListener {
            val dialogFragment = DatePickerFragment()
            val bundle = Bundle()
            bundle.putString("activityName", "UpdateDetailsActivity")
            mDOBChanged = mETDOB.text.toString()
            bundle.putString("dob", mDOBChanged)
            dialogFragment.arguments = bundle
            dialogFragment.show(supportFragmentManager, "DATE_PICKER")
        }

        mSpinnerCountries.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mCountryChanged = mSpinnerCountries.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing.
            }
        }

        mButtonUpdate.setOnClickListener {
            mFirstNameChanged = mETFirstName.text.toString()
            mMiddleNameChanged = mETMiddleName.text.toString()
            mLastNameChanged = mETLastName.text.toString()
            mMobileNoChanged = mETMobileNumber.text.toString()
            mEmailChanged = mETEmail.text.toString()
            mDOBChanged = mETDOB.text.toString()

            if(isFormValid() && areDetailsChanged())
                updateDetails()
            else {
                Toast.makeText(this@UpdateUserDetailsActivity, "You have not updated any field.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    }

    private fun isFormValid() = if(mETFirstName.text.toString().isEmpty()) {
            mETFirstName.error = "First name can not be empty."
            false
        } else if(mETLastName.text.toString().isEmpty()) {
            mETLastName.error = "Last name can not be empty."
            false
        } else if(mETMobileNumber.text.toString().isEmpty()) {
            mETMobileNumber.error = "This field can not be empty."
            false
        } else if(mETEmail.text.toString().isEmpty()) {
            mETEmail.error = "This field can not be empty."
            false
        } else if(mETDOB.text.toString().isEmpty()) {
            Toast.makeText(this, "Tell us your special day.", Toast.LENGTH_LONG).show()
            false
        } else if(!isConnected()) {
            Toast.makeText(this, "You are not connected to internet. Please connect to internet, and try again.",
                    Toast.LENGTH_LONG).show()
            false
        } else
            true

    private fun areDetailsChanged() = (mFirstNameChanged != mFirstName) || (mMiddleNameChanged != mMiddleName) ||
            (mLastNameChanged != mLastName) || (mEmailChanged != mEmail) || (mMobileNoChanged != mMobileNo) ||
            (mDOBChanged != mDOB) || (mCountryChanged != mCountry)

    private fun isConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun updateDetails() {
        val db = FirebaseFirestore.getInstance()
        val userUID = FirebaseAuth.getInstance().currentUser!!.uid

        val userDetails: HashMap<String, Any> = HashMap()
        userDetails["firstName"] = mFirstNameChanged
        userDetails["middleName"] = mMiddleNameChanged
        userDetails["lastName"] = mLastNameChanged
        userDetails["email"] = mEmailChanged
        userDetails["dateOfBirth"] = getLongFromDate(mDOBChanged)
        userDetails["country"] = mCountryChanged

        // Update the changed details in Firebase Cloudstore.
        db.collection("users")
                .document(userUID)
                .update(userDetails)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        // Update the details in SharedPreference.
                        val userDetailsSharedPreference = applicationContext
                                .getSharedPreferences("in.aayushgoyal.hackernews.USER_BASIC_DETAILS", Context.MODE_PRIVATE)
                        val editor = userDetailsSharedPreference.edit()
                        editor.putString("country", mCountryChanged)
                        editor.putLong("dateOfBirth", getLongFromDate(mDOBChanged))
                        editor.putString("email", mEmailChanged)
                        editor.putString("firstName", mFirstNameChanged)
                        editor.putString("lastName", mLastNameChanged)
                        editor.putString("middleName", mMiddleNameChanged)
                        editor.putString("mobileNumber", mMobileNoChanged)
                        editor.apply()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Could not update your details. Try again.",
                            Toast.LENGTH_LONG).show()
                }
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

}