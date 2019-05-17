package de.schrader.ktor

interface Thing<T>
class Some<T>(val value: T) : Thing<T>
class None<T>(val info: String = "") : Thing<T>
