package com.ghost.bolt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.ghost.bolt.api.TmdbApi
import com.ghost.bolt.database.AppDatabase
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.ui.navigation.NavigationRoot
import com.ghost.bolt.ui.screen.HomeScreen
import com.ghost.bolt.ui.theme.BoltTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
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
                NavigationRoot( modifier = Modifier.fillMaxSize())
            }
        }
    }
}

