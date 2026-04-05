package pt.unl.fct.iadi.bookstore.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Schema(description = "Review resource returned by the API")
data class ReviewResponse(
    @field:Schema(description = "Generated review identifier", example = "1")
    val id: Long,

    @field:Schema(description = "Rating from 1 to 5", example = "5")
    val rating: Int,

    @field:Schema(description = "Optional review comment", example = "Excellent reference book.")
    val comment: String?,

    @field:Schema(description = "Username of the review author", example = "editor1")
    val author: String,
)

@Schema(description = "Payload to create a review")
data class CreateReviewRequest(
    @field:NotNull
    @field:Min(1)
    @field:Max(5)
    @field:Schema(description = "Rating from 1 to 5", example = "5", minimum = "1", maximum = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    val rating: Int,

    @field:Size(max = 500)
    @field:Schema(description = "Optional review comment", example = "Excellent reference book.", maxLength = 500)
    val comment: String? = null,
)

@Schema(description = "Payload to fully replace a review")
data class ReplaceReviewRequest(
    @field:NotNull
    @field:Min(1)
    @field:Max(5)
    @field:Schema(description = "Rating from 1 to 5", example = "4", minimum = "1", maximum = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    val rating: Int,

    @field:Size(max = 500)
    @field:Schema(description = "Optional review comment", example = "Still very good.", maxLength = 500)
    val comment: String? = null,
)

@Schema(description = "Payload to partially update a review")
data class UpdateReviewRequest(
    @field:Min(1)
    @field:Max(5)
    @field:Schema(description = "Rating from 1 to 5", example = "3", minimum = "1", maximum = "5")
    val rating: Int? = null,

    @field:Size(max = 500)
    @field:Schema(description = "Optional review comment", example = "I changed my mind.", maxLength = 500)
    val comment: String? = null,
)
