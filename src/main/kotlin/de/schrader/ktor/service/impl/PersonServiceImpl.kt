package de.schrader.ktor.service.impl

import de.schrader.ktor.repository.PersonRepository
import de.schrader.ktor.route.Person
import de.schrader.ktor.service.PersonService

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
