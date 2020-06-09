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
import io.netty.handler.codec.http.multipart.FileUpload
import org.wycliffeassociates.sourceaudio.upload.FilePathGenerator
import org.wycliffeassociates.sourceaudio.upload.model.FilePathTestModel
import org.wycliffeassociates.sourceaudio.upload.model.FileUploadModel
import java.io.File


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

//                val html = File("./src/main/kotlin/webapp/index.html").readText()
//                call.respondText(html, ContentType.Text.Html)
            }
            post {
                val data = call.receive<FilePathTestModel>()
                val model = FilePathTestModel(
                    fileName = "Filename",
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
    embeddedServer(Netty, 4567, watchPaths = listOf("KotlinWeb"), module = Application::module).start()
}