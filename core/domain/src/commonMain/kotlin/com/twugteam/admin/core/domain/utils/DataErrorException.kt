package com.twugteam.admin.core.domain.utils

class DataErrorException (
    val error: DataError
): Exception() {

}