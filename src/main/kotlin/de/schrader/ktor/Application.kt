package de.schrader.ktor

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import de.schrader.ktor.api.repository.PersonRepository
import de.schrader.ktor.api.repository.PersonRepositoryImpl
import de.schrader.ktor.auth.UserRepository
import de.schrader.ktor.auth.UserRepositoryImpl
import de.schrader.ktor.common.Database
import de.schrader.ktor.api.service.PersonService
import de.schrader.ktor.api.service.PersonServiceImpl
import de.schrader.ktor.auth.Session
import de.schrader.ktor.auth.hash
import de.schrader.ktor.webapp.about
import de.schrader.ktor.webapp.home
import de.schrader.ktor.webapp.signin
import de.schrader.ktor.webapp.signout
import de.schrader.ktor.webapp.signup
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.freemarker.FreeMarker
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level
import de.schrader.ktor.api.controller.person as person_api
import de.schrader.ktor.webapp.person as person_webapp

const val API_VERSION = "v1"
const val API_PREFIX = "/api/$API_VERSION"

fun Application.main() {

    log.info("Starting application ...")

    install(DefaultHeaders)

    install(StatusPages) {
        exception<Throwable> { ex ->
            call.respondText(ex.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            throw ex
        }
    }

    install(ContentNegotiation) {
        // gson {
        //    setPrettyPrinting()
        // }
        jackson {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
        // moshi()
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

//    install(Authentication) {
//        basic(name = "auth") {
//            realm = "Ktor Server"
//            validate { credentials ->
//                if (credentials.password == "${credentials.name}123") User(credentials.name) else null
//            }
//        }
//    }

    install(CallLogging) {
        level = Level.INFO
        // if filter returns true, the call is logged; if no filters are defined, everything is logged
        filter { call -> call.request.path().startsWith(API_PREFIX) }
//        format {
//            "${it.request.httpMethod.value} ${it.request.path()}} => ${it.response.status()}"
//        }
    }

    install(Koin) {
        printLogger()
        modules(appModule)
    }

    install(Locations)

    install(Sessions) {
        cookie<Session>("COOKIE_NAME")
    }

    Database.init()

    val hashFunction = { password: String -> hash(password) }

    routing {
        static("/static") {
            resources("images")
        }
//        authenticate("auth") {
        home()
        about()

        signup(hashFunction)
        signin(hashFunction)
        signout()

        person_api()
        person_webapp()

//          post<Person> { person ->
//            when (val thing = personService.create(person)) {
//                is Some -> call.respond(HttpStatusCode.Created, thing.value)
//                is None -> call.respond(HttpStatusCode.InternalServerError)
//            }
//        }
//        }
    }
}

private val appModule = module {
    single<PersonService> { PersonServiceImpl(get()) } // get() resolves PersonRepository
    single<PersonRepository> { PersonRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl() }
}
