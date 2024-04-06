package com.hirno.explorer.util.string.matcher

import com.hirno.explorer.model.term.SearchTerm
import com.hirno.explorer.util.string.parser.SearchTermParser
import java.io.File

class FileMatcherImpl(
    private val parser: SearchTermParser
): FileMatcher {
    override fun matches(dir: File, name: String): Boolean {
        val targets = listOf(dir.name, name)
        return parser.terms.map { term ->
            when (term.type) {
                SearchTerm.Type.EXCLUDE -> targets.forEach { target ->
                    if (!target.contains(term.word, ignoreCase = true)) {
                        return@map true
                    }
                }
                SearchTerm.Type.EXACT -> targets.forEach { target ->
                    if (target.split(' ').contains(term.word)) {
                        return@map true
                    }
                }
                SearchTerm.Type.CONTAINS -> targets.forEach { target ->
                    if (target.contains(term.word, ignoreCase = true)) {
                        return@map true
                    }
                }
            }
            return@map false
        }.all { it }
    }
}