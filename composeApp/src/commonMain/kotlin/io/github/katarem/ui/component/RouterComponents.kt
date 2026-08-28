package io.github.katarem.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomBar(
    menuActions: List<Pair<ImageVector, () -> Unit>>,
    selectedTabIndex: Int,
    modifier: Modifier
) = NavigationBar {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        menuActions.forEachIndexed { index, (icon, action) ->
            val iconModifier = if(index == selectedTabIndex) {
                Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            } else {
                Modifier
            }
            IconButton(
                modifier = iconModifier,
                onClick = { action() },
                content = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            )
        }
    }
}
