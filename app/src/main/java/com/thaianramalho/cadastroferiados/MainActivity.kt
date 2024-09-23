package com.thaianramalho.cadastroferiados

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.LocalDate.*
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    private val feriados = mutableListOf<Feriado>()
    private lateinit var editTextNome: EditText
    private lateinit var editTextData: EditText
    private lateinit var editTextEstado: EditText
    private lateinit var editTextMunicipio: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var buttonCadastrar: Button
    private lateinit var buttonExcluir: Button
    private lateinit var listViewFeriados: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var selectedFeriadoIndex: Int? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextNome = findViewById(R.id.editTextNome)
        editTextData = findViewById(R.id.editTextData)
        editTextEstado = findViewById(R.id.editTextEstado)
        editTextMunicipio = findViewById(R.id.editTextMunicipio)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        buttonCadastrar = findViewById(R.id.buttonCadastrar)
        buttonExcluir = findViewById(R.id.buttonExcluir)
        listViewFeriados = findViewById(R.id.listViewFeriados)

        val tipos = TipoFeriado.values().map { it.name }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        spinnerTipo.adapter = spinnerAdapter

        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                atualizarCamposVisibilidade()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        buttonCadastrar.setOnClickListener {
            if (selectedFeriadoIndex != null) {
                editarFeriado()
            } else {
                cadastrarFeriado()
            }
        }

        buttonExcluir.setOnClickListener {
            excluirFeriado()
        }

        editTextData.setOnClickListener {
            showDatePicker()
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listViewFeriados.adapter = adapter

        listViewFeriados.setOnItemClickListener { _, _, position, _ ->
            selecionarFeriado(position)
        }
    }

    private fun atualizarCamposVisibilidade() {
        val tipoSelecionado = TipoFeriado.valueOf(spinnerTipo.selectedItem.toString())
        editTextEstado.visibility =
            if (tipoSelecionado == TipoFeriado.ESTADUAL) View.VISIBLE else View.GONE
        editTextMunicipio.visibility =
            if (tipoSelecionado == TipoFeriado.MUNICIPAL) View.VISIBLE else View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = of(selectedYear, selectedMonth + 1, selectedDay)
                editTextData.setText(selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            }, year, month, day)

        datePickerDialog.datePicker.minDate =
            System.currentTimeMillis()
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cadastrarFeriado() {
        val nome = editTextNome.text.toString()
        val dataStr = editTextData.text.toString()
        val tipo = TipoFeriado.valueOf(spinnerTipo.selectedItem.toString())
        val estado = editTextEstado.text.toString()
        val municipio = editTextMunicipio.text.toString()

        if (nome.isBlank() || dataStr.isBlank() || (tipo == TipoFeriado.ESTADUAL && estado.isBlank()) || (tipo == TipoFeriado.MUNICIPAL && municipio.isBlank())) {
            Toast.makeText(this, "Preencha todos os campos necessários", Toast.LENGTH_SHORT).show()
            return
        }

        val data: LocalDate
        try {
            data = parse(dataStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            Toast.makeText(this, "Data inválida", Toast.LENGTH_SHORT).show()
            return
        }

        if (!validarData(data)) {
            Toast.makeText(this, "Data não pode ser no passado", Toast.LENGTH_SHORT).show()
            return
        }

        val feriado = Feriado(
            nome,
            data,
            tipo,
            estado.takeIf { tipo == TipoFeriado.ESTADUAL },
            municipio.takeIf { tipo == TipoFeriado.MUNICIPAL })
        feriados.add(feriado)

            if(tipo == TipoFeriado.ESTADUAL){
                adapter.add("${nome} - ${data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${tipo} - ${estado}")
            }else if(tipo == TipoFeriado.MUNICIPAL){
                adapter.add("${nome} - ${data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${tipo}- ${municipio}")
            }else{
                adapter.add("${nome} - ${data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${tipo}")
            }

        clearFields()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun editarFeriado() {
        val nome = editTextNome.text.toString()
        val dataStr = editTextData.text.toString()
        val tipo = TipoFeriado.valueOf(spinnerTipo.selectedItem.toString())
        val estado = editTextEstado.text.toString()
        val municipio = editTextMunicipio.text.toString()

        if (nome.isBlank() || dataStr.isBlank() || (tipo == TipoFeriado.ESTADUAL && estado.isBlank()) || (tipo == TipoFeriado.MUNICIPAL && municipio.isBlank())) {
            Toast.makeText(this, "Preencha todos os campos necessários", Toast.LENGTH_SHORT).show()
            return
        }

        val data: LocalDate
        try {
            data = parse(dataStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            Toast.makeText(this, "Data inválida", Toast.LENGTH_SHORT).show()
            return
        }

        if (!validarData(data)) {
            Toast.makeText(this, "Data não pode ser no passado", Toast.LENGTH_SHORT).show()
            return
        }

        val index = selectedFeriadoIndex ?: return
        feriados[index] = Feriado(
            nome,
            data,
            tipo,
            estado.takeIf { tipo == TipoFeriado.ESTADUAL },
            municipio.takeIf { tipo == TipoFeriado.MUNICIPAL })

        adapter.clear()
        feriados.forEach {
            if(it.tipo == TipoFeriado.ESTADUAL){
                adapter.add("${it.nome} - ${it.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${it.tipo} - ${it.estado}")
            }else if(it.tipo == TipoFeriado.MUNICIPAL){
                adapter.add("${it.nome} - ${it.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${it.tipo}- ${it.municipio}")
            }else{
                adapter.add("${it.nome} - ${it.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${it.tipo}")
            }
        }


        clearFields()
        selectedFeriadoIndex = null
        buttonCadastrar.text = "Cadastrar"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun excluirFeriado() {
        val index = selectedFeriadoIndex ?: return
        feriados.removeAt(index)
        adapter.clear()
        feriados.forEach {
            if(it.tipo == TipoFeriado.ESTADUAL){
                adapter.add("${it.nome} - ${it.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${it.tipo} - ${it.estado}")
            }else if(it.tipo == TipoFeriado.MUNICIPAL){
                adapter.add("${it.nome} - ${it.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${it.tipo}- ${it.municipio}")
            }else{
                adapter.add("${it.nome} - ${it.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} - ${it.tipo}")
            }
        }
        clearFields()
        selectedFeriadoIndex = null
        buttonCadastrar.text = "Cadastrar"
        Toast.makeText(this, "Feriado excluído", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selecionarFeriado(position: Int) {
        val feriado = feriados[position]
        editTextNome.setText(feriado.nome)
        editTextData.setText(feriado.data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        spinnerTipo.setSelection(TipoFeriado.values().indexOf(feriado.tipo))
        editTextEstado.setText(feriado.estado)
        editTextMunicipio.setText(feriado.municipio)
        selectedFeriadoIndex = position
        buttonCadastrar.text = "Editar"
        atualizarCamposVisibilidade()
    }

    private fun clearFields() {
        editTextNome.text.clear()
        editTextData.text.clear()
        editTextEstado.text.clear()
        editTextMunicipio.text.clear()
        spinnerTipo.setSelection(0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validarData(data: LocalDate): Boolean {
        return data.isAfter(now())
    }
}