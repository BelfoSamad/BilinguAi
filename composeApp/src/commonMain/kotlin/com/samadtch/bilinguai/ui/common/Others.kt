package com.samadtch.bilinguai.ui.common

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.samadtch.bilinguai.Resources
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CustomSnackbar(content: String, isSuccess: Boolean = false) {
    Snackbar(
        modifier = Modifier.padding(16.dp),
        containerColor = if (isSuccess) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SortDropdown(
    modifier: Modifier,
    onSortPicked: (String) -> Unit
) {
    var sortByDate by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier) {
        Row(
            modifier = Modifier
                .clickable(onClick = { expanded = true })
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.secondary), CircleShape)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp).align(Alignment.CenterVertically),
                text = stringResource(
                    Resources.strings.sort_by_val,
                    if (sortByDate) stringResource(Resources.strings.date)
                    else stringResource(Resources.strings.name)
                ),
                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary)
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Default.ExpandMore,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = "Dropdown"
            )
        }
        DropdownMenu(
            modifier = Modifier.width(164.dp).background(color = MaterialTheme.colorScheme.primary),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            //Sort By
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 24.dp),
                text = stringResource(Resources.strings.sort_by),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
            DropdownMenuItem(
                text = {
                    Row(Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = sortByDate,
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = MaterialTheme.colorScheme.secondary,
                                checkedColor = MaterialTheme.colorScheme.tertiary,
                                checkmarkColor = MaterialTheme.colorScheme.primary,
                            ),
                            onCheckedChange = {
                                sortByDate = true
                                onSortPicked("Date")
                                expanded = false
                            },
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(end = 16.dp),
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary),
                            text = stringResource(Resources.strings.date)
                        )
                    }
                },
                onClick = {
                    sortByDate = true
                    onSortPicked("Date")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Row(Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = !sortByDate,
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = MaterialTheme.colorScheme.secondary,
                                checkedColor = MaterialTheme.colorScheme.tertiary,
                                checkmarkColor = MaterialTheme.colorScheme.primary,
                            ),
                            onCheckedChange = {
                                sortByDate = false
                                onSortPicked("Topic")
                                expanded = false
                            },
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f)
                                .padding(end = 16.dp),
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary),
                            text = stringResource(Resources.strings.name)
                        )
                    }
                },
                onClick = {
                    sortByDate = false
                    onSortPicked("Topic")
                    expanded = false
                }
            )
        }
    }
}

/*
 * Shimmer Effect
 *
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000)
        )
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.DarkGray,
                MaterialTheme.colorScheme.tertiary,
                Color.DarkGray
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        ),
        shape = MaterialTheme.shapes.large
    ).onGloballyPositioned {
        size = it.size
    }
}

val shimmerModifier = Modifier
    .fillMaxWidth()
    .padding(16.dp, 8.dp)
    .height(24.dp)
    .shimmerEffect()