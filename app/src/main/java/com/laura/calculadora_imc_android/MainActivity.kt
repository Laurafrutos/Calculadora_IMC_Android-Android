package com.laura.calculadora_imc_android

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider
import java.util.Locale
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var heightSlider: Slider
    private lateinit var rgGenero: RadioGroup
    private lateinit var calculateButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var resultTextView2: TextView


    private lateinit var tvWeight: TextView
    private lateinit var btnAddWeight: FloatingActionButton
    private lateinit var btnSubtractWeight: FloatingActionButton

    // NUEVO: Variable para almacenar el peso actual en memoria
    private var currentWeight: Int = 70

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupWindowInsets()
        initViews()
        setupWeightButtons() // NUEVO: Configura los clics de + y -

        calculateButton.setOnClickListener {
            val height = heightSlider.value.toDouble()
            // MODIFICADO: Ahora el peso se lee directamente de nuestra variable numérica
            val weight = currentWeight.toDouble()
            val selectedGenderId = rgGenero.checkedRadioButtonId

            if (height == 0.0) {
                showDivisionByZeroWarning()
            } else {
                val imc = calculateIMC(height, weight)
                displayResult(imc)

                // MODIFICADO: Aplica el texto y el color del estado de forma dinámica
                configurarResultadoVisual(imc, selectedGenderId)
            }
        }
    }

    private fun initViews() {
        heightSlider = findViewById(R.id.heightSlider)
        rgGenero = findViewById(R.id.rg_genero)
        calculateButton = findViewById(R.id.calculateButton)
        resultTextView = findViewById(R.id.resultTextView)
        resultTextView2 = findViewById(R.id.resultTextView2)

        // NUEVO: Inicialización de los nuevos componentes visuales de peso
        tvWeight = findViewById(R.id.tvWeight)
        btnAddWeight = findViewById(R.id.btnAddWeight)
        btnSubtractWeight = findViewById(R.id.btnSubtractWeight)

        // Sincronizar el texto inicial con la variable
        updateWeightText()
    }

    // NUEVO: Configuración del comportamiento de los botones + y -
    private fun setupWeightButtons() {
        btnAddWeight.setOnClickListener {
            if (currentWeight < 250) { // Límite máximo lógico de peso
                currentWeight++
                updateWeightText()
            }
        }

        btnSubtractWeight.setOnClickListener {
            if (currentWeight > 10) { // Límite mínimo lógico de peso
                currentWeight--
                updateWeightText()
            }
        }
    }

    // NUEVO: Método auxiliar para refrescar el TextView del peso
    private fun updateWeightText() {
        tvWeight.text = currentWeight.toString()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun calculateIMC(heightCm: Double, weightKg: Double): Double {
        val heightMeters = heightCm / 100.0
        return weightKg / heightMeters.pow(2)
    }

    private fun displayResult(imc: Double) {
        // MODIFICADO: Formato de un solo decimal para coincidir con el diseño estilizado (.1f)
        resultTextView.text = String.format(Locale.getDefault(), "%.1f", imc)
    }

    // MODIFICADO: Devuelve el texto y pinta el color correspondiente usando Hexadecimales directos
    private fun configurarResultadoVisual(imc: Double, selectedGenderId: Int) {
        val (estado, colorHex) = if (selectedGenderId == R.id.rb_mujer) {
            when {
                imc < 19.0 -> Pair("BAJO PESO", "#FF5252") // Rojo
                imc in 19.0..23.9 -> Pair("NORMAL", "#24D876") // Verde
                imc in 24.0..28.9 -> Pair("SOBREPESO", "#FFEB3B") // Amarillo
                else -> Pair("OBESIDAD", "#FF5252") // Rojo
            }
        } else {
            when {
                imc < 20.0 -> Pair("BAJO PESO", "#FF5252")
                imc in 20.0..24.9 -> Pair("NORMAL", "#24D876")
                imc in 25.0..29.9 -> Pair("SOBREPESO", "#FFEB3B")
                else -> Pair("OBESIDAD", "#FF5252")
            }
        }

        resultTextView2.text = estado
        resultTextView2.setTextColor(Color.parseColor(colorHex))
    }

    private fun showDivisionByZeroWarning() {
        Toast.makeText(this, "La altura no puede ser cero", Toast.LENGTH_SHORT).show()
    }
}
