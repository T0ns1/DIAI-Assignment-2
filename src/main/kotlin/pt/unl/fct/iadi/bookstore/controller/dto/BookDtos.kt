package pt.unl.fct.iadi.bookstore.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL
import java.math.BigDecimal

@Schema(description = "Book resource returned by the API")
data class BookResponse(
    @field:Schema(
        description = "Unique ISBN identifier of the book",
        example = "9780134685991",
    )
    val isbn: String,

    @field:Schema(
        description = "Book title",
        minLength = 1,
        maxLength = 120,
        example = "Effective Java",
    )
    val title: String,

    @field:Schema(
        description = "Book author",
        minLength = 1,
        maxLength = 80,
        example = "Joshua Bloch",
    )
    val author: String,

    @field:Schema(
        description = "Book price, must be greater than zero",
        example = "49.99",
        minimum = "0.01",
    )
    val price: BigDecimal,

    @field:Schema(
        description = "Remote URL of the book cover image",
        example = "https://example.com/covers/effective-java.jpg",
    )
    val image: String,
)

@Schema(description = "Payload to create a new book")
data class CreateBookRequest(
    @field:NotBlank
    @field:Schema(
        description = "Unique ISBN identifier of the book",
        example = "9780134685991",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val isbn: String,

    @field:NotBlank
    @field:Size(min = 1, max = 120)
    @field:Schema(
        description = "Book title",
        example = "Effective Java",
        minLength = 1,
        maxLength = 120,
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val title: String,

    @field:NotBlank
    @field:Size(min = 1, max = 80)
    @field:Schema(
        description = "Book author",
        example = "Joshua Bloch",
        minLength = 1,
        maxLength = 80,
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val author: String,

    @field:NotNull
    @field:DecimalMin(value = "0.01")
    @field:Schema(
        description = "Book price, must be greater than zero",
        example = "49.99",
        minimum = "0.01",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val price: BigDecimal,

    @field:NotBlank
    @field:URL
    @field:Schema(
        description = "Remote URL of the book cover image",
        example = "https://example.com/covers/effective-java.jpg",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val image: String,
)

@Schema(description = "Payload to fully replace a book")
data class ReplaceBookRequest(
    @field:NotBlank
    @field:Schema(
        description = "Unique ISBN identifier of the book",
        example = "9780134685991",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val isbn: String,

    @field:NotBlank
    @field:Size(min = 1, max = 120)
    @field:Schema(
        description = "Book title",
        example = "Effective Java, 3rd Edition",
        minLength = 1,
        maxLength = 120,
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val title: String,

    @field:NotBlank
    @field:Size(min = 1, max = 80)
    @field:Schema(
        description = "Book author",
        example = "Joshua Bloch",
        minLength = 1,
        maxLength = 80,
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val author: String,

    @field:NotNull
    @field:DecimalMin(value = "0.01")
    @field:Schema(
        description = "Book price, must be greater than zero",
        example = "54.99",
        minimum = "0.01",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val price: BigDecimal,

    @field:NotBlank
    @field:URL
    @field:Schema(
        description = "Remote URL of the book cover image",
        example = "https://example.com/covers/effective-java-3e.jpg",
        requiredMode = Schema.RequiredMode.REQUIRED,
    )
    val image: String,
)

@Schema(description = "Payload to partially update a book")
data class UpdateBookRequest(
    @field:Size(min = 1, max = 120)
    @field:Schema(
        description = "Book title",
        example = "Effective Java",
    )
    val title: String? = null,

    @field:Size(min = 1, max = 80)
    @field:Schema(
        description = "Book author",
        example = "Joshua Bloch",
    )
    val author: String? = null,

    @field:DecimalMin(value = "0.01")
    @field:Schema(
        description = "Book price, must be greater than zero",
        example = "44.99",
        minimum = "0.01",
    )
    val price: BigDecimal? = null,

    @field:URL
    @field:Schema(
        description = "Remote URL of the book cover image",
        example = "https://example.com/covers/effective-java.jpg",
    )
    val image: String? = null,
)