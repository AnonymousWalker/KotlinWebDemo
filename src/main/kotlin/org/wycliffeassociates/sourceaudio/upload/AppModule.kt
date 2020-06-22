package org.wycliffeassociates.sourceaudio.upload

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
import java.util.*

fun Application.appModule() {
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
                resources("css")
                resources("js")
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
    val noFallbackController = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)

    for(languageRange in languageRanges) {
        val locale = Locale.Builder().setLanguageTag(languageRange.range).build()
        try {
            ResourceBundle.getBundle("templates/index", locale, noFallbackController)
            return locale
        } catch (ex: Exception) { ex.printStackTrace() }
    }

    return Locale.getDefault()
}

val Application.envKind get() = environment.config.property("ktor.environment").getString()