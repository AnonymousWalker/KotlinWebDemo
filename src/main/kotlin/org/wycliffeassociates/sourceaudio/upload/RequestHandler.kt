package org.wycliffeassociates.sourceaudio.upload

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import org.wycliffeassociates.sourceaudio.upload.model.FileUploadModel
import java.io.File
import java.lang.IllegalArgumentException

class RequestHandler {

    fun handleFormUpload(parts: List<PartData>): String {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        var file = File("./src/main/resources/uploads/")

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
        try {
            val model = createModel(file, params)
            response.putAll(
                mapOf(
                    "output" to FilePathGenerator.createPathFromFile(model),
                    "success" to true
                )
            )
        } catch (ex: IllegalArgumentException) {
            response.putAll(
                mapOf(
                    "output" to ex.message!!,
                    "success" to false
                )
            )
        }

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