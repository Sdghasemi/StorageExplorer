package com.hirno.explorer.model.term

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchTerm(
    val word: String = "",
    val type: Type = Type.CONTAINS,
): Parcelable {
    @IgnoredOnParcel
    val isValid = when (type) {
        Type.CONTAINS -> word.length >= 2
        else -> word.isNotEmpty()
    }

    enum class Type {
        CONTAINS,
        EXACT,
        EXCLUDE,
    }

    companion object {
        fun create(term: String) = when {
            term.startsWith('-') -> SearchTerm(
                word = term.substring(1),
                type = Type.EXCLUDE
            )
            term.startsWith('"') && term.endsWith('"') -> SearchTerm(
                word = term.substring(1, term.lastIndex),
                type = Type.EXACT
            )
            else -> SearchTerm(
                word = term,
                type = Type.CONTAINS
            )
        }
    }
}
