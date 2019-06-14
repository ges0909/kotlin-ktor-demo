package de.schrader.ktor

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.schrader.ktor.model.auth.User
import de.schrader.ktor.repository.PersonRepository
import de.schrader.ktor.repository.PersonRepositoryImpl
import de.schrader.ktor.service.PersonService
import de.schrader.ktor.service.PersonServiceImpl
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.basic
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
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.response.respondText
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.slf4j.event.Level
import de.schrader.ktor.controller.person as person_api
import de.schrader.ktor.webapp.person as person_webapp

const val API_VERSION = "v1"
const val API_PREFIX = "/api/$API_VERSION"
const val WEBAPP_PREFIX = "/webapp"

@KtorExperimentalLocationsAPI
fun Application.main() {

    log.info("Starting application ...")

    install(DefaultHeaders)

    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
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

    install(Authentication) {
        basic(name = "auth") {
            realm = "Ktor Server"
            validate { credentials ->
                if (credentials.password == "${credentials.name}123") User(credentials.name) else null
            }
        }
    }

    install(Koin) {
        modules(appModule)
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

    val db = Database.connect(hikari())
//    transaction {
//        addLogger(StdOutSqlLogger)
//    }
    val personRepository: PersonRepository by inject()
    personRepository.createTable()

    routing {
        static("/static") {
            resources("images")
        }
        authenticate("auth") {
            person_api()
            person_webapp()
//          post<Person> { person ->
//            when (val thing = personService.create(person)) {
//                is Some -> call.respond(HttpStatusCode.Created, thing.value)
//                is None -> call.respond(HttpStatusCode.InternalServerError)
//            }
//        }
        }
    }
}

private val appModule = module {
    single<PersonService> { PersonServiceImpl(get()) } // get() resolves PersonRepository
    single<PersonRepository> { PersonRepositoryImpl() }
}

private fun hikari(): HikariDataSource {
    val config = HikariConfig()
    config.driverClassName = "org.h2.Driver"
    // config.jdbcUrl = "jdbc:h2:~/test;DATABASE_TO_UPPER=false"
    config.jdbcUrl = "jdbc:h2:mem:test;DATABASE_TO_UPPER=false"
    config.maximumPoolSize = 3
    config.isAutoCommit = false
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    config.validate()
    return HikariDataSource(config)
}
