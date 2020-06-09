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

data class Person(val name: String, val age: Int)

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
            route("get") {
                get {
                    call.respondText { "GET method from: ${call.request.path()}" }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, 4567, watchPaths = listOf("KotlinWeb"), module = Application::module).start()
}