package de.schrader.ktor.repository

// import com.fasterxml.jackson.annotation.JsonInclude

// @JsonInclude(JsonInclude.Include.NON_NULL)
data class Person(val id: Int?, val name: String, var age: Int)
