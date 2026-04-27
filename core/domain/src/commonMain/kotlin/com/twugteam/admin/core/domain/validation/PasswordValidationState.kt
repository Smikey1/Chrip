package com.twugteam.admin.core.domain.validation

data class PasswordValidationState(
    val hasMinLength: Boolean = false,
    val hasAtLeastOneUpperCaseChar: Boolean = false,
    val hasAtLeastOneLowerCaseChar: Boolean = false,
    val hasNumber: Boolean = false
) {
    val isValidPassword: Boolean
        get() = hasMinLength && hasNumber && hasAtLeastOneUpperCaseChar && hasAtLeastOneLowerCaseChar
}
