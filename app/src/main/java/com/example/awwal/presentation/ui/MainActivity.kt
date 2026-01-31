package com.example.awwal.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.awwal.presentation.ui.theme.AwwalTheme
import com.example.awwal.NavigationBar
import com.example.awwal.presentation.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by themeViewModel.themeState.collectAsState()

            AwwalTheme(theme = theme) {
                App()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun App() {
    NavigationBar()
}