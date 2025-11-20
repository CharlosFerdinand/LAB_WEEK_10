package com.example.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.viewmodels.TotalViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var db: TotalDatabase

    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ Prepare database AFTER context is available
        db = prepareDatabase()

        // 2️⃣ Load DB value into ViewModel BEFORE UI
        initializeValueFromDatabase()

        // 3️⃣ Set UI
        setContentView(R.layout.activity_main)

        // 4️⃣ ViewModel + UI
        prepareViewModel()
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) {
            updateText(it)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java,
            "total-database"
        ).allowMainThreadQueries().build()
    }

    private fun initializeValueFromDatabase() {
        val total = db.totalDao().getTotal(ID)

        if (total.isEmpty()) {
            db.totalDao().insert(Total(ID, 0))
            viewModel.setTotal(0)
        } else {
            viewModel.setTotal(total.first().total)
        }
    }

    override fun onPause() {
        super.onPause()
        // Save ViewModel value into DB
        db.totalDao().update(Total(ID, viewModel.total.value ?: 0))
    }

    companion object {
        const val ID: Long = 1
    }
}
