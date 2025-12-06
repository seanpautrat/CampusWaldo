package com.example.campuswaldo.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Single hint object in the "hints" array from GET /waldo/today/
 *
 * Example JSON:
 * "hints": [{ "id": 1, "text": "I am either...", "image_url": null }]
 */
@Serializable
data class WaldoHintDto(
    val id: Int,
    val text: String,
    @SerialName("image_url") val imageUrl: String? = null
)

/**
 * Full response for GET /waldo/today/
 *
 * Example JSON:
 * {
 *   "waldo": "charlie",
 *   "image": "https://example.com/charlie.jpg",
 *   "date": "2025-12-05",
 *   "hints": [ { ... }, ... ]
 * }
 */
@Serializable
data class WaldoTodayResponse(
    val waldo: String,
    val image: String,
    val date: String,
    val hints: List<WaldoHintDto> = emptyList()
)

/**
 * Response for GET /waldo/code/
 */
@Serializable
data class SecretCodeResponse(
    val secret_code: String
)

/**
 * Body for POST /waldo/redeem/ (or /waldo/found/)
 * Backend expects: { "user_id": ..., "secret_code": "..." }
 */
@Serializable
data class RedeemRequest(
    @SerialName("user_id") val userId: Int,
    @SerialName("secret_code") val secretCode: String
)

/**
 * Raw backend response for redeem route.
 * Example JSON: { "points_awarded": 18, "total_points": 42 }
 */
@Serializable
data class RedeemApiResponse(
    val points_awarded: Double,
    val total_points: Int
)

/**
 * Clean UI model used in HuntUiState and the composables.
 */
data class WaldoOfDay(
    val alias: String,
    val imageUrl: String,
    val hints: List<String>
)
/**
 * Response for GET /leaderboard/ (adjust field names if needed once you see the JSON)
 */
@Serializable
data class LeaderboardUserDto(
    val rank: Int,
    @SerialName("user_id") val userId: Int,
    val username: String,
    val points: Int
)

@Serializable
data class LeaderboardResponse(
    val leaderboard: List<LeaderboardUserDto>
)

@Serializable
data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val points: Int
)

/**
 * UI model for redeem result shown in the Hunt screen.
 */
data class RedeemResult(
    val correct: Boolean,
    val pointsEarned: Int,
    val message: String
)

@Serializable
data class User(
    val username: String,
    val isTodayWaldo: Boolean = false
)