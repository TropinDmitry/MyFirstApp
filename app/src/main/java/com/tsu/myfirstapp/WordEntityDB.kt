package com.tsu.myfirstapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WordEntityDB(val Transcription: String, val PartOfSpeech: String,
    @PrimaryKey val Word: String)