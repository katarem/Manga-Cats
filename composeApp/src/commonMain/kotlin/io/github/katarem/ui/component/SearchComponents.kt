package io.github.katarem.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Filter
import compose.icons.feathericons.Search
import io.github.katarem.data.constant.Demographic
import io.github.katarem.data.model.Tag
import io.github.katarem.ui.state.SearchFilters
import io.github.katarem.ui.state.SearchState

@Composable
fun Searchbar(
    search: String,
    onTyped: (String) -> Unit,
    onSearch: () -> Unit,
    toggleFilters: () -> Unit,
    modifier: Modifier = Modifier
) = TextField(
    value = search,
    onValueChange = onTyped,
    modifier = modifier,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Search,
    ),
    keyboardActions = KeyboardActions(
        onSearch = { onSearch() }
    ),
    singleLine = true,
    leadingIcon = {
        IconButton(
            onClick = toggleFilters
        ) {
            Icon(imageVector = FeatherIcons.Filter, contentDescription = "Filter")
        }
    },
    trailingIcon = {
        Icon(imageVector = FeatherIcons.Search, contentDescription = "Search")
    },
    shape = RoundedCornerShape(20.dp),
    colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    selectedTags: List<String>,
    selectedDemographic: String,
    tags: List<Tag>,
    onDismiss: () -> Unit,
    onApply: (List<String>, String) -> Unit
) {
    var selectedTags by remember { mutableStateOf(selectedTags) }
    var selectedDemographic by remember { mutableStateOf(selectedDemographic) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onApply(selectedTags, selectedDemographic) },
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text("Close")
            }
        },
        title = { Text("Filters", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                LazyColumn {
                    stickyHeader {
                        Text(
                            text = "Demographic",
                            style = MaterialTheme.typography.displayMedium,
                        )
                    }
                    choiceSelector(
                        items = Demographic.entries.toList().reversed(),
                        selectedItem = selectedDemographic,
                        onSelected = {
                            selectedDemographic = it
                        }
                    )
                }
                TagSelector(
                    tags = tags,
                    selectedTags = selectedTags,
                    onSelected = { tag, selected ->
                        selectedTags = if(selected)
                            selectedTags.plus(tag)
                        else
                            selectedTags.minus(tag)
                    }
                )
            }
        }
    )
}

fun LazyListScope.choiceSelector(items: List<Demographic>, selectedItem: String, onSelected: (String) -> Unit) =
    items(items) { option ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(option.name)
            RadioButton(
                selected = option.value == selectedItem,
                onClick = {
                    onSelected(option.value)
                },
            )
        }
    }

@Composable
fun TagSelector(
    tags: List<Tag>,
    selectedTags: List<String>,
    onSelected: (String, Boolean) -> Unit
) {
    Text("Tags", style = MaterialTheme.typography.displayMedium)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tags.forEach { tag ->
            val selected = selectedTags.contains(tag.id)
            FilterChip(
                selected = selected,
                onClick = { onSelected(tag.id, !selected) },
                label = { Text(tag.name) }
            )
        }
    }
}