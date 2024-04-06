package com.hirno.explorer.util.string.parser

import com.hirno.explorer.model.term.SearchTerm

interface SearchTermParser {
    val terms: List<SearchTerm>
}