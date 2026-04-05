package pt.unl.fct.iadi.bookstore.service

import jakarta.validation.Validator
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.bookstore.controller.dto.CreateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.CreateReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReplaceBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReplaceReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateReviewRequest
import pt.unl.fct.iadi.bookstore.domain.Book
import pt.unl.fct.iadi.bookstore.domain.Review
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class BookstoreService(
    private val validator: Validator,
) {
    private val books = ConcurrentHashMap<String, Book>()
    private val reviewsByBook = ConcurrentHashMap<String, MutableMap<Long, Review>>()
    private val reviewIdSequence = AtomicLong(1)

    fun listBooks(): List<Book> = books.values.sortedBy { it.isbn }

    fun createBook(request: CreateBookRequest): Book {
        if (books.containsKey(request.isbn)) {
            throw BookAlreadyExistsException(request.isbn)
        }

        val book = Book(
            isbn = request.isbn,
            title = request.title,
            author = request.author,
            price = request.price,
            image = request.image,
        )
        validate(book)
        books[book.isbn] = book
        reviewsByBook.putIfAbsent(book.isbn, ConcurrentHashMap())
        return book
    }

    fun getBook(isbn: String): Book = books[isbn] ?: throw BookNotFoundException(isbn)

    fun replaceBook(isbn: String, request: ReplaceBookRequest): Pair<Book, Boolean> {
        val book = Book(
            isbn = isbn,
            title = request.title,
            author = request.author,
            price = request.price,
            image = request.image,
        )
        validate(book)
        val created = !books.containsKey(isbn)
        books[isbn] = book
        reviewsByBook.putIfAbsent(isbn, ConcurrentHashMap())
        return book to created
    }

    fun updateBook(isbn: String, request: UpdateBookRequest): Book {
        val current = getBook(isbn)
        val updated = current.copy(
            title = request.title ?: current.title,
            author = request.author ?: current.author,
            price = request.price ?: current.price,
            image = request.image ?: current.image,
        )
        validate(updated)
        books[isbn] = updated
        return updated
    }

    @PreAuthorize("hasRole('ADMIN')")
    fun deleteBook(isbn: String) {
        if (books.remove(isbn) == null) {
            throw BookNotFoundException(isbn)
        }
        reviewsByBook.remove(isbn)
    }

    fun listReviews(isbn: String): List<Review> {
        ensureBookExists(isbn)
        return reviewsByBook[isbn].orEmpty().values.sortedBy { it.id }
    }

    fun createReview(isbn: String, request: CreateReviewRequest): Review {
        ensureBookExists(isbn)
        val review = Review(
            id = reviewIdSequence.getAndIncrement(),
            rating = request.rating,
            comment = request.comment,
            author = currentUsername(),
        )
        validate(review)
        val reviewMap = reviewsByBook.computeIfAbsent(isbn) { ConcurrentHashMap() }
        reviewMap[review.id] = review
        return review
    }

    @PreAuthorize("@reviewAuthorizationService.isReviewAuthor(#isbn, #reviewId, authentication)")
    fun replaceReview(isbn: String, reviewId: Long, request: ReplaceReviewRequest): Review {
        ensureBookExists(isbn)
        val reviewMap = reviewsByBook[isbn] ?: throw ReviewNotFoundException(isbn, reviewId)
        val current = reviewMap[reviewId] ?: throw ReviewNotFoundException(isbn, reviewId)
        val updated = Review(
            id = reviewId,
            rating = request.rating,
            comment = request.comment,
            author = current.author,
        )
        validate(updated)
        reviewMap[reviewId] = updated
        return updated
    }

    @PreAuthorize("@reviewAuthorizationService.isReviewAuthor(#isbn, #reviewId, authentication)")
    fun updateReview(isbn: String, reviewId: Long, request: UpdateReviewRequest): Review {
        ensureBookExists(isbn)
        val reviewMap = reviewsByBook[isbn] ?: throw ReviewNotFoundException(isbn, reviewId)
        val current = reviewMap[reviewId] ?: throw ReviewNotFoundException(isbn, reviewId)
        val updated = current.copy(
            rating = request.rating ?: current.rating,
            comment = request.comment ?: current.comment,
        )
        validate(updated)
        reviewMap[reviewId] = updated
        return updated
    }

    @PreAuthorize("@reviewAuthorizationService.isReviewAuthorOrAdmin(#isbn, #reviewId, authentication)")
    fun deleteReview(isbn: String, reviewId: Long) {
        ensureBookExists(isbn)
        val reviewMap = reviewsByBook[isbn] ?: throw ReviewNotFoundException(isbn, reviewId)
        if (reviewMap.remove(reviewId) == null) {
            throw ReviewNotFoundException(isbn, reviewId)
        }
    }

    fun getReview(isbn: String, reviewId: Long): Review {
        ensureBookExists(isbn)
        return reviewsByBook[isbn]?.get(reviewId) ?: throw ReviewNotFoundException(isbn, reviewId)
    }

    private fun ensureBookExists(isbn: String) {
        if (!books.containsKey(isbn)) {
            throw BookNotFoundException(isbn)
        }
    }

    private fun currentUsername(): String =
        SecurityContextHolder.getContext().authentication?.name
            ?.takeUnless { it.isBlank() }
            ?: "anonymous"

    private fun validate(any: Any) {
        val violations = validator.validate(any)
        if (violations.isNotEmpty()) {
            val message = violations
                .sortedBy { it.propertyPath.toString() }
                .joinToString("; ") { "${it.propertyPath}: ${it.message}" }
            throw RequestValidationException(message)
        }
    }
}
