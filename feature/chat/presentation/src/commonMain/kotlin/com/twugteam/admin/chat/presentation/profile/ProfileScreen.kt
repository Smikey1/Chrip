package com.twugteam.admin.chat.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twugteam.admin.chat.presentation.profile.components.ProfileAdaptiveLayout
import com.twugteam.admin.chat.presentation.profile.components.ProfileHeaderSection
import com.twugteam.admin.core.designsystem.components.avatar.AvatarSize
import com.twugteam.admin.core.designsystem.components.avatar.ChripAvatarPhoto
import com.twugteam.admin.core.designsystem.components.buttons.ChirpButton
import com.twugteam.admin.core.designsystem.components.buttons.ChripButtonStyle
import com.twugteam.admin.core.designsystem.components.dialogs.ChirpAdaptiveDialogSheetLayout
import com.twugteam.admin.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.twugteam.admin.core.designsystem.components.divider.ChirpHorizontalDivider
import com.twugteam.admin.core.designsystem.components.textfields.ChirpPasswordTextField
import com.twugteam.admin.core.designsystem.components.textfields.ChirpTextField
import com.twugteam.admin.core.designsystem.theme.ChirpTheme
import com.twugteam.admin.core.designsystem.theme.extended
import com.twugteam.admin.core.presentation.util.DeviceConfiguration
import com.twugteam.admin.core.presentation.util.clearFocusOnTapOutside
import com.twugteam.admin.core.presentation.util.getCurrentDeviceConfiguration
import com.twugteam.admin.feature.chat.presentation.Res
import com.twugteam.admin.feature.chat.presentation.cancel
import com.twugteam.admin.feature.chat.presentation.contact_chirp_support_change_email
import com.twugteam.admin.feature.chat.presentation.delete
import com.twugteam.admin.feature.chat.presentation.delete_profile_picture
import com.twugteam.admin.feature.chat.presentation.delete_profile_picture_desc
import com.twugteam.admin.feature.chat.presentation.email
import com.twugteam.admin.feature.chat.presentation.new_password
import com.twugteam.admin.feature.chat.presentation.password
import com.twugteam.admin.feature.chat.presentation.password_change_successful
import com.twugteam.admin.feature.chat.presentation.password_hint
import com.twugteam.admin.feature.chat.presentation.profile_image
import com.twugteam.admin.feature.chat.presentation.save
import com.twugteam.admin.feature.chat.presentation.upload_icon
import com.twugteam.admin.feature.chat.presentation.upload_image
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreenRoot(
    onDismiss: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ChirpAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        ProfileScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    ProfileAction.OnDismiss -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}

@Composable
private fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
) {
    Column(
        modifier = Modifier
            .clearFocusOnTapOutside()
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeaderSection(
            username = state.username,
            onCloseClick = {
                onAction(ProfileAction.OnDismiss)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp,
                    horizontal = 20.dp
                )
        )
        ChirpHorizontalDivider()
        ProfileAdaptiveLayout(
            headerText = stringResource(Res.string.profile_image)
        ) {
            Row {
                ChripAvatarPhoto(
                    displayInitialText = state.userInitials,
                    size = AvatarSize.LARGE,
                    imageUrl = state.profilePictureUrl,
                    onProfileClick = {
                        onAction(ProfileAction.OnUploadPictureClick)
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ChirpButton(
                        text = stringResource(Res.string.upload_image),
                        style = ChripButtonStyle.SECONDARY,
                        enabled = !state.isUploadingImage && !state.isDeletingImage,
                        isLoading = state.isUploadingImage,
                        leadingIcon = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.upload_icon),
                                contentDescription = stringResource(Res.string.upload_image)

                            )
                        },
                        onClick = {
                            onAction(ProfileAction.OnUploadPictureClick)
                        }
                    )

                    ChirpButton(
                        text = stringResource(Res.string.delete),
                        style = ChripButtonStyle.DESTRUCTIVE_SECONDARY,
                        enabled = !state.isUploadingImage
                                && !state.isDeletingImage
                                && state.profilePictureUrl != null,
                        isLoading = state.isUploadingImage,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(Res.string.delete)

                            )
                        },
                        onClick = {
                            onAction(ProfileAction.OnDeletePictureClick)
                        }
                    )
                }
            }
            if (state.imageError != null) {
                Text(
                    text = state.imageError.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        ChirpHorizontalDivider()
        ProfileAdaptiveLayout(
            headerText = stringResource(Res.string.email),
        ) {
            ChirpTextField(
                state = state.emailTextState,
                enabled = false,
                supportingText = stringResource(Res.string.contact_chirp_support_change_email)
            )
        }
        ChirpHorizontalDivider()
        ProfileAdaptiveLayout(
            headerText = stringResource(Res.string.password),
        ) {
            ChirpPasswordTextField(
                state = state.currentPasswordTextState,
                isPasswordVisible = state.isCurrentPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ProfileAction.OnToggleCurrentPasswordVisibilityClick)
                },
                placeholder = stringResource(Res.string.password),
                isError = state.newPasswordError != null
            )

            ChirpPasswordTextField(
                state = state.newPasswordTextState,
                isPasswordVisible = state.isNewPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ProfileAction.OnToggleNewPasswordVisibilityClick)
                },
                placeholder = stringResource(Res.string.new_password),
                isError = state.newPasswordError != null,
                supportingText = state.newPasswordError?.asString()
                    ?: stringResource(Res.string.password_hint)
            )

            if(state.isChangePasswordSuccessful){
                Text(
                    text = stringResource(Res.string.password_change_successful),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.success,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                ChirpButton(
                    text = stringResource(Res.string.cancel),
                    style = ChripButtonStyle.SECONDARY,
                    onClick = {
                        onAction(ProfileAction.OnDismiss)
                    }
                )
                ChirpButton(
                    text = stringResource(Res.string.save),
                    enabled = state.canChangePassword,
                    isLoading = state.isChangingPassword,
                    onClick = {
                        onAction(ProfileAction.OnChangePasswordClick)
                    }
                )
            }
        }
        val currentDeviceConfiguration = getCurrentDeviceConfiguration()
        if (currentDeviceConfiguration in listOf(
                DeviceConfiguration.MOBILE_PORTRAIT,
                DeviceConfiguration.MOBILE_LANDSCAPE
            )
        ) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    if (state.showDeleteConfirmationDialog) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.delete_profile_picture),
            description = stringResource(Res.string.delete_profile_picture_desc),
            cancelButtonText = stringResource(Res.string.cancel),
            confirmButtonText = stringResource(Res.string.delete),
            onDismiss = {
                onAction(ProfileAction.OnDismissDeleteConfirmationDialogClick)
            },
            onConfirmClick = {
                onAction(ProfileAction.OnConfirmDeleteClick)
            },
            onCancelClick = {
                onAction(ProfileAction.OnDismissDeleteConfirmationDialogClick)
            },
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ChirpTheme(
        isDarkTheme = false
    ) {
        ProfileScreen(
            state = ProfileState(),
            onAction = {}
        )
    }
}