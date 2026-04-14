package com.twugteam.admin.core.domain.logging

interface ChripLogger {
    fun info(message: String)
    fun warn(message: String)
    fun debug(message: String)
    fun error(message: String, throwable: Throwable? = null)
}