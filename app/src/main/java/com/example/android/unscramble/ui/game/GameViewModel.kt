package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 * ViewModel berisi data aplikasi
 * dan metode untuk memproses data
 */
class GameViewModel : ViewModel() {

    // Variabel dengan nilai data
    // yang tersimpan di dalamnya dapat diubah
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    // Penggunakan Spannable untuk TalkBack
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    // Daftar kata yang digunakan dalam permainan
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    // Inisialisasi untuk mendapatkan kata acak
    init {
        getNextWord()
    }

    /*
     * Memperbarui currentWord dan
     * currentScrambledWord dengan kata berikutnya.
     */
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = _currentWordCount.value?.inc()
            wordsList.add(currentWord)
        }
    }

    /*
     * Menginisialisasi ulang data permainan
     * untuk memulai ulang permainan.
     */
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

    /*
     * Meningkatkan skor permainan
     * jika pemain benar menebak kata.
     */
    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    /*
     * Mengembalikan nilai true jika kata benar,
     * lalu meningkatkan skor permainan.
     */
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    /*
     * Mengembalikan nilai benar
     * jika jumlah kata saat ini kurang dari MAX_NO_OF_WORDS.
     * Kemudian, dilakukan pembaruan kata berikutnya.
     */
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}