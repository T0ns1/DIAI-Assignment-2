package pt.unl.fct.iadi.bookstore.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.bookstore.controller.dto.ErrorResponse

@Component
class SecurityErrorHandlers(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint, AccessDeniedHandler {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        writeError(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication is required")
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        writeError(response, HttpStatus.FORBIDDEN, "FORBIDDEN", "Access is denied")
    }

    fun writeUnauthorized(response: HttpServletResponse, message: String) {
        writeError(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message)
    }

    private fun writeError(
        response: HttpServletResponse,
        status: HttpStatus,
        error: String,
        message: String,
    ) {
        if (response.isCommitted) {
            return
        }

        response.status = status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        objectMapper.writeValue(response.writer, ErrorResponse(error = error, message = message))
    }
}
