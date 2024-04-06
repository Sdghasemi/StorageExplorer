package com.hirno.explorer.util.string.matcher

import java.io.File

interface FileMatcher {
    fun matches(dir: File, name: String): Boolean
}