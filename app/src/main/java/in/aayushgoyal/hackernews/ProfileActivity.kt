package `in`.aayushgoyal.hackernews

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

import java.text.SimpleDateFormat
import java.util.*

private const val UPDATE_DETAILS_ACTIVITY = 0
private const val CHOOSE_PROFILE_IMAGE = 100
private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 200

class ProfileActivity : AppCompatActivity() {

    private lateinit var mFirstName: String
    private lateinit var mMiddleName: String
    private lateinit var mLastName: String
    private lateinit var mMobileNo: String
    private lateinit var mEmail: String
    private lateinit var mDateOfBirth: String
    private lateinit var mCountry: String
    private lateinit var profilePictureURL: String

    private lateinit var uriProfilePicture: Uri

    private lateinit var buttonUpdateDetails: Button
    private lateinit var imageView: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvMobileNo: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDateOfBirth: TextView
    private lateinit var tvCountry: TextView

    private val user = FirebaseAuth.getInstance().currentUser

    companion object {
        fun firstTimeAskingPermission(context: Context, permission: String, isFirstTime: Boolean) {
            val sharedPreference = context.getSharedPreferences("in.aayushgoyal.hackernews.PERMISSIONS_FILE", Context.MODE_PRIVATE)
            sharedPreference.edit().putBoolean(permission, isFirstTime).apply()
        }

        fun isFirstTimeAskingPermission(context: Context, permission: String) = context.getSharedPreferences(
                "in.aayushgoyal.hackernews.PERMISSIONS_FILE",
                Context.MODE_PRIVATE).getBoolean(permission, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        buttonUpdateDetails = findViewById(R.id.button_update_details_activity_profile)
        val buttonLogout: Button = findViewById(R.id.button_logout_activity_profile)
        imageView = findViewById(R.id.iv_user_profile_image_activity_profile)
        tvCountry = findViewById(R.id.tv_country_activity_profile)
        tvDateOfBirth = findViewById(R.id.tv_dob_activity_profile)
        tvEmail = findViewById(R.id.tv_email_activity_profile)
        tvMobileNo = findViewById(R.id.tv_mobile_no_activity_profile)
        tvName = findViewById(R.id.tv_user_full_name_activity_profile)

        buttonUpdateDetails.isClickable = false

        loadProfilePicture()

        loadProfileDetails()

        imageView.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // If the user has earlier denied the permission but has not chosen Never Ask Again, then show an
                // explanation to the user why the app needs this particular permission in the first place.
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "The app needs read permission to External Storage in order to access" +
                            " the photo.", Toast.LENGTH_LONG).show()

                    // Introduce a delay so that the user can read the toast. Then request for the permission again.
                    val handler = Handler()
                    handler.postDelayed({
                        ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
                    }, 1500)
                } else {
                    if(isFirstTimeAskingPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        firstTimeAskingPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, false)
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
                    } else
                        //Permission disable by device policy or user denied permanently. Show proper error message
                        Toast.makeText(this, "Read permission to External Storage has not been granted. You" +
                                " can grant the permission from Settings->Permissions->Storage", Toast.LENGTH_LONG).show()
                }
            } else
                // Permission is granted. Show the image chooser to the user.
                showImageChooser()
        }

        buttonUpdateDetails.setOnClickListener {
            val intent = Intent(this, UpdateUserDetailsActivity::class.java)
            intent.putExtra("firstName", mFirstName)
            intent.putExtra("middleName", mMiddleName)
            intent.putExtra("lastName", mLastName)
            intent.putExtra("mobileNumber", mMobileNo)
            intent.putExtra("email", mEmail)
            intent.putExtra("dateOfBirth", mDateOfBirth)
            intent.putExtra("country", mCountry)
            startActivityForResult(intent, UPDATE_DETAILS_ACTIVITY)
        }

        buttonLogout.setOnClickListener {
            signOut()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CHOOSE_PROFILE_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            uriProfilePicture = data.data!!
            try {
                uploadProfilePictureToFirebaseStorage()
            } catch(e: IOException) {
                Log.e("PROFILE ACTIVITY", e.message)
            }
        } else if(requestCode == UPDATE_DETAILS_ACTIVITY && resultCode == Activity.RESULT_OK) {
            loadProfileDetails()
            Toast.makeText(this, "Details Updated Successfully.", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadProfilePicture() {
        if(user!!.photoUrl != null)
            Glide.with(this).load(user.photoUrl).into(imageView)
    }

    private fun showImageChooser() {
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select a profile photo."), CHOOSE_PROFILE_IMAGE)
    }

    // Upload the image selected by the user to the FirebaseStorage as profile picture of the user.
    private fun uploadProfilePictureToFirebaseStorage() {
        // Get a reference to the storage and the path of the profile picture.
        val storageReference = FirebaseStorage.getInstance().reference
        val profilePictureRef = storageReference.child("users/" + user!!.uid + "/" +
                "profpic" + ".jpg")

        // If URI of the local image is not null, upload the image to the storage using the same URI.
        val uploadTask = profilePictureRef.putFile(uriProfilePicture)
        uploadTask.addOnSuccessListener {
            profilePictureRef.downloadUrl.addOnSuccessListener {
                profilePictureURL = it.toString()
                updateUserProfile()
            }.addOnFailureListener {
                Toast.makeText(this, "Image could not be set. Please try again.", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Log.e("UPLOAD ERROR", it.message)
            Toast.makeText(this, "Image could not be set.", Toast.LENGTH_LONG).show()
        }

    }

    private fun updateUserProfile() {
        val profile = UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(profilePictureURL))
                .build()

        user!!.updateProfile(profile)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(this, "Profile Updated.", Toast.LENGTH_LONG).show()
                        loadProfilePicture()
                    }
                }
    }

    private fun loadProfileDetails() {
        val userDetails = getUsersBasicDetailsFromSharedPreference()
        if(!userDetails.isEmpty()) {
            // If the SharedPreference is successfully loaded, load the user details from the cache data.
            mFirstName = userDetails["firstName"].toString()
            mMiddleName = userDetails["middleName"].toString()
            mLastName = userDetails["lastName"].toString()
            mMobileNo = userDetails["mobileNumber"].toString()
            mEmail = userDetails["email"].toString()
            mDateOfBirth = getDateFromLong(userDetails["dateOfBirth"] as Long)
            mCountry = userDetails["country"].toString()

            loadProfileUI()
        } else {
            // Else, load the user details from the Firebase.
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                    .document(user!!.uid)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val documentSnapshot = it.result
                            mFirstName = documentSnapshot!!.getString("firstName") as String
                            mMiddleName = documentSnapshot.getString("middleName") as String
                            mLastName = documentSnapshot.getString("lastName") as String
                            mMobileNo = documentSnapshot.getString("mobileNumber") as String
                            mEmail = documentSnapshot.getString("email") as String
                            mDateOfBirth = getDateFromLong(documentSnapshot.getLong("dateOfBirth") as Long)
                            mCountry = documentSnapshot.getString("country") as String

                            loadProfileUI()
                        } else {
                            Toast.makeText(this, "Error loading data. Please try again.", Toast.LENGTH_LONG).show()
                        }
                    }
        }
    }

    private fun loadProfileUI() {
        if (mMiddleName.isEmpty()) {
            val name = "$mFirstName $mLastName"
            tvName.text = name
        }
        else {
            val name = "$mFirstName $mMiddleName $mLastName"
            tvName.text = name
        }
        tvEmail.text = mEmail
        tvMobileNo.text = mMobileNo
        tvDateOfBirth.text = mDateOfBirth
        tvCountry.text = mCountry

        buttonUpdateDetails.isClickable = true
    }

    // Save the basic details of the user in the SharedPreference.
    private fun getUsersBasicDetailsFromSharedPreference(): Map<String, Any> {
        val userDetailsSharedPreference = applicationContext
                .getSharedPreferences("in.aayushgoyal.hackernews.USER_BASIC_DETAILS", Context.MODE_PRIVATE)

        val country = userDetailsSharedPreference.getString("country", "")
        val dateOfBirth = userDetailsSharedPreference.getLong("dateOfBirth", 0)
        val email = userDetailsSharedPreference.getString("email", "")
        val firstName = userDetailsSharedPreference.getString("firstName", "")
        val lastName = userDetailsSharedPreference.getString("lastName", "")
        val middleName = userDetailsSharedPreference.getString("middleName", "")
        val mobileNumber = userDetailsSharedPreference.getString("mobileNumber", "")
        val uid = userDetailsSharedPreference.getString("uid", "")

        return if(firstName!!.isNotEmpty()) {
            val userDetails = HashMap<String, Any>()
            userDetails["country"] = country
            userDetails["dateOfBirth"] = dateOfBirth
            userDetails["email"] = email
            userDetails["firstName"] = firstName
            userDetails["lastName"] = lastName
            userDetails["middleName"] = middleName
            userDetails["mobileNumber"] = mobileNumber
            userDetails["uid"] = uid

            userDetails
        } else
            HashMap()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImageChooser()
                } else {
                    // Permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(this, "External Storage read permission has not been granted. Cannot load the" +
                            "selected image.", Toast.LENGTH_LONG).show()
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    /* This method does the following:
     * 1. Clears all the data from "in.aayushgoyal.hackernews.USER_SIGN_IN_CREDENTIALS" SharedPreference.
     * 2. Clears all the data from "in.aayushgoyal.hackernews.USER_BASIC_DETAILS" SharedPreference.
     * 3. Sign-outs the user from the app.
     */
    private fun signOut() {
        val signInCredentialsSharedPreference = applicationContext
                .getSharedPreferences("in.aayushgoyal.hackernews.USER_SIGN_IN_CREDENTIALS", Context.MODE_PRIVATE)
        val signInCredentialsSharedPreferenceEditor = signInCredentialsSharedPreference.edit()

        val userDetailsSharedPreference = applicationContext
                .getSharedPreferences("in.aayushgoyal.hackernews.USER_BASIC_DETAILS", Context.MODE_PRIVATE)
        val userDetailsSharedPreferenceEditor = userDetailsSharedPreference.edit()

        // Clear all the data from SharedPreference.
        signInCredentialsSharedPreferenceEditor.clear()
        signInCredentialsSharedPreferenceEditor.apply()

        userDetailsSharedPreferenceEditor.clear()
        userDetailsSharedPreferenceEditor.apply()

        // Sign out the user.
        FirebaseAuth.getInstance().signOut()
        // Navigate the user back to Sign-In screen.
        val intent = Intent(this@ProfileActivity, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun getDateFromLong(milliseconds: Long): String {
        val date = Date(milliseconds)
        val df2 = SimpleDateFormat("dd/MM/yyyy")
        return df2.format(date)
    }

}
