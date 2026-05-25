package com.example.btarduino

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var optionsList: RecyclerView
    private lateinit var optionsAdapter: OptionsAdapter
    private lateinit var store: OptionsStore
    private lateinit var options: MutableList<Option>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME) ?: "-"
        findViewById<TextView>(R.id.connectedTo).text =
            getString(R.string.connected_to, deviceName)

        store = OptionsStore(this)
        options = store.load().toMutableList()
        if (options.isEmpty()) {
            // Opciones por defecto la primera vez
            options.add(Option("Musica 1", "a"))
            options.add(Option("Musica 2", "b"))
            options.add(Option("Musica 3", "c"))
        }

        optionsAdapter = OptionsAdapter(
            items = options,
            onSend = { sendCharacter(it) },
            onEdit = { index -> showEditDialog(index) },
            onDelete = { index -> deleteOption(index) }
        )

        optionsList = findViewById(R.id.optionsList)
        optionsList.layoutManager = LinearLayoutManager(this)
        optionsList.adapter = optionsAdapter

        findViewById<Button>(R.id.addBtn).setOnClickListener {
            options.add(Option(getString(R.string.new_option), "x"))
            optionsAdapter.notifyItemInserted(options.size - 1)
            persist()
        }

        findViewById<Button>(R.id.disconnectBtn).setOnClickListener {
            BluetoothHelper.disconnect()
            finish()
        }
    }

    private fun sendCharacter(option: Option) {
        if (option.character.isEmpty()) {
            Toast.makeText(this, R.string.empty_char, Toast.LENGTH_SHORT).show()
            return
        }
        try {
            BluetoothHelper.send(option.character)
            Toast.makeText(
                this,
                getString(R.string.sent, option.character),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.error_send, e.message ?: "?"),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showEditDialog(index: Int) {
        if (index !in options.indices) return
        val current = options[index]
        val view = layoutInflater.inflate(R.layout.dialog_edit_option, null)
        val titleInput = view.findViewById<EditText>(R.id.titleInput)
        val charInput = view.findViewById<EditText>(R.id.charInput)
        titleInput.setText(current.title)
        charInput.setText(current.character)

        AlertDialog.Builder(this)
            .setTitle(R.string.edit_option)
            .setView(view)
            .setPositiveButton(R.string.save) { _, _ ->
                val newTitle = titleInput.text.toString().trim()
                    .ifEmpty { current.title }
                val newChar = charInput.text.toString()
                current.title = newTitle
                current.character = newChar
                optionsAdapter.notifyItemChanged(index)
                persist()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteOption(index: Int) {
        if (index !in options.indices) return
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_option)
            .setMessage(R.string.delete_confirm)
            .setPositiveButton(R.string.delete) { _, _ ->
                options.removeAt(index)
                optionsAdapter.notifyItemRemoved(index)
                optionsAdapter.notifyItemRangeChanged(index, options.size - index)
                persist()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun persist() {
        store.save(options)
    }

    override fun onStop() {
        persist()
        super.onStop()
    }

    companion object {
        const val EXTRA_DEVICE_NAME = "extra_device_name"
    }
}
