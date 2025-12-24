package com.example.awwal.presentation.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.awwal.presentation.viewmodel.ThemeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.awwal.domain.classes.enums.ThemeMode

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = viewModel()
) {
    val currentTheme by viewModel.themeState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Theme Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ThemeOption(
            text = "Light",
            selected = currentTheme == ThemeMode.LIGHT,
            onClick = { viewModel.updateTheme(ThemeMode.LIGHT) }
        )
        ThemeOption(
            text = "Dark",
            selected = currentTheme == ThemeMode.DARK,
            onClick = { viewModel.updateTheme(ThemeMode.DARK) }
        )
        ThemeOption(
            text = "System Default",
            selected = currentTheme == ThemeMode.AUTO,
            onClick = { viewModel.updateTheme(ThemeMode.AUTO) }
        )
    }
}

@Composable
fun ThemeOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null recommended for accessibility with selectable
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}