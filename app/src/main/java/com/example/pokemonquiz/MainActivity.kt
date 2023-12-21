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

    private var pokemonIds: List<Int> = getRandomNumbers()
    private var index = mutableIntStateOf(0)

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
                            horizontalArrangement = Arrangement.End
                        ) {

                            Button(onClick = {
                                next(skipAlert = true)
                                init()
                            }) {
                                Text(text = "Restart")
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Button(onClick = { finish() }) {
                                Text(text = "Close")
                            }
                        }
                        Text(
                            text = "Quiz ${index.value + 1}: No.${
                                String.format(
                                    "%04d",
                                    pokemonIds[index.value]
                                )
                            }",
                            fontSize = 30.sp
                        )
                        AsyncImage(
                            model = String.format(POKEMON_IMAGE_URL, pokemonIds[index.value]),
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
        index.value = 0
        pokemonIds = getRandomNumbers()
    }

    private fun next(skipAlert: Boolean = false) {
        if (index.value >= TOTAL_COUNT - 1) {
            if (skipAlert) {
                AlertDialog.Builder(this)
                    .setTitle("Pokemon Quiz")
                    .setMessage("Game over!")
                    .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
                    .show()
            }
            return
        }
        index.value++
    }

    private fun getRandomNumbers(): List<Int> {
        val numbers = mutableListOf<Int>()
        while (numbers.size < TOTAL_COUNT) {
            val number = Random.nextInt(START_ID, END_ID + 1)
            if (!numbers.contains(number)) {
                numbers.add(number)
            }
        }
        return numbers
    }

    companion object {
        private const val START_ID = 1
        private const val END_ID = 1010
        private const val TOTAL_COUNT = 50
        private const val POKEMON_IMAGE_URL =
            "https://data1.pokemonkorea.co.kr/newdata/pokedex/mid/%04d01.png"
    }
}
