package org.wycliffeassociates.sourceaudio.routing

enum class SupportedLanguages(vararg val ext: String) {
    ENGLISH("en", "en-US"),
    VIETNAMESE("vi");

    companion object {
        fun isSupported(extension: String): Boolean {
            return values().any {
                it.name == extension.toUpperCase() || it.ext.contains(extension)
            }
        }

        fun getDefault(): String = "en"
    }
}