package com.tsu.myfirstapp

import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApi {
    @GET("{word}")
    suspend fun getWordMeaning(@Path("word") word: String): ArrayList<Word>
}