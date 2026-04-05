package pt.unl.fct.iadi.bookstore.security

import org.springframework.stereotype.Component

@Component
class ApiTokenService {
    private val appTokens = mapOf(
        "catalog-app" to "token-catalog-abc123",
        "mobile-app" to "token-mobile-def456",
        "web-app" to "token-web-ghi789",
    )

    private val tokensToApps = appTokens.entries.associate { (app, token) -> token to app }

    fun isValid(token: String?): Boolean = token != null && tokensToApps.containsKey(token)

    fun resolveAppName(token: String?): String = tokensToApps[token] ?: "unknown"
}
