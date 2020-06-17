package org.wycliffeassociates.sourceaudio.upload.model

import java.io.File

data class FilePathTestModel(
    val fileName: String,
    val languageCode: String,
    val dublinCoreId: String,
    val grouping: String,
    val expectedResult: String,
    val projectId: String = "",
    val mediaExtension: String = "",
    val mediaQuality: String = "hi"
) {
    var inputFile: File = File(fileName)

    fun getFileUploadModel(): FileUploadModel {
        return FileUploadModel(inputFile, languageCode, dublinCoreId, grouping, projectId, mediaExtension, mediaQuality)
    }
}