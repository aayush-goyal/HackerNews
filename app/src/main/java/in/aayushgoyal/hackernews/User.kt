package `in`.aayushgoyal.hackernews

// Data class for storing basic details of a user.
data class User(var country: String,
                var dateOfBirth: Long,
                var email: String,
                var mobileNumber: String,
                var firstName: String,
                var middleName: String,
                var lastName: String,
                var userUID: String)