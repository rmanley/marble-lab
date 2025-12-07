@file:OptIn(ExperimentalMaterial3Api::class)

package tech.rkanelabs.marblelab.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import tech.rkanelabs.marblelab.ui.theme.MarbleLabTheme

@Composable
fun MarbleLabTopAppBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("Marble Lab")
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun MarbleLabTopAppBarPreview() {
    MarbleLabTheme {
        MarbleLabTopAppBar()
    }
}