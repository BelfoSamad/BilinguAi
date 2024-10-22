package com.samadtch.bilinguai.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(content: @Composable () -> Unit) {

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF151515),//Black
            secondary = Color(0xFF666666),//Gray
            tertiary = Color(0xFFDAEF68),//Bright
            error = Color(0xFFE74C3C),
            surfaceVariant = Color.Transparent,
        ),
        typography = typography,
        shapes = shapes,
        content = content
    )
}

/**
 *
 * TextFields
 *
 */
@Composable
fun PrimaryTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = MaterialTheme.colorScheme.primary,
        unfocusedTextColor = MaterialTheme.colorScheme.primary,
        disabledTextColor = MaterialTheme.colorScheme.primary,
        disabledPlaceholderColor = MaterialTheme.colorScheme.primary,
        selectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.secondary,
            backgroundColor = MaterialTheme.colorScheme.secondary,
        ),
        //Error
        errorTextColor = MaterialTheme.colorScheme.primary,
        errorTrailingIconColor = MaterialTheme.colorScheme.primary,
        errorIndicatorColor = MaterialTheme.colorScheme.primary,
        errorSupportingTextColor = MaterialTheme.colorScheme.primary,
        errorPlaceholderColor = MaterialTheme.colorScheme.primary,
        errorCursorColor = MaterialTheme.colorScheme.primary,
        //Text
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        //PlaceHolder
        focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary,
        //Indicator
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
        disabledIndicatorColor = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun SecondaryTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        cursorColor = MaterialTheme.colorScheme.tertiary,
        focusedTextColor = MaterialTheme.colorScheme.tertiary,
        unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
        disabledTextColor = MaterialTheme.colorScheme.tertiary,
        disabledPlaceholderColor = MaterialTheme.colorScheme.tertiary,
        selectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.secondary,
            backgroundColor = MaterialTheme.colorScheme.secondary,
        ),
        //Error
        errorTextColor = MaterialTheme.colorScheme.error,
        errorTrailingIconColor = MaterialTheme.colorScheme.error,
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        errorSupportingTextColor = MaterialTheme.colorScheme.error,
        errorPlaceholderColor = MaterialTheme.colorScheme.secondary,
        errorCursorColor = MaterialTheme.colorScheme.error,
        //Text
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
        focusedTrailingIconColor = MaterialTheme.colorScheme.tertiary,
        //PlaceHolder
        focusedPlaceholderColor = MaterialTheme.colorScheme.secondary,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.secondary,
        //Indicator
        focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
        disabledIndicatorColor = MaterialTheme.colorScheme.secondary,
    )
}

/**
 *
 * Buttons
 *
 */
@Composable
fun PrimaryFilledButtonColors(): ButtonColors {
    return ButtonDefaults.filledTonalButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.tertiary
    )
}

@Composable
fun SecondaryFilledButtonColors(): ButtonColors {
    return ButtonDefaults.filledTonalButtonColors(
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ErrorFilledButtonColors(): ButtonColors {
    return ButtonDefaults.filledTonalButtonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun PrimaryIconButtonColors(): IconButtonColors {
    return IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.tertiary,
    )
}

@Composable
fun SecondaryIconButtonColors(): IconButtonColors {
    return IconButtonDefaults.outlinedIconButtonColors(
        contentColor = MaterialTheme.colorScheme.secondary
    )
}