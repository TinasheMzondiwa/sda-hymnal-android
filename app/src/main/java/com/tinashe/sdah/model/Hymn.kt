package com.tinashe.sdah.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by tinashe on 2017/07/02.
 */

class Hymn(
        @SerializedName("number")
        var number: Int = 0,

        @SerializedName("title")
        var title: String? = null,

        @SerializedName("content")
        var content: String? = null,

        @SerializedName("edited")
        var edited: String? = null,

        @SerializedName("composer")
        val composer: String? = null,

        @SerializedName("author")
        val author: String? = null,

        @SerializedName("tempo")
        val tempo: String? = null,

        @SerializedName("key")
        val key: String? = null,

        val favorite: Boolean? = false
) : Serializable {
    override fun toString(): String {
        return "Hymn(number=$number, title=$title)"
    }
}
