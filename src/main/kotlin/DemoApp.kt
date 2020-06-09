import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.*
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
                val model = FilePathTestModel(
                    fileName = data.fileName,
                    languageCode =  data.languageCode,
                    dublinCoreId =  data.dublinCoreId,
                    grouping =  data.grouping,
                    mediaExtension = data.mediaExtension,
                    mediaQuality = data.mediaQuality,
                    expectedResult = ""
                )
                val result = FilePathGenerator.createPathFromFile(model.getFileUploadModel())
                call.respondText(result)
            }
        }
    }
}

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 4567, watchPaths = listOf("KotlinWeb"), module = Application::module)
    server.start(wait = true)
}