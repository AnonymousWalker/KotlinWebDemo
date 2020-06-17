import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.*
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import org.wycliffeassociates.sourceaudio.upload.FilePathGenerator
import org.wycliffeassociates.sourceaudio.upload.model.FilePathTestModel
import java.lang.IllegalArgumentException
import dev.jbs.ktor.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
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
                call.respond(ThymeleafContent("index", mapOf("obj" to "")))
            }
            post {
                val data = call.receive<FilePathTestModel>()
                val serializedResult = processFileUpload(data)

                call.respondText(serializedResult, ContentType.Application.Json)
            }
        }
        get("/greeting") {
            call.respond(ThymeleafContent("greeting_page", mapOf("user" to ""), locale = Locale.FRENCH))
        }
    }
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

