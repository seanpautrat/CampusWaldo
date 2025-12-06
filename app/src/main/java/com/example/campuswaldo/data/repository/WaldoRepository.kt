package com.example.campuswaldo.data.repository

import com.example.campuswaldo.data.model.LeaderboardEntry
import com.example.campuswaldo.data.model.RedeemRequest
import com.example.campuswaldo.data.model.RedeemResult
import com.example.campuswaldo.data.model.WaldoOfDay
import com.example.campuswaldo.data.remote.WaldoApiService
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WaldoRepository @Inject constructor(
    private val apiService: WaldoApiService
) {

    suspend fun getTodayWaldo(): Result<WaldoOfDay> {
        return runCatching {
            val dto = apiService.getTodayWaldo()
            WaldoOfDay(
                alias = dto.waldo,
                imageUrl = dto.image,
                hints = dto.hints.map { it.text }
            )
        }
    }

    suspend fun redeemCode(
        userId: Int,
        code: String
    ): Result<RedeemResult> {
        return runCatching {
            try {

                val response = apiService.redeemWaldo(
                    RedeemRequest(
                        userId = userId,
                        secretCode = code
                    )
                )

                RedeemResult(
                    correct = response.points_awarded > 0.0,
                    pointsEarned = response.points_awarded.toInt(),
                    message = if (response.points_awarded > 0.0) {
                        "You found today's Waldo! Total points: ${response.total_points}"
                    } else {
                        "No points awarded. Keep hunting!"
                    }
                )
            } catch (e: IOException) {
                RedeemResult(
                    correct = true,
                    pointsEarned = 0,
                    message = "You found today's Waldo!"
                )
            }
        }
    }

    suspend fun getSecretCode(): Result<String> {
        return runCatching {
            apiService.getSecretCode().secret_code
        }
    }

    suspend fun getLeaderboard(): Result<List<LeaderboardEntry>> {
        return runCatching {
            val response = apiService.getLeaderboard()

            response.leaderboard.map { dto ->
                LeaderboardEntry(
                    rank = dto.rank,
                    username = dto.username,
                    points = dto.points
                )
            }
        }
    }
}


