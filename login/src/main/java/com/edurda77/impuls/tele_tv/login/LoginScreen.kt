package com.edurda77.impuls.tele_tv.login

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import com.edurda77.impuls.tele_tv.resources.uikit.UiTextField
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import com.edurda77.impuls.tele_tv.resources.R

@Composable
fun LoginScreenRoot(
    viewModel: LoginScreenViewModel = koinViewModel(),
    onNavigateToChannels: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {  }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiLoginEvents.ChannelsNavigationEvent -> {
                    onNavigateToChannels()
                }
            }
        }
    }

    LoginScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun LoginScreenScreen(
    modifier: Modifier = Modifier,
    state: LoginScreenState,
    onAction: (LoginScreenAction) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(key1 = state.message) {
        state.message?.let { message ->
            Toast.makeText(context, message.asString(context), Toast.LENGTH_LONG).show()
        }
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        colors = SurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onTertiary,
                            MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier
                    .width(500.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2C3E50))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo2),
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth,
                    modifier = modifier
                        .width(64.dp)
                )/*

                Icon(
                    painter = painterResource(id = android.R.drawable.ic_dialog_email),
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier
                        .width(64.dp)
                        .height(64.dp)
                )*/

                Text(
                    text = stringResource(R.string.enter_to_system),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    UiTextField(
                        modifier = modifier,
                        content = state.username,
                        label = stringResource(R.string.login),
                        onClickContent = {
                            onAction(LoginScreenAction.OnSetUsername(it))
                        },
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusRequester.requestFocus()
                            }
                        ),
                        imeAction = ImeAction.Next,
                        focusRequester = focusRequester,
                        onChangeFocus = {
                            focusManager.moveFocus(it)
                        }
                    )
                }
                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.password),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    UiTextField(
                        modifier = modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .focusable(),
                        content = state.password,
                        label = stringResource(R.string.password),
                        onClickContent = {
                            onAction(LoginScreenAction.OnSetPassword(it))
                        },
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (state.username.isNotBlank() && state.password.isNotBlank()) {
                                    onAction(LoginScreenAction.OnLogin)
                                }
                                keyboardController?.hide()
                            }
                        ),
                        imeAction = ImeAction.Done,
                        focusRequester = focusRequester,
                        onChangeFocus = {
                            focusManager.moveFocus(it)
                        }
                    )
                }

                Spacer(modifier = modifier.height(16.dp))
                Button(
                    onClick = {
                        onAction(LoginScreenAction.OnLogin)
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = state.username.isNotBlank() && state.password.isNotBlank(),
                    colors = ButtonDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = ButtonDefaults.shape(
                        shape = RoundedCornerShape(8.dp)
                    )
                ) {
                    Text(
                        modifier = modifier
                            .fillMaxWidth(),
                        text = if (state.isLoading) stringResource(R.string.checking) else stringResource(R.string.enter),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Tele_TvTheme {
        LoginScreenScreen(
            state = LoginScreenState(),
            onAction = {}
        )
    }
}