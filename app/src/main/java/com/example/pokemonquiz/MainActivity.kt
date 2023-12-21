package com.example.pokemonquiz

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pokemonquiz.ui.theme.PokemonQuizTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private var pokemonPageIndices: List<Int> = getRandomIndices()
    private var orderIndex = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContent {
            PokemonQuizTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Button(onClick = {
                                restart()
                            }) {
                                Text(text = "Restart")
                            }
                            Button(onClick = {
                                startActivity(
                                    PokemonWebView.createIntent(
                                        this@MainActivity,
                                        pageViewIndex = pokemonPageIndices[orderIndex.value]
                                    )
                                )
                            }) {
                                Text(text = "Answer")
                            }
                        }
                        Text(
                            text = "Quiz ${orderIndex.value + 1}",
                            fontSize = 30.sp
                        )
                        AsyncImage(
                            model = PokemonData.getImageUrl(pokemonPageIndices[orderIndex.value]),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { next() }
                        )
                    }
                }
            }
        }
    }

    private fun init() {
        orderIndex.value = 0
        pokemonPageIndices = getRandomIndices()
    }

    private fun restart() {
        next(skipAlert = true)
        init()
    }

    private fun next(skipAlert: Boolean = false) {
        if (orderIndex.value >= TOTAL_COUNT - 1) {
            if (!skipAlert) {
                AlertDialog.Builder(this)
                    .setTitle("Pokemon Quiz")
                    .setMessage("Game over!")
                    .setPositiveButton("Restart") { _, _ -> restart() }
                    .show()
            }
            return
        }
        orderIndex.value++
    }

    private fun getRandomIndices(): List<Int> {
        val indices = mutableListOf<Int>()
        val random = Random(System.currentTimeMillis())
        while (indices.size < TOTAL_COUNT) {
            val index = random.nextInt(0, PokemonData.MAX_PAGE_VIEW_INDEX + 1)
            if (!indices.contains(index)) {
                indices.add(index)
            }
        }
        return indices
    }

    companion object {
        private const val TOTAL_COUNT = 50
    }
}
