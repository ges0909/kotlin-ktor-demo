package de.schrader.ktor

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.schrader.ktor.controller.persons
import de.schrader.ktor.repository.PersonRepository
import de.schrader.ktor.repository.PersonRepositoryImpl
import de.schrader.ktor.service.PersonService
import de.schrader.ktor.service.PersonServiceImpl
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.slf4j.event.Level

@KtorExperimentalLocationsAPI
fun Application.main() {
    // log.info("Install features")

    install(StatusPages)
    install(DefaultHeaders)
    install(ContentNegotiation) {
        //        gson {
//            setPrettyPrinting()
//        }
        jackson {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }
    install(Koin) {
        modules(appModule)
    }
    install(CallLogging) {
        level = Level.INFO
        // if filter returns true, the call is logged; if no filters are defined, everything is logged
        filter { call -> call.request.path().startsWith("/person") }
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
        persons()
//        post<Person> { person ->
//            when (val thing = personService.create(person)) {
//                is Some -> call.respond(HttpStatusCode.Created, thing.value)
//                is None -> call.respond(HttpStatusCode.InternalServerError)
//            }
//        }
    }

    personRepository.dropTable()
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
