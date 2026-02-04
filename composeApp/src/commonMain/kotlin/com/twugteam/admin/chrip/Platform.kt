package com.twugteam.admin.chrip

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform