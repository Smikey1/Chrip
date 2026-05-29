package com.twugteam.admin.chat.presentation.util

import com.twugteam.admin.core.presentation.util.UiText
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.today
import com.twugteam.admin.feature.chat.presentation.today_x
import com.twugteam.admin.feature.chat.presentation.yesterday
import com.twugteam.admin.feature.chat.presentation.yesterday_x
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

object DateUtils {
    fun formatMessageTime(instant: Instant, clock: Clock = Clock.System): UiText {
        val timeZone = TimeZone.currentSystemDefault()
        val messageDateTime = instant.toLocalDateTime(timeZone)
        val todayDate = clock.now().toLocalDateTime(timeZone).date
        val yesterdayDate = todayDate.minus(1, DateTimeUnit.DAY)
        val formattedTime = messageDateTime.format(
            LocalDateTime.Format {
                amPmHour()
                chars(":")
                minute()
                chars(":")
                amPmMarker("am","pm")
            }
        )
        val formattedDateTime = messageDateTime.format(
            LocalDateTime.Format {
                day()
                chars("/")
                monthNumber()
                chars("/")
                year()
                chars("/")
                amPmHour()
                chars(formattedTime)
            }
        )
        return when(messageDateTime.date){
            todayDate -> UiText.Resource(Res.string.today_x, arrayOf(formattedTime))
            yesterdayDate -> UiText.Resource(Res.string.yesterday_x, arrayOf(formattedTime))
            else -> UiText.DynamicString(formattedDateTime)
        }
    }
}