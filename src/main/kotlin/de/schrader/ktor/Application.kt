package de.schrader.ktor

import de.schrader.ktor.api.model.login
import de.schrader.ktor.api.repository.PersonRepository
import de.schrader.ktor.api.repository.PersonRepositoryImpl
import de.schrader.ktor.api.service.PersonService
import de.schrader.ktor.api.service.PersonServiceImpl
import de.schrader.ktor.auth.Session
import de.schrader.ktor.auth.User
import de.schrader.ktor.auth.UserRepository
import de.schrader.ktor.auth.UserRepositoryImpl
import de.schrader.ktor.auth.hash
import de.schrader.ktor.auth.hashKey
import de.schrader.ktor.common.Database
import de.schrader.ktor.webapp.about
import de.schrader.ktor.webapp.home
import de.schrader.ktor.webapp.signin
import de.schrader.ktor.webapp.signout
import de.schrader.ktor.webapp.signup
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.freemarker.FreeMarker
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.Locations
import io.ktor.locations.locations
import io.ktor.request.header
import io.ktor.request.host
import io.ktor.request.path
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import org.koin.dsl.module
import org.koin.experimental.builder.single
import org.koin.experimental.builder.singleBy
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.slf4j.event.Level
import java.net.URI
import java.util.concurrent.TimeUnit
import de.schrader.ktor.api.controller.person as api_person
import de.schrader.ktor.webapp.person as webapp_person

const val API_VERSION = "v1"
const val API_PREFIX = "/api/$API_VERSION"

const val HOME_ROUTE = "/"
const val ABOUT_ROUTE = "/about"
const val SIGNIN_ROUTE = "/signin"
const val SIGNUP_ROUTE = "/signup"
const val SIGNOUT_ROUTE = "/signout"

const val LOGIN_ENDPOINT = "/login"
const val PERSONS_ENDPOINT = "/persons"

const val SESSION_COOKIE_NAME = "KTOR_SESSION"

fun Application.main() {

    val jwtService: JwtService by inject()
    val userRepository: UserRepository by inject()

    log.info("Starting application ...")

    install(Koin) {
        printLogger()
        modules(appModule)
    }

    install(DefaultHeaders)

    install(StatusPages) {
        exception<Throwable> { ex ->
            call.respondText(ex.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            throw ex
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }

        // jackson {
        //    setSerializationInclusion(JsonInclude.Include.NON_NULL)
        //    configure(SerializationFeature.INDENT_OUTPUT, true)
        // }

        // moshi()
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Authentication) {

        // basic(name = "auth") {
        //     realm = "Ktor DEmo"
        //     validate { credentials ->
        //         if (credentials.password == "${credentials.name}123") User(credentials.name) else null
        //     }
        // }

        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Ktor Demo"
            validate {
                val userId = it.payload.getClaim("id").asString()
                userRepository.findById(userId)
            }
        }
    }

    install(Sessions) {
        cookie<Session>(SESSION_COOKIE_NAME) {
            transform(SessionTransportTransformerMessageAuthentication(hashKey))
        }
    }

    install(CallLogging) {
        level = Level.INFO
        // if filter returns true, the call is logged; if no filters are defined, everything is logged
        filter { call -> call.request.path().startsWith(API_PREFIX) }
//        format {
//            "${it.request.httpMethod.value} ${it.request.path()}} => ${it.response.status()}"
//        }
    }

    install(Locations)

    Database.init()

    val hashFunction = { password: String -> hash(password) }

    routing {

        static("/static") {
            resources("images")
        }

        // web app
        home()
        about()
        signup(hashFunction)
        signin(hashFunction)
        signout()
        // authenticate("auth") {
        webapp_person(hashFunction)
        // }

        // API
        login()
        authenticate("jwt") {
            api_person()
        }
    }
}

suspend fun ApplicationCall.redirect(location: Any) =
    respondRedirect(/*application.*/locations.href(location))

fun ApplicationCall.referrerHost() =
    request.header(HttpHeaders.Referrer)?.let { URI.create(it).host }

fun ApplicationCall.securityCode(date: Long, user: User, hashFunction: (String) -> String) =
    hashFunction("$date:${user.userId}:${request.host()}:${referrerHost()}")

fun ApplicationCall.verifyCode(date: Long, user: User, code: String, hashFunction: (String) -> String) =
    securityCode(date, user, hashFunction) == code &&
            (System.currentTimeMillis() - date).let { it > 0 && it < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS) }

private val appModule = module(createdAtStart = true) {
    singleBy<PersonService, PersonServiceImpl>()
    singleBy<PersonRepository, PersonRepositoryImpl>()
    singleBy<UserRepository, UserRepositoryImpl>()
    single<JwtService>()
}
