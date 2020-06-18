package org.wycliffeassociates.sourceaudio.upload

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import org.wycliffeassociates.sourceaudio.upload.model.FileUploadModel
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException

class RequestHandler {

    fun handleFormUpload(parts: List<PartData>): String {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        var file = File("./src/main/resources/uploads/").apply { mkdirs() }

        parts.forEach { part ->
            when (part) {
                is PartData.FormItem -> {
                    paramsMap[part.name!!] = part.value
                }
                is PartData.FileItem -> {
                    file = file.resolve(part.originalFileName!!)
                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use {
                            input.copyTo(it)
                        }
                    }
                }
            }
            part.dispose()
        }

        return processUploadModel(file, paramsMap)
    }

    private fun processUploadModel(file: File, params: Map<String, String>): String {
        val response = mutableMapOf<String, Any>()
        var output: String = ""
        var isSuccess: Boolean = true

        try {
            val model = createModel(file, params)

            output = FilePathGenerator.createPathFromFile(model)

            val newDirectory = File("./src/main/resources/uploads/$output")
            file.copyTo(newDirectory)  //this is where the file is stored on server
        } catch (ex: Exception) {
            output = if (ex is FileSystemException) ex.reason!! else ex.message!!
            isSuccess = false
        } finally {
            file.delete()
        }

        response.putAll(
            mapOf(
                "output" to output,
                "success" to isSuccess
            )
        )

        return jacksonObjectMapper().writeValueAsString(response)
    }

    @Throws(IllegalArgumentException::class)
    private fun createModel(file: File, params: Map<String, String>): FileUploadModel {
        return if (params["mediaQuality"]!!.isNotBlank()) {
            FileUploadModel(
                inputFile = file,
                languageCode = params["languageCode"]!!,
                resourceType = params["resourceType"]!!,
                grouping = params["grouping"]!!,
                projectId = params["projectId"]!!,
                mediaExtension = params["mediaExtension"]!!,
                mediaQuality = params["mediaQuality"]!!
            )
        } else {
            FileUploadModel(
                inputFile = file,
                languageCode = params["languageCode"]!!,
                resourceType = params["resourceType"]!!,
                grouping = params["grouping"]!!,
                projectId = params["projectId"]!!,
                mediaExtension = params["mediaExtension"]!!
            )
        }
    }
}