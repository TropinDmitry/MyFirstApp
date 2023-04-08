package com.tsu.myfirstapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WordDao {
    @Insert
    fun insert (word: WordEntityDB)

    @Query("SELECT * FROM WordEntityDB")
    fun getAllMeanings(): List<WordEntityDB>

    @Query("SELECT * FROM WordEntityDB WHERE word LIKE :word")
    fun findWord(word: String): WordEntityDB

    @Delete
    fun delete(word: WordEntityDB)
}