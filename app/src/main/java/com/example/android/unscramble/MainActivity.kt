package com.example.android.unscramble

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Aktivitas yang menjadi hosting untuk fragment permainan.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }
}