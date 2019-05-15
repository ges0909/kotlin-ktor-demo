package de.schrader.ktor.service

import de.schrader.ktor.controller.Person
import de.schrader.ktor.repository.PersonRepository

interface PersonService {
    fun get(): List<Person>
    fun getById(id: Int): Person
    fun create(person: Person): Person
    fun update(id: Int, person: Person)
    fun delete(id: Int)
}

class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {

    override fun get(): List<Person> = personRepository.get()

    override fun getById(id: Int): Person = personRepository.getById(id)

    override fun create(person: Person): Person {
        val id = personRepository.create(person)
        return getById(id)
    }

    override fun update(id: Int, person: Person) = personRepository.update(id, person)

    override fun delete(id: Int) = personRepository.delete(id)
}
