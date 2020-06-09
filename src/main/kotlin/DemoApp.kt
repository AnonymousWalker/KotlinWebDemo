import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.wycliffeassociates.sourceaudio.upload.FilePathGenerator
import org.wycliffeassociates.sourceaudio.upload.model.FilePathTestModel
import java.io.File
import java.lang.IllegalArgumentException


fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Routing) {
        route("/") {
            get {
                val html = File("./src/index.html").readText()
                call.respondText(html, ContentType.Text.Html)
            }
            post {
                val data = call.receiveParameters()
                val model = FilePathTestModel(
                    fileName = data["filePath"]!!,
                    languageCode =  data["languageCode"]!!,
                    dublinCoreId =  data["dublinCoreId"]!!,
                    projectId = data["projectId"]!!,
                    grouping =  data["grouping"]!!,
                    mediaExtension = data["mediaExtension"]!!,
                    mediaQuality = data["mediaQuality"]!!,
                    expectedResult = ""
                )
                val result = try {
                    FilePathGenerator.createPathFromFile(model.getFileUploadModel())
                } catch (ex: IllegalArgumentException) {
                    ex.message!!
                }
                call.respondText(result, ContentType.Text.Html)
            }
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, 4567, watchPaths = listOf("KotlinWeb"), module = Application::module).start()
}