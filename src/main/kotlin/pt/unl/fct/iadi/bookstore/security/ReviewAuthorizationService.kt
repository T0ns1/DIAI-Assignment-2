package pt.unl.fct.iadi.bookstore.security

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.bookstore.service.BookstoreService

@Component("reviewAuthorizationService")
class ReviewAuthorizationService(
    private val bookstoreService: BookstoreService,
) {
    fun isReviewAuthor(isbn: String, reviewId: Long, authentication: Authentication): Boolean =
        bookstoreService.getReview(isbn, reviewId).author == authentication.name

    fun isReviewAuthorOrAdmin(isbn: String, reviewId: Long, authentication: Authentication): Boolean =
        isReviewAuthor(isbn, reviewId, authentication) ||
            authentication.authorities.any { it.authority == "ROLE_ADMIN" }
}
