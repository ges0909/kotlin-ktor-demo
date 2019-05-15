package de.schrader.ktor.service

import de.schrader.ktor.route.Person

interface PersonService {
    fun get(): List<Person>
    fun getById(id: Int): Person
    fun create(person: Person): Person
    fun update(id: Int, person: Person)
    fun delete(id: Int)
}
