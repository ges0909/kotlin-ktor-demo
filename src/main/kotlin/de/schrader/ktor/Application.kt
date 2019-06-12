package de.schrader.ktor

import arrow.core.getOrElse
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.schrader.ktor.controller.persons
import de.schrader.ktor.model.Person
import de.schrader.ktor.model.auth.User
import de.schrader.ktor.repository.PersonRepository
import de.schrader.ktor.repository.PersonRepositoryImpl
import de.schrader.ktor.service.PersonService
import de.schrader.ktor.service.PersonServiceImpl
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.basic
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.slf4j.event.Level

const val API_PATH = "/api"
const val API_VERSION = "v1"

@KtorExperimentalLocationsAPI
fun Application.main() {
    // log.info("Install features")

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
        filter { call -> call.request.path().startsWith(API_PATH) }
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

    val personService: PersonService by inject()

    routing {
        authenticate("auth") {

            persons() // api

            get("/persons") {
                val user = call.authentication.principal as User
                val persons = personService.findAll().getOrElse { emptyArray<List<Person>>() }
                call.respond(
                    FreeMarkerContent(
                        "persons.ftl", mapOf(
                            "displayName" to user.displayName,
                            "persons" to persons
                        )
                    )
                )
            }
            post("/persons") {
                val params = call.receiveParameters()
                val name = params["name"] ?: throw IllegalArgumentException("Missing parameter: name")
                val age = params["age"] ?: throw IllegalArgumentException("Missing parameter: age")
                personService.create(Person(name = name, age = age.toInt()))
                call.respondRedirect("/persons")
            }
        }

//        post<Person> { person ->
//            when (val thing = personService.create(person)) {
//                is Some -> call.respond(HttpStatusCode.Created, thing.value)
//                is None -> call.respond(HttpStatusCode.InternalServerError)
//            }
//        }

    }
}

private val appModule = module {
    single<PersonService> { PersonServiceImpl(get()) } // get() resolves PersonRepository
    single<PersonRepository> { PersonRepositoryImpl() }
}

private fun hikari(): HikariDataSource {
    val config = HikariConfig()
    // config.driverClassName = "org.h2.Driver"
    // config.jdbcUrl = "jdbc:h2:~/test;DATABASE_TO_UPPER=false"
    config.jdbcUrl = "jdbc:h2:mem:test;DATABASE_TO_UPPER=false"
    config.maximumPoolSize = 3
    config.isAutoCommit = false
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    config.validate()
    return HikariDataSource(config)
}
