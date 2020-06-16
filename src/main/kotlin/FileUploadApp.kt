import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.DefaultHeaders
import io.ktor.http.*
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.thymeleaf.Thymeleaf
import io.ktor.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.wycliffeassociates.sourceaudio.upload.FilePathGenerator
import org.wycliffeassociates.sourceaudio.upload.model.FilePathTestModel
import java.io.File
import java.lang.IllegalArgumentException

fun Application.module() {
    install(DefaultHeaders)
    install(CORS) {
        anyHost()
        header(HttpHeaders.AccessControlAllowOrigin)
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
            }
        }
        route("/") {
            get {
                val html = File("./src/index.html").readText()
                call.respondText(html, ContentType.Text.Html)
            }
            post {
                val data = call.receive<FilePathTestModel>()
                val serializedResult = processFileUpload(data)

                call.respondText(serializedResult, ContentType.Application.Json)
            }
        }
        get("/greeting") {
            call.respond(ThymeleafContent("greeting_page", mapOf("user" to "asdf")))
        }
    }
}

fun processFileUpload(data: FilePathTestModel): String {
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
    val serializedResult = mapper.writeValueAsString(result)

    return serializedResult
}

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080, watchPaths = listOf("KotlinWeb"), module = Application::module)
    server.start(wait = true)
}