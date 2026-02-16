package com.ghost.bolt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.ghost.bolt.database.AppDatabase
import com.ghost.bolt.ui.navigation.NavigationRoot
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
                NavigationRoot(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

