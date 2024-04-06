package com.hirno.explorer.util.string.parser

import com.hirno.explorer.model.term.SearchTerm

class SearchTermParserImpl(
    val searchTerm: String
): SearchTermParser {
    override val terms = run {
        searchTerm.split(' ').mapNotNull { word ->
            SearchTerm.create(word).takeIf { it.isValid }
        }
    }
}