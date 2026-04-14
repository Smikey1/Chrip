package com.twugteam.admin.core.data.logging

import co.touchlab.kermit.Logger
import com.twugteam.admin.core.domain.logging.ChripLogger

object KermitLogger : ChripLogger {
    override fun info(message: String) {
        Logger.i(message)
    }

    override fun warn(message: String) {
        Logger.w(message)
    }

    override fun debug(message: String) {
        Logger.d(message)
    }

    override fun error(message: String, throwable: Throwable?) {
        Logger.e(message,throwable)
    }

}