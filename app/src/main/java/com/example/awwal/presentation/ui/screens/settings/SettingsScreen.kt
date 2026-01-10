package com.example.awwal.presentation.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.awwal.domain.classes.CalculationMethodType
import com.example.awwal.domain.classes.MadhabType
import com.example.awwal.domain.classes.enums.ThemeMode
import com.example.awwal.presentation.viewmodel.PrayerTimesSettingsViewModel
import com.example.awwal.presentation.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = koinViewModel(),
    prayerTimesSettingsViewModel: PrayerTimesSettingsViewModel = koinViewModel()
) {
    val currentTheme by themeViewModel.themeState.collectAsState()
    val prayerSettings by prayerTimesSettingsViewModel.settings.collectAsState()

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Theme Settings Section
        SettingsSection(title = "Theme Settings") {
            ThemeOption(
                text = "Light",
                selected = currentTheme == ThemeMode.LIGHT,
                onClick = { themeViewModel.updateTheme(ThemeMode.LIGHT) }
            )
            ThemeOption(
                text = "Dark",
                selected = currentTheme == ThemeMode.DARK,
                onClick = { themeViewModel.updateTheme(ThemeMode.DARK) }
            )
            ThemeOption(
                text = "System Default",
                selected = currentTheme == ThemeMode.AUTO,
                onClick = { themeViewModel.updateTheme(ThemeMode.AUTO) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Prayer Times Settings Section
        SettingsSection(title = "Prayer Times Settings") {
            // Location
            Text(
                text = "Location",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "Latitude: %.4f, Longitude: %.4f".format(
                    prayerSettings.latitude,
                    prayerSettings.longitude
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calculation Method Dropdown
            var methodExpanded by remember { mutableStateOf(false) }
            Text(
                text = "Calculation Method",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = methodExpanded,
                onExpandedChange = { methodExpanded = !methodExpanded }
            ) {
                OutlinedTextField(
                    value = prayerSettings.calculationMethod.displayName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = methodExpanded,
                    onDismissRequest = { methodExpanded = false }
                ) {
                    CalculationMethodType.entries.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method.displayName) },
                            onClick = {
                                prayerTimesSettingsViewModel.updateCalculationMethod(method)
                                methodExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Madhab Selection
            Text(
                text = "Madhab (for Asr)",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            MadhabType.entries.forEach { madhab ->
                ThemeOption(
                    text = madhab.displayName,
                    selected = prayerSettings.madhab == madhab,
                    onClick = { prayerTimesSettingsViewModel.updateMadhab(madhab) }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Changed from surfaceVariant to surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
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