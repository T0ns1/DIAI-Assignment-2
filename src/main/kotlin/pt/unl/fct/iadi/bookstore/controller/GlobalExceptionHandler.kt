package pt.unl.fct.iadi.bookstore.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import pt.unl.fct.iadi.bookstore.controller.dto.ErrorResponse
import pt.unl.fct.iadi.bookstore.service.BookAlreadyExistsException
import pt.unl.fct.iadi.bookstore.service.BookNotFoundException
import pt.unl.fct.iadi.bookstore.service.RequestValidationException
import pt.unl.fct.iadi.bookstore.service.ReviewNotFoundException
import java.util.Locale

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFound(ex: BookNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val language = resolveLanguage(request)
        val message = when (language) {
            "pt" -> "Livro com ISBN ${ex.isbn} não encontrado"
            else -> "Book with ISBN ${ex.isbn} not found"
        }
        return build(HttpStatus.NOT_FOUND, ErrorResponse("NOT_FOUND", message), language)
    }

    @ExceptionHandler(ReviewNotFoundException::class)
    fun handleReviewNotFound(ex: ReviewNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val language = resolveLanguage(request)
        val message = when (language) {
            "pt" -> "Avaliação ${ex.reviewId} do livro ${ex.isbn} não encontrada"
            else -> "Review ${ex.reviewId} for book ${ex.isbn} not found"
        }
        return build(HttpStatus.NOT_FOUND, ErrorResponse("NOT_FOUND", message), language)
    }

    @ExceptionHandler(BookAlreadyExistsException::class)
    fun handleConflict(ex: BookAlreadyExistsException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val language = resolveLanguage(request)
        val message = when (language) {
            "pt" -> "Já existe um livro com ISBN ${ex.isbn}"
            else -> "Book with ISBN ${ex.isbn} already exists"
        }
        return build(HttpStatus.CONFLICT, ErrorResponse("CONFLICT", message), language)
    }

    @ExceptionHandler(RequestValidationException::class, MethodArgumentNotValidException::class, ConstraintViolationException::class)
    fun handleValidation(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val language = resolveLanguage(request)
        val message = when (ex) {
            is RequestValidationException -> ex.message ?: defaultValidationMessage(language)
            is MethodArgumentNotValidException -> ex.bindingResult.fieldErrors
                .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
                .ifBlank { defaultValidationMessage(language) }
            is ConstraintViolationException -> ex.constraintViolations
                .joinToString("; ") { "${it.propertyPath}: ${it.message}" }
                .ifBlank { defaultValidationMessage(language) }
            else -> defaultValidationMessage(language)
        }
        return build(HttpStatus.BAD_REQUEST, ErrorResponse("VALIDATION_ERROR", message), language)
    }

    private fun defaultValidationMessage(language: String): String =
        if (language == "pt") "Pedido inválido" else "Invalid request"

    private fun build(status: HttpStatus, body: ErrorResponse, language: String): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status)
            .header(HttpHeaders.CONTENT_LANGUAGE, language)
            .body(body)

    private fun resolveLanguage(request: HttpServletRequest): String {
        val locale = request.locale ?: LocaleContextHolder.getLocale()
        return if (locale.language.equals("pt", ignoreCase = true)) "pt" else "en"
    }
}
