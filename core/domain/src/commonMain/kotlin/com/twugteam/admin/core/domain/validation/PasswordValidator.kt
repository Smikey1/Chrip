package com.twugteam.admin.core.domain.validation

object PasswordValidator {

    private const val MINIMUM_PASSWORD_LENGTH = 6

    fun validate(password: String): PasswordValidationState {
        val hasMinLength = password.length >= MINIMUM_PASSWORD_LENGTH
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasNumber = password.any { it.isDigit() }

        return PasswordValidationState(
            hasMinLength = hasMinLength,
            hasAtLeastOneUpperCaseChar = hasUpperCase,
            hasAtLeastOneLowerCaseChar = hasLowerCase,
            hasNumber = hasNumber
        )

    }
}