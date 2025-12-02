package com.plcoding.echojournal.core.database.echo

import androidx.room.TypeConverter
import com.plcoding.echojournal.echos.presentation.models.MoodUi

class MoodUiTypeConverter {

    @TypeConverter
    fun fromMood(moodUi: MoodUi): String {
        return moodUi.name
    }

    @TypeConverter
    fun toMood(name: String): MoodUi {
        return MoodUi.valueOf(name)
    }
}