package pt.unl.fct.iadi.bookstore.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody as SpringRequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import pt.unl.fct.iadi.bookstore.controller.dto.BookResponse
import pt.unl.fct.iadi.bookstore.controller.dto.CreateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.CreateReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ErrorResponse
import pt.unl.fct.iadi.bookstore.controller.dto.ReplaceBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReplaceReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewResponse
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.UpdateReviewRequest

@Tag(
    name = "Bookstore",
    description = "Book and review management API",
)
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
interface BookstoreAPI {

    @Operation(
        summary = "List books",
        description = "Returns the full book catalog. Returns an empty array when no books exist.",
    )
    @ApiResponse(
        responseCode = "200",
        description = "Books returned",
        content = [
            Content(
                array = ArraySchema(
                    schema = Schema(implementation = BookResponse::class),
                ),
            ),
        ],
    )
    @GetMapping("/books")
    fun listBooks(): ResponseEntity<List<BookResponse>>

    @Operation(
        summary = "Create a book",
        description = "Registers a new book in the catalog.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Book created",
                content = [
                    Content(
                        schema = Schema(implementation = BookResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "409",
                description = "Book already exists",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    @PostMapping(
        "/books",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun createBook(
        @RequestBody(
            description = "Book to create",
            required = true,
            content = [
                Content(
                    schema = Schema(implementation = CreateBookRequest::class),
                ),
            ],
        )
        @Valid
        @SpringRequestBody
        request: CreateBookRequest,
    ): ResponseEntity<BookResponse>

    @Operation(
        summary = "Get a single book",
        description = "Returns a single book by ISBN. Error messages honor Accept-Language for pt and en.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Book returned",
                content = [
                    Content(
                        schema = Schema(implementation = BookResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping("/books/{isbn}")
    fun getBook(
        @Parameter(
            description = "ISBN of the book",
            example = "9780134685991",
        )
        @PathVariable
        isbn: String,
        @RequestHeader(
            name = "Accept-Language",
            required = false,
        )
        acceptLanguage: String?,
    ): ResponseEntity<BookResponse>

    @Operation(
        summary = "Replace a book",
        description = "Fully replaces a book. Creates it if it does not already exist.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Book replaced",
                content = [
                    Content(
                        schema = Schema(implementation = BookResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "201",
                description = "Book created through upsert",
                content = [
                    Content(
                        schema = Schema(implementation = BookResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    @PutMapping(
        "/books/{isbn}",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun replaceBook(
        @Parameter(
            description = "ISBN of the book",
            example = "9780134685991",
        )
        @PathVariable
        isbn: String,
        @RequestBody(
            description = "Full replacement representation",
            required = true,
            content = [
                Content(
                    schema = Schema(implementation = ReplaceBookRequest::class),
                ),
            ],
        )
        @Valid
        @SpringRequestBody
        request: ReplaceBookRequest,
    ): ResponseEntity<BookResponse>

    @Operation(
        summary = "Partially update a book",
        description = "Updates only the provided book fields.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Book updated",
                content = [
                    Content(
                        schema = Schema(implementation = BookResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    @PatchMapping(
        "/books/{isbn}",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun updateBook(
        @Parameter(
            description = "ISBN of the book",
            example = "9780134685991",
        )
        @PathVariable
        isbn: String,
        @RequestBody(
            description = "Partial update representation",
            required = true,
            content = [
                Content(
                    schema = Schema(implementation = UpdateBookRequest::class),
                ),
            ],
        )
        @Valid
        @SpringRequestBody
        request: UpdateBookRequest,
    ): ResponseEntity<BookResponse>

    @Operation(
        summary = "Delete a book",
        description = "Deletes a book and all reviews belonging to it.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Book deleted",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    @DeleteMapping("/books/{isbn}")
    fun deleteBook(
        @Parameter(
            description = "ISBN of the book",
            example = "9780134685991",
        )
        @PathVariable
        isbn: String,
    ): ResponseEntity<Void>

    @Operation(
        summary = "List reviews for a book",
        description = "Returns all reviews for the given book.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Reviews returned",
                content = [
                    Content(
                        array = ArraySchema(
                            schema = Schema(implementation = ReviewResponse::class),
                        ),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping("/books/{isbn}/reviews")
    fun listReviews(
        @Parameter(
            description = "ISBN of the book",
            example = "9780134685991",
        )
        @PathVariable
        isbn: String,
    ): ResponseEntity<List<ReviewResponse>>

    @Operation(
        summary = "Create a review",
        description = "Creates a review for the given book.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Review created",
                content = [
                    Content(
                        schema = Schema(implementation = ReviewResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book not found",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    @PostMapping(
        "/books/{isbn}/reviews",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun createReview(
        @Parameter(
            description = "ISBN of the book",
            example = "9780134685991",
        )
        @PathVariable
        isbn: String,
        @RequestBody(
            description = "Review to create",
            required = true,
            content = [
                Content(
                    schema = Schema(implementation = CreateReviewRequest::class),
                ),
            ],
        )
        @Valid
        @SpringRequestBody
        request: CreateReviewRequest,
    ): ResponseEntity<ReviewResponse>

    @Operation(
        summary = "Replace a review",
        description = "Fully replaces an existing review.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Review replaced",
                content = [
                    Content(
                        schema = Schema(implementation = ReviewResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book or review not found",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    @PutMapping(
        "/books/{isbn}/reviews/{reviewId}",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun replaceReview(
        @Parameter(
            description = "ISBN of the book",
            example = "9780134685991",
        )
        @PathVariable
        isbn: String,
        @Parameter(
            description = "Identifier of the review",
            example = "1",
        )
        @PathVariable
        reviewId: Long,
        @RequestBody(
            description = "Full replacement representation",
            required = true,
            content = [
                Content(
                    schema = Schema(implementation = ReplaceReviewRequest::class),
                ),
            ],
        )
        @Valid
        @SpringRequestBody
        request: ReplaceReviewRequest,
    ): ResponseEntity<ReviewResponse>

    @Operation(
        summary = "Partially update a review",
        description = "Updates only the provided review fields.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Review updated",
                content = [
                    Content(
                        schema = Schema(implementation = ReviewResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book or review not found",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    @PatchMapping(
        "/books/{isbn}/reviews/{reviewId}",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun updateReview(
        @Parameter(
            description = "ISBN of the book",
            example = "9780134685991",
        )
        @PathVariable
        isbn: String,
        @Parameter(
            description = "Identifier of the review",
            example = "1",
        )
        @PathVariable
        reviewId: Long,
        @RequestBody(
            description = "Partial update representation",
            required = true,
            content = [
                Content(
                    schema = Schema(implementation = UpdateReviewRequest::class),
                ),
            ],
        )
        @Valid
        @SpringRequestBody
        request: UpdateReviewRequest,
    ): ResponseEntity<ReviewResponse>

    @Operation(
        summary = "Delete a review",
        description = "Deletes an existing review from a book.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Review deleted",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Book or review not found",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    @DeleteMapping("/books/{isbn}/reviews/{reviewId}")
    fun deleteReview(
        @Parameter(
            description = "ISBN of the book",
            example = "9780134685991",
        )
        @PathVariable
        isbn: String,
        @Parameter(
            description = "Identifier of the review",
            example = "1",
        )
        @PathVariable
        reviewId: Long,
    ): ResponseEntity<Void>
}