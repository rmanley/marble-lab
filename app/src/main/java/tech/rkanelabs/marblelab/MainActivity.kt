@file:OptIn(ExperimentalMaterial3Api::class)

package tech.rkanelabs.marblelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import dagger.hilt.android.AndroidEntryPoint
import tech.rkanelabs.marblelab.ui.LevelEditorScreen
import tech.rkanelabs.marblelab.ui.theme.MarbleLabTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarbleLabTheme {
                // todo: routes??
                LevelEditorScreen()
            }
        }
    }
}
