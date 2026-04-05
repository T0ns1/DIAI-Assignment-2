package pt.unl.fct.iadi.bookstore.security

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.bookstore.service.BookNotFoundException
import pt.unl.fct.iadi.bookstore.service.BookstoreService
import pt.unl.fct.iadi.bookstore.service.ReviewNotFoundException

@Component("reviewAuthorizationService")
class ReviewAuthorizationService(
    private val bookstoreService: BookstoreService,
) {
    fun isReviewAuthor(isbn: String, reviewId: Long, authentication: Authentication): Boolean =
        try {
            bookstoreService.getReview(isbn, reviewId).author == authentication.name
        } catch (_: ReviewNotFoundException) {
            false
        } catch (_: BookNotFoundException) {
            false
        }

    fun isReviewAuthorOrAdmin(isbn: String, reviewId: Long, authentication: Authentication): Boolean =
        authentication.authorities.any { it.authority == "ROLE_ADMIN" } ||
            isReviewAuthor(isbn, reviewId, authentication)
}
