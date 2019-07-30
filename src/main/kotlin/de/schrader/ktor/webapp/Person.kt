package de.schrader.ktor.webapp

import de.schrader.ktor.PERSONS_ENDPOINT
import de.schrader.ktor.api.model.Person
import de.schrader.ktor.api.service.PersonService
import de.schrader.ktor.auth.Session
import de.schrader.ktor.auth.UserRepository
import de.schrader.ktor.redirect
import de.schrader.ktor.securityCode
import de.schrader.ktor.verifyCode
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.koin.ktor.ext.inject

fun Route.person(hashFunction: (String) -> String) {
    val userRepository: UserRepository by inject()
    val personService: PersonService by inject()

    route(PERSONS_ENDPOINT) {
        get {
            // val user = call.authentication.principal as User
            val user = call.sessions.get<Session>()?.let { userRepository.findById(it.userId) }
                ?: return@get call.redirect(Signin())

            val persons = personService.findAll()
            val date = System.currentTimeMillis()
            val code = call.securityCode(date, user, hashFunction)

            call.respond(
                FreeMarkerContent(
                    "person.ftl", mapOf("persons" to persons, "user" to user, "date" to date, "code" to code),
                    user.userId
                )
            )
        }

        post {
            val user = call.sessions.get<Session>()?.let { userRepository.findById(it.userId) }
                ?: return@post call.redirect(Signin())

            val params = call.receiveParameters()
            val date = params["date"]?.toLongOrNull() ?: return@post call.redirect(it)
            val code = params["code"] ?: return@post call.redirect(it)
            val action = params["action"] ?: throw IllegalArgumentException("Missing parameter `action`")

            if (!call.verifyCode(date, user, code, hashFunction)) return@post call.redirect(Signin())

            when (action) {
                "add" -> {
                    val name = params["name"] ?: throw IllegalArgumentException("Missing parameter `name`")
                    val age = params["age"] ?: throw IllegalArgumentException("Missing parameter `age`")
                    personService.create(Person(userId = user.userId, name = name, age = age.toInt()))
                }
                "delete" -> {
                    val id = params["id"] ?: throw IllegalArgumentException("Missing parameter `id`")
                    personService.delete(id.toInt())
                }
            }
            call.respondRedirect(PERSONS_ENDPOINT)
        }
    }
}
