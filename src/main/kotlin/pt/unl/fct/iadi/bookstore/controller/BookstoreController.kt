package pt.unl.fct.iadi.bookstore.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pt.unl.fct.iadi.bookstore.controller.dto.BookResponse
import pt.unl.fct.iadi.bookstore.controller.dto.CreateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.CreateReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReplaceBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReplaceReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewResponse
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateReviewRequest
import pt.unl.fct.iadi.bookstore.domain.Book
import pt.unl.fct.iadi.bookstore.domain.Review
import pt.unl.fct.iadi.bookstore.service.BookstoreService

@RestController
class BookstoreController(
    private val bookstoreService: BookstoreService,
) : BookstoreAPI {

    override fun listBooks(): ResponseEntity<List<BookResponse>> =
        ResponseEntity.ok(bookstoreService.listBooks().map { it.toResponse() })

    override fun createBook(request: CreateBookRequest): ResponseEntity<Void> {
        val created = bookstoreService.createBook(request)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{isbn}")
            .buildAndExpand(created.isbn)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    override fun getBook(
        isbn: String,
        acceptLanguage: String?,
    ): ResponseEntity<BookResponse> =
        ResponseEntity.ok(bookstoreService.getBook(isbn).toResponse())

    override fun replaceBook(
        isbn: String,
        request: ReplaceBookRequest,
    ): ResponseEntity<BookResponse> {
        if (isbn != request.isbn) {
            throw IllegalArgumentException("ISBN in path and body must match")
        }

        val (book, created) = bookstoreService.replaceBook(isbn, request)
        val response = book.toResponse()

        return if (created) {
            val location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri()
            ResponseEntity.created(location).body(response)
        } else {
            ResponseEntity.ok(response)
        }
    }

    override fun updateBook(
        isbn: String,
        request: UpdateBookRequest,
    ): ResponseEntity<BookResponse> =
        ResponseEntity.ok(bookstoreService.updateBook(isbn, request).toResponse())

    override fun deleteBook(isbn: String): ResponseEntity<Void> {
        bookstoreService.deleteBook(isbn)
        return ResponseEntity.noContent().build()
    }

    override fun listReviews(isbn: String): ResponseEntity<List<ReviewResponse>> =
        ResponseEntity.ok(bookstoreService.listReviews(isbn).map { it.toResponse() })

    override fun createReview(
        isbn: String,
        request: CreateReviewRequest,
    ): ResponseEntity<ReviewResponse> {
        val created = bookstoreService.createReview(isbn, request)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{reviewId}")
            .buildAndExpand(created.id)
            .toUri()

        return ResponseEntity.created(location).body(created.toResponse())
    }

    override fun replaceReview(
        isbn: String,
        reviewId: Long,
        request: ReplaceReviewRequest,
    ): ResponseEntity<ReviewResponse> =
        ResponseEntity.ok(
            bookstoreService.replaceReview(isbn, reviewId, request).toResponse(),
        )

    override fun updateReview(
        isbn: String,
        reviewId: Long,
        request: UpdateReviewRequest,
    ): ResponseEntity<ReviewResponse> =
        ResponseEntity.ok(
            bookstoreService.updateReview(isbn, reviewId, request).toResponse(),
        )

    override fun deleteReview(
        isbn: String,
        reviewId: Long,
    ): ResponseEntity<Void> {
        bookstoreService.deleteReview(isbn, reviewId)
        return ResponseEntity.noContent().build()
    }

    private fun Book.toResponse() = BookResponse(
        isbn = isbn,
        title = title,
        author = author,
        price = price,
        image = image,
    )

    private fun Review.toResponse() = ReviewResponse(
        id = id,
        rating = rating,
        comment = comment,
        author = author,
    )
}
