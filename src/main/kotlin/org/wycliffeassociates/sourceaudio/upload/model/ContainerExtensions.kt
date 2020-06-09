package org.wycliffeassociates.sourceaudio.upload.model

enum class ContainerExtensions(ext: String) {
    TR("tr");

    companion object : SupportedExtensions {
        override fun isSupported(extension: String): Boolean = values().any { it.name == extension.toUpperCase() }
    }
}