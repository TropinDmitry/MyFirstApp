package com.tsu.myfirstapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MeaningDao{
    @Insert
    fun insert(meaning: MeaningEntityDB)

    @Query("SELECT * FROM MeaningEntityDB WHERE word LIKE :word")
    fun getWordInfo(word: String): List<MeaningEntityDB>
}