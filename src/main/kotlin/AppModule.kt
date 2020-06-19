import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import dev.jbs.ktor.thymeleaf.*
import io.ktor.http.content.*
import io.ktor.request.acceptLanguage
import io.ktor.request.receiveMultipart
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.wycliffeassociates.sourceaudio.routing.SupportedLanguages
import org.wycliffeassociates.sourceaudio.upload.RequestHandler
import org.wycliffeassociates.sourceaudio.upload.TemplateModel
import org.wycliffeassociates.sourceaudio.upload.model.CompressedExtensions
import org.wycliffeassociates.sourceaudio.upload.model.Groupings
import org.wycliffeassociates.sourceaudio.upload.model.MediaQuality
import org.wycliffeassociates.sourceaudio.upload.model.UncompressedExtensions
import java.util.*

fun Application.module() {
    install(DefaultHeaders)
    install(CORS) {
        anyHost()
        header(HttpHeaders.AccessControlAllowOrigin)
    }
    install(CallLogging)
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
        get("/") {
            val contentLanguage = Locale.LanguageRange.parse(call.request.acceptLanguage())
            val templateModel = TemplateModel()

            call.respond(
                ThymeleafContent(
                    template = "index",
                    model = mapOf(
                        "qualityList" to templateModel.qualityList,
                        "groupingList" to templateModel.groupingList,
                        "extensionList" to templateModel.extensionList
                    ),
                    locale = getPreferredLocale(contentLanguage)
                )
            )
        }
        post("/upload") {
            val multiPart = call.receiveMultipart().readAllParts()
            val result = RequestHandler().handleFormUpload(multiPart)

            call.respondText(result, ContentType.Application.Json)
        }
    }
}

fun getPreferredLocale(languageRanges: List<Locale.LanguageRange>): Locale {
    var language = ""
    for (range in languageRanges) {
        language = range.range.split("-")[0]
        if (SupportedLanguages.isSupported(language)) break
    }
    if (!SupportedLanguages.isSupported(language)) language = SupportedLanguages.getDefault()

    return Locale(language)
}