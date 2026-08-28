package io.github.katarem.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.katarem.data.constant.ContentRating
import io.github.katarem.data.constant.Language
import io.github.katarem.ui.component.SelectionSetting
import io.github.katarem.ui.component.VersionDisplay
import io.github.katarem.ui.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>(),
) {

    val isCascade by viewModel.preferredReadingMode.collectAsState()
    val isSavingMode by viewModel.preferredQuality.collectAsState()
    val preferredLanguage by viewModel.preferredLanguage.collectAsState()
    val preferredRating by viewModel.preferredRating.collectAsState()

    val languages = Language.entries.map { Pair(it.name, it.value) }
    val ratings = ContentRating.entries.map { Pair(it.name, it.value) }


    Column(
        modifier = Modifier.fillMaxSize().padding(5.dp)
    ) {
        SelectionSetting(
            title = "Language",
            selectedValue = languages.firstOrNull { it.second == preferredLanguage  }?.first ?: "",
            options = languages,
            onSelect = { selected ->
                viewModel.setPreferredLanguage(selected)
            },
        )
        SelectionSetting(
            title = "Rating",
            selectedValue = ratings.firstOrNull { it.second == preferredRating }?.first ?: "",
            options = ratings,
            onSelect = { selected ->
                viewModel.setPreferredRating(selected)
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Cascade Reading Mode")
            Switch(
                checked = isCascade,
                onCheckedChange = { newValue ->
                    viewModel.setPreferredReadingMode(newValue)
                }
            )

        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Data Saving Mode")
            Switch(
                checked = isSavingMode,
                onCheckedChange = { newValue ->
                    viewModel.setPreferredQualityMode(newValue)
                }
            )
        }
        VersionDisplay()
    }
}