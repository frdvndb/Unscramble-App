package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragmen tempat game dimainkan, berisi logika game.
 */
class GameFragment : Fragment() {

    // Mengikat instance objek dengan akses ke tampilan di tata letak game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    // Buat ViewModel saat pertama kali fragmen dibuat.
    // Jika fragmen dibuat ulang, fragmen tersebut menerima
    // instance GameViewModel yang sama yang dibuat oleh fragmen pertama.
    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate file XML tata letak dan mengembalikan instance objek binding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setel viewModel untuk pengikatan data
        // Ini memungkinkan akses tata letak terikat ke semua data di ViewModel
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        // Tentukan tampilan fragmen sebagai pemilik siklus hidup pengikatan
        // Ini digunakan agar pengikatan dapat mengamati pembaruan LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        // Setel pendengar klik untuk tombol Skip dan Submit
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }

    /*
     * Memeriksa kata pengguna, dan memperbarui skor yang sesuai.
     * Menampilkan kata acak berikutnya.
     * Setelah kata terakhir, pengguna diperlihatkan tampilan skor akhir.
     */
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    /*
     * Melewati kata saat ini tanpa mengubah skor.
     */
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    /*
     * Membuat dan menampilkan AlertDialog dengan skor akhir.
     */
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }


    /*
     * Menginisialisasi ulang data di ViewModel dan
     * memperbarui tampilan dengan data baru untuk memulai ulang game.
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /*
     * Keluar dari permainan.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
     * Sets dan resets status kesalahan teks pada kolom isi.
     */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }
}