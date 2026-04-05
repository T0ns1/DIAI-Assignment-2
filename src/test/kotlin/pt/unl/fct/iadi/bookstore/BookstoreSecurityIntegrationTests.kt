package pt.unl.fct.iadi.bookstore

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class BookstoreSecurityIntegrationTests(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) {
    @Test
    fun `api docs remain public`() {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk)
    }

    @Test
    fun `api token is required for reads`() {
        mockMvc.perform(get("/books"))
            .andExpect(status().isUnauthorized)
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
            .andExpect(jsonPath("$.message").value("Missing or invalid X-Api-Token"))
    }

    @Test
    fun `write operations require basic authentication`() {
        mockMvc.perform(
            post("/books")
                .header(API_TOKEN_HEADER, VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson(uniqueIsbn())),
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
            .andExpect(jsonPath("$.message").value("Authentication is required"))
    }

    @Test
    fun `only admin can delete a book`() {
        val isbn = uniqueIsbn()
        createBook(isbn, "editor1", "editor1pass")

        mockMvc.perform(
            delete("/books/$isbn")
                .header(API_TOKEN_HEADER, VALID_TOKEN)
                .with(httpBasic("editor1", "editor1pass")),
        )
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.error").value("FORBIDDEN"))
            .andExpect(jsonPath("$.message").value("Access is denied"))
    }

    @Test
    fun `only review author can update a review`() {
        val isbn = uniqueIsbn()
        createBook(isbn, "editor1", "editor1pass")
        val reviewId = createReview(isbn, "editor1", "editor1pass")

        mockMvc.perform(
            patch("/books/$isbn/reviews/$reviewId")
                .header(API_TOKEN_HEADER, VALID_TOKEN)
                .with(httpBasic("editor2", "editor2pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"comment":"Unauthorized edit"}"""),
        )
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.error").value("FORBIDDEN"))

        mockMvc.perform(
            patch("/books/$isbn/reviews/$reviewId")
                .header(API_TOKEN_HEADER, VALID_TOKEN)
                .with(httpBasic("editor1", "editor1pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"comment":"Authorized edit"}"""),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.author").value("editor1"))
            .andExpect(jsonPath("$.comment").value("Authorized edit"))
    }

    @Test
    fun `review responses expose author and admin can delete any review`() {
        val isbn = uniqueIsbn()
        createBook(isbn, "editor1", "editor1pass")
        val reviewId = createReview(isbn, "editor1", "editor1pass")

        mockMvc.perform(
            delete("/books/$isbn/reviews/$reviewId")
                .header(API_TOKEN_HEADER, VALID_TOKEN)
                .with(httpBasic("editor2", "editor2pass")),
        )
            .andExpect(status().isForbidden)

        mockMvc.perform(
            delete("/books/$isbn/reviews/$reviewId")
                .header(API_TOKEN_HEADER, VALID_TOKEN)
                .with(httpBasic("admin", "adminpass")),
        )
            .andExpect(status().isNoContent)
    }

    private fun createBook(isbn: String, username: String, password: String) {
        mockMvc.perform(
            post("/books")
                .header(API_TOKEN_HEADER, VALID_TOKEN)
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson(isbn)),
        )
            .andExpect(status().isCreated)
    }

    private fun createReview(isbn: String, username: String, password: String): Long {
        val response = mockMvc.perform(
            post("/books/$isbn/reviews")
                .header(API_TOKEN_HEADER, VALID_TOKEN)
                .with(httpBasic(username, password))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"rating":5,"comment":"Excellent reference book."}"""),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.author", equalTo(username)))
            .andReturn()
            .response
            .contentAsString

        return objectMapper.readTree(response).get("id").asLong()
    }

    private fun bookJson(isbn: String): String =
        """
        {
          "isbn": "$isbn",
          "title": "Effective Java",
          "author": "Joshua Bloch",
          "price": 49.99,
          "image": "https://example.com/covers/effective-java.jpg"
        }
        """.trimIndent()

    private fun uniqueIsbn(): String = UUID.randomUUID().toString().replace("-", "").take(13)

    companion object {
        private const val API_TOKEN_HEADER = "X-Api-Token"
        private const val VALID_TOKEN = "token-catalog-abc123"
    }
}
