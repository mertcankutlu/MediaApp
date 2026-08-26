package com.media.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.media.app.presentation.player.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerTestScreen(viewModel = playerViewModel)
                }
            }
        }
    }
}

@Composable
fun PlayerTestScreen(viewModel: PlayerViewModel) {
    val state by viewModel.playerState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Faz 1: Çekirdek Oynatıcı Testi",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Çalan Parça: ${state.currentTrack?.title ?: "Yok"}")
        Text(text = "Sanatçı: ${state.currentTrack?.artist ?: "Yok"}")
        Text(text = "Durum: ${if (state.isPlaying) "Oynatılıyor ▶" else "Duraklatıldı ⏸"}")
        Text(text = "Süre: ${state.currentPositionMs / 1000}s / ${state.durationMs / 1000}s")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { viewModel.playSampleTrack() }) {
            Text("Örnek Parçayı Başlat")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { viewModel.togglePlayPause() }) {
            Text(if (state.isPlaying) "Duraklat" else "Oynat")
        }
    }
}
