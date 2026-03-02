package com.nozero.shared.core

/**
 * A sealed wrapper for operation results.
 * Used across all layers to avoid throwing exceptions for expected failures.
 */
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val error: AppError) : AppResult<Nothing>()
}

sealed class AppError(val message: String) {
    class NotFound(message: String = "Resource not found") : AppError(message)
    class ValidationError(message: String) : AppError(message)
    class DatabaseError(message: String) : AppError(message)
    class Unknown(message: String = "An unknown error occurred") : AppError(message)
}
