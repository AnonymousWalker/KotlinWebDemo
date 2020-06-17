import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.*
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import org.wycliffeassociates.sourceaudio.upload.FilePathGenerator
import org.wycliffeassociates.sourceaudio.upload.model.FilePathTestModel
import java.lang.IllegalArgumentException
import dev.jbs.ktor.thymeleaf.*
import io.ktor.http.content.*
import io.ktor.request.receiveMultipart
import io.netty.handler.codec.http.multipart.FileUpload
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.wycliffeassociates.sourceaudio.upload.model.FileUploadModel
import java.io.File
import java.util.*

fun Application.module() {
    install(DefaultHeaders)
    install(CORS) {
        anyHost()
        header(HttpHeaders.AccessControlAllowOrigin)
    }
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
    install(Routing) {
        routing {
            static("static") {
                files("src/css")
                files("src/js")
                files("src/main/resources/templates")
            }
        }
        route("/") {
            get {
                call.respond(ThymeleafContent("index", mapOf("obj" to ""), locale = Locale("vi")))
            }
            post {
                val data = call.receive<FilePathTestModel>()
                val serializedResult = processFileUpload(data)

                call.respondText(serializedResult, ContentType.Application.Json)
            }
        }
        post("/upload") {
            val multiPart = call.receiveMultipart()
//                val parts = multiPart.readAllParts()
            val dataMap: MutableMap<String, String> = mutableMapOf()
            val fileUploadModel: FileUploadModel
            var file = File("")
            multiPart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        println(part.name + "=" + part.value)
//                            if (part.name == "languageCode")
                        dataMap[part.name!!] = part.value
                    }
                    is PartData.FileItem -> {
                        file = File("./src/main/resources/${part.originalFileName!!}")
                        println(file.path)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use {
                                input.copyTo(it)
                            }
                        }
                    }
                }
                part.dispose()
            }
            val result = processFileUpload(file, dataMap)

            call.respondText(result)
        }
    }
}

private fun processFileUpload(file: File, data: Map<String,String>): String {
    val model = if (data["mediaQuality"]!!.isNotBlank()) {
        FilePathTestModel(
            fileName = "",
            languageCode = data["languageCode"]!!,
            dublinCoreId = data["dublinCoreId"]!!,
            grouping = data["grouping"]!!,
            projectId = data["projectId"]!!,
            mediaExtension = data["mediaExtension"]!!,
            mediaQuality = data["mediaQuality"]!!,
            expectedResult = ""
        )
    } else {
        FilePathTestModel(
            fileName = "",
            languageCode = data["languageCode"]!!,
            dublinCoreId = data["dublinCoreId"]!!,
            grouping = data["grouping"]!!,
            projectId = data["projectId"]!!,
            mediaExtension = data["mediaExtension"]!!,
            expectedResult = ""
        )
    }
    model.inputFile = file
    val result =
        try {
            mapOf(
                "output" to FilePathGenerator.createPathFromFile(model.getFileUploadModel()),
                "success" to true
            )
        } catch (ex: IllegalArgumentException) {
            mapOf(
                "output" to ex.message!!,
                "success" to false
            )
        }
    val mapper = jacksonObjectMapper()

    return mapper.writeValueAsString(result)
}


private fun processFileUpload(data: FilePathTestModel): String {
    val model = if (data.mediaQuality.isNotBlank()) {
        FilePathTestModel(
            fileName = data.fileName,
            languageCode = data.languageCode,
            dublinCoreId = data.dublinCoreId,
            projectId = data.projectId,
            grouping = data.grouping,
            mediaExtension = data.mediaExtension,
            mediaQuality = data.mediaQuality,
            expectedResult = ""
        )
    } else {
        FilePathTestModel(
            fileName = data.fileName,
            languageCode = data.languageCode,
            dublinCoreId = data.dublinCoreId,
            projectId = data.projectId,
            grouping = data.grouping,
            mediaExtension = data.mediaExtension,
            expectedResult = ""
        )
    }

    val result =
        try {
            mapOf(
                "output" to FilePathGenerator.createPathFromFile(model.getFileUploadModel()),
                "success" to true
            )
        } catch (ex: IllegalArgumentException) {
            mapOf(
                "output" to ex.message!!,
                "success" to false
            )
        }
    val mapper = jacksonObjectMapper()

    return mapper.writeValueAsString(result)
}
