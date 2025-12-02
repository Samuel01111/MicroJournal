package com.plcoding.echojournal.core.database.echo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.plcoding.echojournal.echos.presentation.models.MoodUi

@Entity
data class EchoEntity(
    @PrimaryKey(autoGenerate = true)
    val echoId: Int = 0,
    val title: String,
    val mood: MoodUi,
    val recordedAt: Long,
    val note: String?,
    val audioFilePath: String,
    val audioPlaybackLength: Long,
    val audioAmplitudes: List<Float>,
)