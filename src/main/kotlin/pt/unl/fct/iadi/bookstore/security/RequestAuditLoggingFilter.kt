package pt.unl.fct.iadi.bookstore.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RequestAuditLoggingFilter(
    private val apiTokenService: ApiTokenService,
) : OncePerRequestFilter() {
    private val auditLogger = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            filterChain.doFilter(request, response)
        } finally {
            val appName = apiTokenService.resolveAppName(request.getHeader(ApiTokenFilter.API_TOKEN_HEADER))
            val principal = SecurityContextHolder.getContext().authentication?.name
                ?.takeUnless { it == "anonymousUser" || it.isBlank() }
                ?: "anonymous"
            auditLogger.info("[{}] [{}] {} {} [{}]", appName, principal, request.method, request.requestURI, response.status)
        }
    }
}
