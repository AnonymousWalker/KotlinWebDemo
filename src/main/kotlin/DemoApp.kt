import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.*
import io.ktor.http.content.TextContent
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.wycliffeassociates.sourceaudio.upload.FilePathGenerator
import org.wycliffeassociates.sourceaudio.upload.model.FilePathTestModel
import java.lang.IllegalArgumentException


fun Application.module() {
    install(CORS) {
        anyHost()
        header(HttpHeaders.AccessControlAllowOrigin)
    }
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Routing) {
        routing {
            static("static") {
                files("src/css")
                files("src/js")
                default("src/index.html")
            }
        }
        route("/") {
            post {
                val data = call.receive<FilePathTestModel>()
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

                call.respondText(serializedResult, ContentType.Application.Json)
            }
        }
    }
}

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 4567, watchPaths = listOf("KotlinWeb"), module = Application::module)
    server.start(wait = true)
}