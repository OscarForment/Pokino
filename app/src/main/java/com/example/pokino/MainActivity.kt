package com.example.pokino

import android.os.Vibrator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ToggleButton
import android.widget.GridLayout
import android.widget.Toast
import android.app.AlertDialog
import android.util.TypedValue
import android.widget.NumberPicker
import android.widget.Switch
import kotlin.random.Random


class MainActivity : Activity() {

    private lateinit var buttons: Array<Array<ToggleButton>>
    private lateinit var editButton: ToggleButton
    private lateinit var centro: Switch
    private lateinit var pokino: Switch
    private lateinit var linea: Switch
    private lateinit var esquinas: Switch

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        editButton = findViewById(R.id.editButton)
        centro=findViewById(R.id.switchCentro)
        pokino=findViewById(R.id.switchPokino)
        esquinas=findViewById(R.id.switchEsquinas)
        linea=findViewById(R.id.switchLinea)
        buttons = Array(5) { row ->
            Array(5) { col ->
                val button = gridLayout.getChildAt(row * 5 + col) as ToggleButton
                button // Agrega el botón al array

            }
        }
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                buttons[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
                buttons[i][j].textOn = "1"
                buttons[i][j].textOff = "1"
                buttons[i][j].setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,R.mipmap.oros)
                buttons[i][j].setOnClickListener { onCardClick(buttons[i][j]) }

            }
        }

        loadButtonState()
        // Configura el botón de limpieza
        val resetButton: Button = findViewById(R.id.resetButton)
        resetButton.setOnClickListener { resetCards() }
        // Configura el botón de búsqueda
        val searchButton: Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener { searchCards() }
        // Configura el botón de aleatoriedad
        val randomButton: Button = findViewById(R.id.randomButton)
        randomButton.setOnClickListener { confirmationRandomCards() }


    }

    // Guardar el estado de los botones cuando se cierra la aplicación
    private fun saveButtonState() {
        val sharedPref = getPreferences(MODE_PRIVATE)
        val editor = sharedPref.edit()

        for (i in 0 until 5) {
            for (j in 0 until 5) {
                val keyText = "buttonText_$i$j"
                val keyImage = "buttonImage_$i$j"

                val palo = when (buttons[i][j].compoundDrawablesRelative[3]?.constantState) {
                    resources.getDrawable(R.mipmap.oros)?.constantState -> R.mipmap.oros
                    resources.getDrawable(R.mipmap.espadas)?.constantState -> R.mipmap.espadas
                    resources.getDrawable(R.mipmap.bastos)?.constantState -> R.mipmap.bastos
                    resources.getDrawable(R.mipmap.copas)?.constantState -> R.mipmap.copas
                    else -> R.mipmap.oros // Manejar caso por defecto o desconocido
                }

                editor.putString(keyText, buttons[i][j].textOn.toString())
                editor.putInt(keyImage, palo)
            }
        }

        editor.apply()
    }
    override fun onPause() {
        super.onPause()
        saveButtonState()
    }

    // Cargar el estado de los botones cuando se inicia la aplicación
    private fun loadButtonState() {
        val sharedPref = getPreferences(MODE_PRIVATE)

        for (i in 0 until 5) {
            for (j in 0 until 5) {
                val keyText = "buttonText_$i$j"
                val keyImage = "buttonImage_$i$j"

                val savedText = sharedPref.getString(keyText, "1")
                val savedImage = sharedPref.getInt(keyImage, 0)

                buttons[i][j].textOn = savedText
                buttons[i][j].textOff = savedText
                buttons[i][j].text = savedText

                if (savedImage != 0) {
                    buttons[i][j].setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, savedImage)
                }
            }
        }
    }



    private fun onCardClick(button: ToggleButton) {
        if (editButton.isChecked) {
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.dialog_edit, null)

            val numberPicker = dialogView.findViewById<NumberPicker>(R.id.numberPicker)
            numberPicker.minValue = 1
            numberPicker.maxValue = 12

            val stringNumberPicker = dialogView.findViewById<NumberPicker>(R.id.stringNumberPicker)
            val suitOptions = resources.getStringArray(R.array.suit_options)
            stringNumberPicker.minValue = 0
            stringNumberPicker.maxValue = suitOptions.size - 1
            stringNumberPicker.displayedValues = suitOptions

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(dialogView)

            alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Guardar") { _, _ ->
                    val newText = numberPicker.value.toString()
                    val selectedSuit = suitOptions[stringNumberPicker.value]
                    updateButtonText(newText, selectedSuit, button)
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    button.isChecked = !button.isChecked
                    dialog.cancel()
                }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

        } else {
            if (!centro.isChecked) {
                if (buttons[2][2].isChecked) {
                    centro.isChecked = true
                    Toast.makeText(this, "¡CENTRO!", Toast.LENGTH_SHORT).show()
                    vibratePhone()
                }
            }
            if (!esquinas.isChecked) {
                if (buttons[0][0].isChecked && buttons[4][4].isChecked
                    && buttons[4][0].isChecked && buttons[0][4].isChecked) {
                    esquinas.isChecked = true
                    Toast.makeText(this, "¡ESQUINAS!", Toast.LENGTH_SHORT).show()
                    vibratePhone()
                }
            }
            if (!pokino.isChecked) {
                if (buttons[0][0].isChecked && buttons[0][1].isChecked
                    && buttons[0][2].isChecked && buttons[0][3].isChecked) {
                    pokino.isChecked = true
                    Toast.makeText(this, "¡POKINO!", Toast.LENGTH_SHORT).show()
                    vibratePhone()
                }
            }
            if (!linea.isChecked) {
                if (checkHorizontalLine() || checkVerticalLine()) {
                    linea.isChecked = true
                    Toast.makeText(this, "¡LÍNEA!", Toast.LENGTH_SHORT).show()
                    vibratePhone()
                }
            }
        }
    }
   // Función para buscar cartas
    private fun searchCards() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_edit, null)

        val numberPicker = dialogView.findViewById<NumberPicker>(R.id.numberPicker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 12

        val stringNumberPicker = dialogView.findViewById<NumberPicker>(R.id.stringNumberPicker)
        val suitOptions = resources.getStringArray(R.array.suit_options)
        stringNumberPicker.minValue = 0
        stringNumberPicker.maxValue = suitOptions.size - 1
        stringNumberPicker.displayedValues = suitOptions

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(dialogView)

        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Buscar") { _, _ ->
                val selectedNumber = numberPicker.value.toString()
                val selectedSuit = suitOptions[stringNumberPicker.value]
                searchCard(selectedNumber, selectedSuit)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    // Función para reiniciar los botones
    private fun resetCards() {

        Toast.makeText(this, "Reiniciando", Toast.LENGTH_SHORT).show()
        // Restaura todos los botones a su estado inicial
        editButton.setChecked(false)
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                buttons[i][j].setChecked(false)
                //buttonState[i][j] = false
            }
        }
        centro.isChecked=false
        esquinas.isChecked=false
        pokino.isChecked=false
        linea.isChecked=false
    }
    // Función para confirmar la aleatoriedad de las cartas
    private fun confirmationRandomCards() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar acción")
        builder.setMessage("¿Estás seguro de que deseas cambiar tu cartón?")

        builder.setPositiveButton("Sí") { _, _ ->
            // Si el usuario hace clic en Sí, llama a la función randomCards()
            randomCards()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss() // Cierra el diálogo sin hacer nada
        }

        val dialog = builder.create()
        dialog.show()
    }
    // Función para generar cartas aleatorias
    private fun randomCards() {
        val (numpalo, numero) = generateNumbers()
        val palos = intArrayOf(R.mipmap.oros, R.mipmap.espadas, R.mipmap.bastos, R.mipmap.copas)
        palos.shuffle()
        for (j in 0 until 4) {

            buttons[0][j].textOn = numero
            buttons[0][j].textOff = numero
            buttons[0][j].setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,palos[j])
            // Forzar la actualización del texto
            buttons[0][j].text = numero

        }
        var numpaloesq:Int
        var numeroesq:String
        do{
            val newPair = generateNumbers()
            numpaloesq = newPair.first
            numeroesq = newPair.second

        }while(numeroesq==numero)

        val palo = when (numpaloesq) {
            1 -> R.mipmap.oros
            2 -> R.mipmap.espadas
            3 -> R.mipmap.bastos
            4 -> R.mipmap.copas
            else -> R.mipmap.oros // Manejar caso por defecto o desconocido
        }
        buttons[0][4].textOn = numeroesq
        buttons[0][4].textOff = numeroesq
        buttons[0][4].setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,palo)
        // Forzar la actualización del texto
        buttons[0][4].text = numeroesq
        for (i in 1 until 5) {
            for (j in 0 until 5) {

                generateCard(i,j)

            }

        }

    }
    // Función auxiliar para generar cartas aleatorias y que no se repitan
    private fun generateCard(i:Int,j:Int){
        val (numpalo, numero) = generateNumbers()
        val palo = when (numpalo) {
            1 -> R.mipmap.oros
            2 -> R.mipmap.espadas
            3 -> R.mipmap.bastos
            4 -> R.mipmap.copas
            else -> R.mipmap.oros // Manejar caso por defecto o desconocido
        }
        var repe = false
        for (ii in 0 until i) {
            for (jj in 0 until 5) {
                val currentDrawable = buttons[ii][jj].compoundDrawablesRelative[3]
                if (buttons[ii][jj].textOff == numero &&
                    currentDrawable?.constantState == resources.getDrawable(palo)?.constantState
                ) {
                    repe=true
                }
            }
        }
        for (jj in 0 until j) {
            val currentDrawable = buttons[i][jj].compoundDrawablesRelative[3]
            if (buttons[i][jj].textOff == numero &&
                currentDrawable?.constantState == resources.getDrawable(palo)?.constantState
            ) {
                repe=true
            }
        }
        if(repe){
            generateCard(i,j)
        }else{
            buttons[i][j].textOn = numero
            buttons[i][j].textOff = numero
            buttons[i][j].setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,palo)
            // Forzar la actualización del texto
            buttons[i][j].text = numero
        }
    }
    // Función para generar el palo y el numero de las cartas
    private fun generateNumbers(): Pair<Int,String>{
        val numeroEntre1y4 = Random.nextInt(1, 5)
        var numeroEntre1y12: Int
        do {
            numeroEntre1y12 = (1..12).random()
        } while (numeroEntre1y12 == 8 || numeroEntre1y12 == 9)
        return Pair(numeroEntre1y4, numeroEntre1y12.toString())
    }
    // Función para buscar cartas
    private fun searchCard(newText: String, selectedSuit: String) {
        val buttonTextWithSuit = when (selectedSuit) {
            "oros" -> R.mipmap.oros
            "espadas" -> R.mipmap.espadas
            "bastos" -> R.mipmap.bastos
            "copas" -> R.mipmap.copas
            else -> R.mipmap.oros // Manejar caso por defecto o desconocido
        }

        for (i in 0 until 5) {
            for (j in 0 until 5) {
                if (buttons[i][j].textOff == newText) {
                    // Obtener la imagen actual en drawableEnd
                    val currentDrawable = buttons[i][j].compoundDrawablesRelative[3]

                    // Comprobar si la imagen actual del botón es igual a la imagen específica
                    if (currentDrawable?.constantState == resources.getDrawable(buttonTextWithSuit)?.constantState) {
                        buttons[i][j].isChecked = true
                        onCardClick(buttons[i][j])
                    }
                }
            }
        }
    }
    // Función para actualizar el número y el palo de las cartas
    private fun updateButtonText(newText: String,selected: String, button:ToggleButton) {
        val buttonTextWithSuit = when (selected) {
            "oros" -> R.mipmap.oros
            "espadas" -> R.mipmap.espadas
            "bastos" -> R.mipmap.bastos
            "copas" -> R.mipmap.copas
            else -> R.mipmap.oros // Manejar caso por defecto o desconocido
        }
        button.textOn = newText
        button.textOff = newText
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,buttonTextWithSuit)
        // Forzar la actualización del texto
        button.text = newText
        button.isChecked=!button.isChecked
    }
    private fun checkHorizontalLine(): Boolean {
        for (i in 0 until 5) {
            if (buttons[i][0].isChecked && buttons[i][1].isChecked &&
                buttons[i][2].isChecked && buttons[i][3].isChecked &&
                buttons[i][4].isChecked
            ) {
                return true
            }
        }
        return false
    }

    private fun checkVerticalLine(): Boolean {
        for (j in 0 until 5) {
            if (buttons[0][j].isChecked && buttons[1][j].isChecked &&
                buttons[2][j].isChecked && buttons[3][j].isChecked &&
                buttons[4][j].isChecked
            ) {
                return true
            }
        }
        return false
    }
    private fun vibratePhone() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator?
        if (vibrator?.hasVibrator() == true) {
            // Patrón de vibración (en milisegundos)
            val pattern = longArrayOf(0, 200, 100, 200)
            vibrator.vibrate(400)
        }
    }

}


