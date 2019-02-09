package `in`.aayushgoyal.hackernews

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.firebase.auth.PhoneAuthProvider

import java.util.concurrent.TimeUnit

class PhoneNumberFragment: Fragment() {

    private var mVerificationInProgress = PhoneAuthActivity.mVerificationInProgress

    companion object {
        internal var phoneNumber: String = ""
    }

    private lateinit var mETMobileNumber: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_phone_number, container, false)

        mETMobileNumber = view.findViewById(R.id.edit_text_mobile_no)
        val mButtonProceed: Button = view.findViewById(R.id.btn_proceed_phone_number)
        val mButtonDialPadPlus: Button = view.findViewById(R.id.button_dialpad_plus_fragment_phone_number)
        val mButtonDialPad0: Button = view.findViewById(R.id.button_dialpad_0_fragment_phone_number)
        val mButtonDialPad1: Button = view.findViewById(R.id.button_dialpad_1_fragment_phone_number)
        val mButtonDialPad2: Button = view.findViewById(R.id.button_dialpad_2_fragment_phone_number)
        val mButtonDialPad3: Button = view.findViewById(R.id.button_dialpad_3_fragment_phone_number)
        val mButtonDialPad4: Button = view.findViewById(R.id.button_dialpad_4_fragment_phone_number)
        val mButtonDialPad5: Button = view.findViewById(R.id.button_dialpad_5_fragment_phone_number)
        val mButtonDialPad6: Button = view.findViewById(R.id.button_dialpad_6_fragment_phone_number)
        val mButtonDialPad7: Button = view.findViewById(R.id.button_dialpad_7_fragment_phone_number)
        val mButtonDialPad8: Button = view.findViewById(R.id.button_dialpad_8_fragment_phone_number)
        val mButtonDialPad9: Button = view.findViewById(R.id.button_dialpad_9_fragment_phone_number)
        val mButtonDialPadBack: Button = view.findViewById(R.id.button_dialpad_back_fragment_phone_number)

        mButtonProceed.setOnClickListener {
            phoneNumber = mETMobileNumber.text.toString()
            if(phoneNumber.isEmpty())
                mETMobileNumber.error = "Please enter a valid mobile number."
            else if(!phoneNumber.startsWith('+'))
                Toast.makeText(activity, "Please start with your country code.", Toast.LENGTH_LONG).show()
            else {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        activity as PhoneAuthActivity,
                        (activity as PhoneAuthActivity).mCallbacks
                )

                mVerificationInProgress = true

                val mPhoneNumberVerificationFragment = PhoneNumberVerificationFragment()
                val fragmentID = "PHONE_NUMBER_VERIFICATION"
                val fragmentManager = fragmentManager
                fragmentManager!!.beginTransaction().replace(R.id.fragment_holder_phone_auth,
                        mPhoneNumberVerificationFragment,
                        fragmentID).commit()
            }
        }

        mButtonDialPadPlus.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_plus)) }

        mButtonDialPad0.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_0))}

        mButtonDialPad1.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_1))}

        mButtonDialPad2.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_2))}

        mButtonDialPad3.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_3))}

        mButtonDialPad4.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_4))}

        mButtonDialPad5.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_5))}

        mButtonDialPad6.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_6))}

        mButtonDialPad7.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_7))}

        mButtonDialPad8.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_8))}

        mButtonDialPad9.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_9))}

        mButtonDialPadBack.setOnClickListener {
            if (mETMobileNumber.text.toString().isNotEmpty()) {
                val temp = mETMobileNumber.text.toString()
                mETMobileNumber.setText(temp.substring(0, temp.length - 1))
            }
        }

        return view
    }

    private fun areTenDigitsEntered(): Boolean {
        return mETMobileNumber.text.toString().length == 13
    }

    private fun dialPadNumberClicked(number: String) {
        if (!areTenDigitsEntered()) {
            var temp = mETMobileNumber.text.toString()
            temp = "$temp$number"
            mETMobileNumber.setText(temp)
        }
    }

}