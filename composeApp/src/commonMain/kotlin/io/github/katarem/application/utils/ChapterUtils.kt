package io.github.katarem.application.utils

import io.github.katarem.data.model.Chapter

fun extractLeadingNumber(text: String): Double? {
    val regex = Regex("""\d+(\.\d+)?""")
    return regex.find(text)?.value?.toDoubleOrNull()
}

fun List<Chapter>.sortByChapter(): List<Chapter> {
    return this.sortedWith(
        compareBy(
            { extractLeadingNumber(it.chapterNumber) },
            { it.chapterNumber }
        ))
}