package com.twugteam.admin.core.domain.utils

class DataErrorException (
    private val error: DataError
): Exception() {

}