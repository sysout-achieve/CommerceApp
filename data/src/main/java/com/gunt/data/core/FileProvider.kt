package com.gunt.data.core

internal interface FileProvider {
    fun getJsonFromAsset(filePath: String): String?
}
