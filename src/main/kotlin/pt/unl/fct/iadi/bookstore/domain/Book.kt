package pt.unl.fct.iadi.bookstore.domain

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL
import java.math.BigDecimal

data class Book(
    @field:NotBlank
    val isbn: String,

    @field:NotBlank
    @field:Size(min = 1, max = 120)
    val title: String,

    @field:NotBlank
    @field:Size(min = 1, max = 80)
    val author: String,

    @field:NotNull
    @field:DecimalMin(value = "0.0", inclusive = false)
    val price: BigDecimal,

    @field:NotBlank
    @field:URL
    val image: String,
)
