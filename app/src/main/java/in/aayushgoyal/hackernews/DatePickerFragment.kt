package `in`.aayushgoyal.hackernews

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

import java.util.Calendar

// Fragment that displays a DatePicker with current date as selected.
class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener{
    private var activityName: String? = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activityName = arguments!!.getString("activityName")
        val date = arguments!!.getString("dob")
        val c: Calendar = Calendar.getInstance()
        val year: Int
        val month: Int
        val day: Int

        if(date!!.isNotBlank()) {
            val dateOfBirth = arguments?.getString("dob")
            val dobArray: List<String> = dateOfBirth!!.split("/")
            year = Integer.parseInt(dobArray[2])
            month = Integer.parseInt(dobArray[1]) - 1
            day = Integer.parseInt(dobArray[0])
        } else {
            // Use the current date as the default date in the picker
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity as FragmentActivity, this, year, month, day)
    }

    // This method changes the date to the date selected by the user in the fragment.
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        val tvDate: TextView
        when(activityName) {
            "NewUserDetailsFillUpActivity" -> {
                tvDate = activity!!.findViewById(R.id.et_dob_activity_new_user_details_fill_up)
                val date = "$day/${month+1}/$year"
                tvDate.text = date
            }
            "UpdateDetailsActivity" -> {
                tvDate = activity!!.findViewById(R.id.et_dob_activity_update_user_details)
                val date = "$day/${month+1}/$year"
                tvDate.text = date
            }
            else -> {
                Toast.makeText(activity, "Sorry, could not select a date.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
