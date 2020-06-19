package org.wycliffeassociates.sourceaudio.upload.model


import java.io.File
import java.lang.IllegalArgumentException

data class FileUploadModel(
    val inputFile: File,
    val languageCode: String,
    val resourceType: String,
    val grouping: String,
    val projectId: String = "",
    val mediaExtension: String = "",
    var mediaQuality: String = ""
) {

    val extension: String = inputFile.extension
    val name: String = inputFile.name

    init {
        validate()
        if(this.mediaQuality.isEmpty()) this.mediaQuality = "hi"
    }

    @Throws(IllegalArgumentException::class)
    private fun validate() {
        if (languageCode.isBlank()) { throw IllegalArgumentException("Language Code is empty") }
        if (resourceType.isBlank()) { throw IllegalArgumentException("Dublin Core ID is empty") }
        if (grouping.isBlank()) { throw IllegalArgumentException("Group is empty") }
        if (!Groupings.isSupported(grouping)) { throw IllegalArgumentException("Group is not supported") }
        if (!MediaQuality.isSupported(mediaQuality)) { throw IllegalArgumentException("Media Quality is invalid") }

        validateExtensions(inputFile.extension, mediaExtension)
    }

    @Throws(IllegalArgumentException::class)
    private fun validateExtensions(fileExtension: String, mediaExtension: String) {
        if (ContainerExtensions.isSupported(fileExtension)) {
            if (mediaExtension.isBlank()) {
                throw IllegalArgumentException("Media Extension is empty")
            }
            if (!CompressedExtensions.isSupported(mediaExtension) && !UncompressedExtensions.isSupported(mediaExtension)) {
                throw IllegalArgumentException(".$mediaExtension file is not supported")
            }
        } else if (!CompressedExtensions.isSupported(fileExtension) && !UncompressedExtensions.isSupported(fileExtension)) {
            throw IllegalArgumentException(".${fileExtension} file is not supported")
        }
    }
}