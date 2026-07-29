package com.android.tvapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.android.tvapp.ui.navigation.TvAppNavGraph
import com.android.tvapp.ui.theme.TvAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TvAppTheme {
                TvAppNavGraph()
            }
        }
    }
}
