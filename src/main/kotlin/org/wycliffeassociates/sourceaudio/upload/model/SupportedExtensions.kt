package org.wycliffeassociates.sourceaudio.upload.model

interface SupportedExtensions {
    fun isSupported(extension: String): Boolean
}