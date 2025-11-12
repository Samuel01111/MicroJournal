package com.plcoding.echojournal.core.presentation.designsystem.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.layout.Row
import com.plcoding.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.plcoding.echojournal.core.presentation.designsystem.theme.buttonGradient

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        modifier = modifier.background(
            brush = if (enabled) {
                MaterialTheme.colorScheme.buttonGradient
            } else SolidColor(MaterialTheme.colorScheme.surfaceVariant),
            shape = CircleShape
        ),
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = if (enabled) {
                MaterialTheme.colorScheme.onPrimary
            } else MaterialTheme.colorScheme.outline
        )
    ) {
        leadingIcon?.invoke()

        if (leadingIcon != null) {
            Spacer(modifier = Modifier.width(6.dp))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) {
                MaterialTheme.colorScheme.onPrimary
            } else MaterialTheme.colorScheme.outline
        )
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    EchoJournalTheme {
        PrimaryButton(
            text = "Primary Button",
            onClick = {},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        )
    }
}