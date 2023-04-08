package com.tsu.myfirstapp.ui.dictionary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.tsu.myfirstapp.*
import com.tsu.myfirstapp.databinding.FragmentDictionaryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DictionaryFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private fun makeDialog(message: String){
        val dialogBuilder = AlertDialog.Builder(this@DictionaryFragment.requireActivity())
        dialogBuilder.setTitle("Alert")
        dialogBuilder.setMessage(message)
        dialogBuilder.setNeutralButton("Ok")
        { _: DialogInterface, _: Int -> }
        dialogBuilder.show()
    }

    private fun makeRecyclerViewElement(meaning: String, example: String) : SpannableString {
        val exampleText = "Example: "

        if (example != "") {
            val lenWordInfo: Int = meaning.count() + 2
            val totalWordInfo = meaning + "\n\n" + exampleText + example
            val coloredExample = SpannableString(totalWordInfo)
            val fcsBlue = ForegroundColorSpan(Color.parseColor("#65AAEA"))

            coloredExample.setSpan(
                fcsBlue,
                lenWordInfo,
                lenWordInfo + 9,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            return coloredExample
        }

        else return SpannableString(meaning)
    }

    private val errorMessage = "Error during getting data from a server was occurred! \n\n" +
            "Please, check your word is correct or your internet connection is able."

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        val root: View = binding.root;

        var word = ""
        var transcription = ""
        var audio = ""
        var partOfSp = ""
        val list = mutableListOf<SpannableString>()
        val listOfMeanings = mutableListOf<String>()
        val listOfExamples = mutableListOf<String>()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/en/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DictionaryApi::class.java)
        val database = Room.databaseBuilder(requireActivity().applicationContext, WordDB::class.java, "words_db")
            .fallbackToDestructiveMigration().build()

        binding.recycleView.layoutManager = LinearLayoutManager(this@DictionaryFragment.requireActivity())
        binding.recycleView.adapter = MeaningsListAdapter(list)

        binding.soundBtn.setOnClickListener{
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(audio)

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }
                catch (_: Throwable){
                    withContext(Dispatchers.Main) {
                        binding.soundBtn.visibility = View.INVISIBLE
                        makeDialog(errorMessage)
                    }
                }

            }
        }

        binding.addToButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val w: WordEntityDB? = database.wordDao().findWord(word)

                if(w != null){
                    withContext(Dispatchers.Main) {
                        makeDialog("Word exists in your dictionary already.")
                    }
                }
                else if(transcription == "" && partOfSp == ""){
                    withContext(Dispatchers.Main){
                        makeDialog("Cannot and the word to dictionary. \n\n" +
                            "Please check your typed word is correct")
                    }
                }
                else {
                    database.wordDao().insert(WordEntityDB(transcription, partOfSp, word))
                    var i = 0
                    listOfMeanings.forEach{
                        database.meaningDao().insert(MeaningEntityDB(word, it, listOfExamples[i]))
                        i++
                    }
                }
            }
        }

        binding.searchBtn.setOnClickListener{
            word = binding.wordEdit.text.toString()
            transcription = ""
            partOfSp = ""
            listOfExamples.clear()
            listOfMeanings.clear()

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val result = service.getWordMeaning(word)
                    list.clear()
                    audio = ""

                    result[0].phonetics.forEach{
                        if(audio != "" && transcription != "")
                            return@forEach
                        if(it.audio != "")
                            audio = it.audio

                        if(it.text != ""){
                            transcription = it.text ?: transcription
                            if(transcription != "")
                                transcription = transcription.substring(1, transcription.length - 1)
                        }
                    }
                    result[0].meanings[0].definitions.forEach {
                        val meaning = it.definition
                        listOfMeanings.add(meaning)

                        val example = it.example ?: ""
                        listOfExamples.add(example)

                        list.add(makeRecyclerViewElement(meaning, example))
                    }

                    word = word.replaceFirstChar {
                        it.uppercase()
                    }

                    partOfSp = result[0].meanings[0].partOfSpeech.replaceFirstChar {
                        it.uppercase()
                    }

                    withContext(Dispatchers.Main) {
                        binding.wordTextView.text = word
                        binding.partOfTextView2.text = partOfSp
                        if (transcription != "")
                            binding.textTranscr.text = "[$transcription]"
                        else binding.textTranscr.text =  transcription
                        binding.recycleView.adapter = MeaningsListAdapter(list)

                        if (audio == "") binding.soundBtn.visibility = View.INVISIBLE
                        else binding.soundBtn.visibility = View.VISIBLE
                    }
                }
                catch (_ : Throwable) {
                    val w: WordEntityDB? = database.wordDao().findWord(word)
                    if (w != null) {
                        list.clear()
                        val m = database.meaningDao().getWordInfo(word)
                        m.forEach {
                            list.add(makeRecyclerViewElement(it.Meaning, it.Example))
                        }

                        withContext(Dispatchers.Main) {
                            binding.wordTextView.text = w.Word
                            binding.partOfTextView2.text = w.PartOfSpeech
                            if (w.Transcription != "")
                                binding.textTranscr.text = "[" + w.Transcription + "]"
                            else binding.textTranscr.text =  w.Transcription
                            binding.soundBtn.visibility = View.INVISIBLE
                            binding.recycleView.adapter = MeaningsListAdapter(list)
                        }
                    }
                    else {
                        withContext(Dispatchers.Main) {
                            makeDialog(errorMessage)
                        }
                    }
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}