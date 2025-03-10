package eu.virtusdevelops.magicbees.api.models

sealed class Result<out T, out E> {
    data class Success<T>(val value: T) : Result<T, Nothing>()
    data class Failure<E>(val errors: E) : Result<Nothing, E>()
    data class Other<T>(val value: T) : Result<T, Nothing>()

    companion object {
        // Factory method for success creation
        fun <T> success(value: T): Result<T, Nothing> = Success(value)

        // Factory method for failure creation
        fun <E> failure(errors: E): Result<Nothing, E> = Failure(errors)

        // for middle
        fun <T> other(value: T): Result<T, Nothing> = Other(value)
    }
}