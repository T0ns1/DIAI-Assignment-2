package pt.unl.fct.iadi.bookstore.service

sealed class BookstoreException(message: String) : RuntimeException(message)

class BookAlreadyExistsException(val isbn: String) : BookstoreException("Book with ISBN $isbn already exists")
class BookNotFoundException(val isbn: String) : BookstoreException("Book with ISBN $isbn not found")
class ReviewNotFoundException(val isbn: String, val reviewId: Long) : BookstoreException("Review $reviewId for book $isbn not found")
class RequestValidationException(message: String) : BookstoreException(message)
