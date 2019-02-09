package `in`.aayushgoyal.hackernews

import java.io.Serializable

class Story(val by: String,
            val descendants: Int = 0,
            val id: Long,
            val kids: Array<Long>,
            val parent: Long = 0,
            val score: Int = 0,
            val time: Long,
            val text: String = "",
            val title: String = "",
            val type: String,
            val url: String = "") : Serializable