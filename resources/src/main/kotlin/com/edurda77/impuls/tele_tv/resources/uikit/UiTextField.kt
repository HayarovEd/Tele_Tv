package com.edurda77.impuls.tele_tv.resources.uikit

import android.content.res.Configuration
import android.view.KeyEvent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

@Composable
fun UiTextField(
    modifier: Modifier = Modifier,
    content: String,
    label: String,
    onChangeFocus: (FocusDirection) -> Unit,
    focusRequester: FocusRequester,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onClickContent: (String) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    BasicTextField(
        modifier = modifier
            .border(width = if(isFocused) 2.dp else 0.dp, color =  if(isFocused) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.border)
            .fillMaxWidth()
            .padding(
                vertical = 4.dp,
                horizontal = 8.dp
            )
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .onKeyEvent {
                if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                    when (it.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            onChangeFocus(FocusDirection.Down)
                        }

                        KeyEvent.KEYCODE_DPAD_UP -> {
                            onChangeFocus(FocusDirection.Up)
                        }

                        KeyEvent.KEYCODE_BACK -> {
                            onChangeFocus(FocusDirection.Exit)
                        }
                    }
                }
                true
            },
        value = content,
        onValueChange = { text -> onClickContent(text) },
        decorationBox = {
            Box(
                modifier = modifier
                    .padding(vertical = 16.dp)
                    .padding(start = 20.dp),
            ) {
                it()
                if (content.isEmpty()) {
                    Text(
                        modifier = modifier.graphicsLayer { alpha = 0.6f },
                        text = label,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        },
        cursorBrush = Brush.verticalGradient(
            colors = listOf(
                LocalContentColor.current,
                LocalContentColor.current,
            )
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions/*KeyboardActions(
            onSearch = {
                searchMovies(searchQuery)
            }
        )*/,
        maxLines = 1,
        textStyle = MaterialTheme.typography.titleSmall.copy(
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Preview(
    showBackground = true
)
@Composable
private fun UiTextFieldView() {
    Tele_TvTheme {
        UiTextField(
            content = "",
            label = "hallo",
            focusRequester = remember { FocusRequester() },
            onChangeFocus = {},
            onClickContent = {}
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun UiTextFieldView2() {
    Tele_TvTheme {
        UiTextField(
            content = "world!",
            label = "hallo",
            focusRequester = remember { FocusRequester() },
            onChangeFocus = {},
            onClickContent = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun UiTextFieldView3() {
    Tele_TvTheme {
        UiTextField(
            content = "",
            label = "hallo",
            focusRequester = remember { FocusRequester() },
            onChangeFocus = {},
            onClickContent = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun UiTextFieldView4() {
    Tele_TvTheme {
        UiTextField(
            content = "world!",
            label = "hallo",
            focusRequester = remember { FocusRequester() },
            onChangeFocus = {},
            onClickContent = {}
        )
    }
}