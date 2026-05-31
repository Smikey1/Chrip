package com.twugteam.admin.chat.presentation.profile

sealed interface ProfileAction {
    data object OnDismiss : ProfileAction
    data object OnUploadPictureClick : ProfileAction
    data object OnErrorImagePicture : ProfileAction
    data class OnImageUriSelected(val uri: String) : ProfileAction
    class OnPictureSelected(val bytes: ByteArray, val mimeType: String?) : ProfileAction

    data object OnDeletePictureClick : ProfileAction
    data object OnConfirmDeleteClick : ProfileAction
    data object OnDismissDeleteConfirmationDialogClick : ProfileAction
    data object OnToggleCurrentPasswordVisibilityClick : ProfileAction
    data object OnToggleNewPasswordVisibilityClick : ProfileAction
    data object OnChangePasswordClick : ProfileAction

}