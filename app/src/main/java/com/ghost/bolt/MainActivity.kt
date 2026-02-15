package com.ghost.bolt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ghost.bolt.database.AppDatabase
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.ui.screen.HomeScreen
import com.ghost.bolt.ui.theme.BoltTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appDatabase: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread {
            appDatabase.openHelper.writableDatabase
            val cursor = appDatabase.query("SELECT 1", null)
            cursor.close()
            Timber.d("Database initialized and opened!")
        }.start()


        enableEdgeToEdge()
        setContent {
            BoltTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(onMediaClick = {}, mediaType = AppMediaType.MOVIE)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BoltTheme {
        Greeting("Android")
    }
}