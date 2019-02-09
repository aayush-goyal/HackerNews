package `in`.aayushgoyal.hackernews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

import java.util.concurrent.TimeUnit

class PhoneNumberVerificationFragment: Fragment() {

    private var phoneNumber: String = ""
    private var code = ""
    private lateinit var mButtonDialPad0: Button
    private lateinit var mButtonDialPad1: Button
    private lateinit var mButtonDialPad2: Button
    private lateinit var mButtonDialPad3: Button
    private lateinit var mButtonDialPad4: Button
    private lateinit var mButtonDialPad5: Button
    private lateinit var mButtonDialPad6: Button
    private lateinit var mButtonDialPad7: Button
    private lateinit var mButtonDialPad8: Button
    private lateinit var mButtonDialPad9: Button
    private lateinit var mButtonDialPadBack: Button
    private lateinit var mButtonVerify: Button
    private lateinit var mEditTextOTP1: EditText
    private lateinit var mEditTextOTP2: EditText
    private lateinit var mEditTextOTP3: EditText
    private lateinit var mEditTextOTP4: EditText
    private lateinit var mEditTextOTP5: EditText
    private lateinit var mEditTextOTP6: EditText
    private lateinit var mTVResendCode: TextView

    private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_phone_number_verification, container, false)

        mEditTextOTP1 = view.findViewById(R.id.edit_text_otp_1)
        mEditTextOTP2 = view.findViewById(R.id.edit_text_otp_2)
        mEditTextOTP3 = view.findViewById(R.id.edit_text_otp_3)
        mEditTextOTP4 = view.findViewById(R.id.edit_text_otp_4)
        mEditTextOTP5 = view.findViewById(R.id.edit_text_otp_5)
        mEditTextOTP6 = view.findViewById(R.id.edit_text_otp_6)
        mTVResendCode = view.findViewById(R.id.tv_resend_code)
        mButtonDialPad0 = view.findViewById(R.id.button_dialpad_0_fragment_phone_number_verification)
        mButtonDialPad1 = view.findViewById(R.id.button_dialpad_1_fragment_phone_number_verification)
        mButtonDialPad2 = view.findViewById(R.id.button_dialpad_2_fragment_phone_number_verification)
        mButtonDialPad3 = view.findViewById(R.id.button_dialpad_3_fragment_phone_number_verification)
        mButtonDialPad4 = view.findViewById(R.id.button_dialpad_4_fragment_phone_number_verification)
        mButtonDialPad5 = view.findViewById(R.id.button_dialpad_5_fragment_phone_number_verification)
        mButtonDialPad6 = view.findViewById(R.id.button_dialpad_6_fragment_phone_number_verification)
        mButtonDialPad7 = view.findViewById(R.id.button_dialpad_7_fragment_phone_number_verification)
        mButtonDialPad8 = view.findViewById(R.id.button_dialpad_8_fragment_phone_number_verification)
        mButtonDialPad9 = view.findViewById(R.id.button_dialpad_9_fragment_phone_number_verification)
        mButtonDialPadBack = view.findViewById(R.id.button_dialpad_back_fragment_phone_number_verification)
        mButtonVerify = view.findViewById(R.id.button_verify_fragment_phone_no_verification)

        mTVResendCode.setOnClickListener { resendVerificationCode(phoneNumber, (activity as PhoneAuthActivity).mResendToken) }

        mButtonDialPad0.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_0)) }

        mButtonDialPad1.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_1)) }

        mButtonDialPad2.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_2)) }

        mButtonDialPad3.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_3)) }

        mButtonDialPad4.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_4)) }

        mButtonDialPad5.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_5)) }

        mButtonDialPad6.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_6)) }

        mButtonDialPad7.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_7)) }

        mButtonDialPad8.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_8)) }

        mButtonDialPad9.setOnClickListener { dialPadNumberClicked(getString(R.string.dialpad_9)) }

        mButtonDialPadBack.setOnClickListener {
            if(!mEditTextOTP6.text.toString().isEmpty())
                mEditTextOTP6.setText("")
            else if(!mEditTextOTP5.text.toString().isEmpty())
                mEditTextOTP5.setText("")
            else if(!mEditTextOTP4.text.toString().isEmpty())
                mEditTextOTP4.setText("")
            else if(!mEditTextOTP3.text.toString().isEmpty())
                mEditTextOTP2.setText("")
            else if(!mEditTextOTP2.text.toString().isEmpty())
                mEditTextOTP2.setText("")
            else if(!mEditTextOTP1.text.toString().isEmpty())
                mEditTextOTP1.setText("")
        }

        mButtonVerify.setOnClickListener {
            if (!(mEditTextOTP1.text.toString().isEmpty() || mEditTextOTP2.text.toString().isEmpty() ||
                            mEditTextOTP3.text.toString().isEmpty() || mEditTextOTP4.text.toString().isEmpty() ||
                            mEditTextOTP5.text.toString().isEmpty() || mEditTextOTP6.text.toString().isEmpty())) {
                // If all the EditText fields have been filled, then proceed with the normal Sign-In flow.
                code = mEditTextOTP1.text.toString() + mEditTextOTP2.text.toString() + mEditTextOTP3.text.toString() +
                        mEditTextOTP4.text.toString() + mEditTextOTP5.text.toString() + mEditTextOTP6.text.toString()

                (activity as PhoneAuthActivity).mProgressBar.show()

                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        (activity as PhoneAuthActivity).mVerificationId, code)
                (activity as PhoneAuthActivity).signInWithPhoneAuthCredential(credential)
            }

        }

        return view
    }

    // Resend the verification code to the user's mobile.
    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,                                // Phone number to verify
                60,                                         // Timeout duration
                TimeUnit.SECONDS,                           // Unit of timeout
                activity as PhoneAuthActivity,              // Activity (for callback binding)
                mCallbacks,                                 // OnVerificationStateChangedCallbacks
                token)                                      // ForceResendingToken from callbacks
    }

    private fun dialPadNumberClicked(number: String) {
        if(mEditTextOTP1.text.toString().isEmpty())
            mEditTextOTP1.setText(number)
        else if(mEditTextOTP2.text.toString().isEmpty())
            mEditTextOTP2.setText(number)
        else if(mEditTextOTP3.text.toString().isEmpty())
            mEditTextOTP3.setText(number)
        else if(mEditTextOTP4.text.toString().isEmpty())
            mEditTextOTP4.setText(number)
        else if(mEditTextOTP5.text.toString().isEmpty())
            mEditTextOTP5.setText(number)
        else if(mEditTextOTP6.text.toString().isEmpty())
            mEditTextOTP6.setText(number)
    }


}