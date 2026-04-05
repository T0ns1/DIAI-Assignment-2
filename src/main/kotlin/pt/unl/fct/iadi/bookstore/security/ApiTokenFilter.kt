package pt.unl.fct.iadi.bookstore.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiTokenFilter(
    private val apiTokenService: ApiTokenService,
    private val securityErrorHandlers: SecurityErrorHandlers,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-ui") ||
            path == "/swagger-ui.html"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = request.getHeader(API_TOKEN_HEADER)
        if (!apiTokenService.isValid(token)) {
            securityErrorHandlers.writeUnauthorized(response, "Missing or invalid X-Api-Token")
            return
        }

        filterChain.doFilter(request, response)
    }

    companion object {
        const val API_TOKEN_HEADER = "X-Api-Token"
    }
}
