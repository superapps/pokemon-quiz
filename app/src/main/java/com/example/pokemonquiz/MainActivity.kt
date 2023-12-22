package com.example.pokemonquiz

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pokemonquiz.ui.theme.PokemonQuizTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private var pokemonPageIndices: List<Int> = getRandomIndices()
    private var blindTypes: List<BlindType> = getBlindTypes()
    private var blindFractions: List<Float> = getBlindFractions()
    private var orderIndex = mutableIntStateOf(0)
    private val hardModeCheckedState = mutableStateOf(false)

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
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = hardModeCheckedState.value, onCheckedChange = {
                                    hardModeCheckedState.value = it
                                })
                                Text(text = "Hard")
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
                        Box(
                            contentAlignment = getBlindBoxAlignment()
                        ) {
                            AsyncImage(
                                model = PokemonData.getImageUrl(pokemonPageIndices[orderIndex.value]),
                                contentDescription = null,
                                modifier = getBlindImageBlueModifier().clickable { next() }
                            )
                            Box(
                                modifier = getBlindBoxModifier()
                            )
                        }
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
                    .setCancelable(false)
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

    private fun getBlindTypes(): List<BlindType> {
        val blindTypes = mutableListOf<BlindType>()
        val values = BlindType.values() + listOf(BlindType.BLUR, BlindType.BLUR, BlindType.BLUR)
        while (blindTypes.size < TOTAL_COUNT) {
            blindTypes.add(values.random())
        }
        return blindTypes
    }

    private fun getBlindImageBlueModifier(): Modifier {
        if (!hardModeCheckedState.value) {
            return Modifier.fillMaxSize()
        }
        return when (blindTypes[orderIndex.value]) {
            BlindType.LEFT,
            BlindType.TOP,
            BlindType.RIGHT,
            BlindType.BOTTOM -> Modifier.fillMaxSize()

            BlindType.BLUR -> Modifier
                .fillMaxSize()
                .blur(
                    when (orderIndex.value % 3) {
                        0 -> 20.dp
                        1 -> 30.dp
                        3 -> 40.dp
                        else -> 40.dp
                    }
                )
        }
    }

    private fun getBlindFractions(): List<Float> {
        val blindFractions = mutableListOf<Float>()
        val fractionRange = (35..55 step 1).toList().map { it.toFloat() / 100 }
        while (blindFractions.size < TOTAL_COUNT) {
            blindFractions.add(fractionRange.random())
        }
        return blindFractions
    }

    private fun getBlindBoxAlignment(): Alignment =
        if (!hardModeCheckedState.value) {
            Alignment.TopStart
        } else {
            when (blindTypes[orderIndex.value]) {
                BlindType.BLUR,
                BlindType.LEFT,
                BlindType.TOP -> Alignment.TopStart

                BlindType.RIGHT,
                BlindType.BOTTOM -> Alignment.BottomEnd
            }
        }

    private fun getBlindBoxModifier(): Modifier =
        if (!hardModeCheckedState.value) {
            Modifier.size(0.dp)
        } else {
            val blindColor = if (isDarkMode()) {
                Color(red = 28, green = 27, blue = 31)
            } else {
                Color(red = 255, green = 251, blue = 254)
            }
            val blindFraction = blindFractions[orderIndex.value]
            when (blindTypes[orderIndex.value]) {
                BlindType.LEFT,
                BlindType.RIGHT -> Modifier
                    .background(blindColor)
                    .fillMaxWidth(blindFraction)
                    .fillMaxHeight()

                BlindType.TOP,
                BlindType.BOTTOM -> Modifier
                    .background(blindColor)
                    .fillMaxWidth()
                    .fillMaxHeight(blindFraction)

                BlindType.BLUR -> Modifier.size(0.dp)
            }
        }

    private fun isDarkMode(): Boolean =
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

    enum class BlindType {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        BLUR
    }

    companion object {
        private const val TOTAL_COUNT = 50
    }
}
