package com.dev.cuckooxa.notesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dev.cuckooxa.notesapp.databinding.ActivityMainBinding
import com.dev.cuckooxa.notesapp.db.DbManager
import com.dev.cuckooxa.notesapp.fragments.MainFragment

class MainActivity : AppCompatActivity() {

    private val dbManager = DbManager(this)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbManager.openDb()
        supportFragmentManager.beginTransaction()
            .replace(binding.placeHolder.id, MainFragment.newInstance(dbManager))
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }
}