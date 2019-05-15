package de.schrader.ktor.service

sealed class Result
class Success(val value: Any) : Result()
class Failure(val message: String) : Result()
