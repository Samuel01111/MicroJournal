package com.plcoding.echojournal.core.presentation.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource

//Android context independent
@Stable // Tells Jetpack composable may have a mutable state change
sealed interface UIText {
    data class Dynamic(val value:String): UIText

    @Stable
    data class StringResource(
        @param:StringRes val id: Int,
        val args: Array<Any> = arrayOf(),
    ): UIText {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StringResource

            if (id != other.id) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    @Composable
    fun asString(): String {
        return when(this) {
            is Dynamic -> value
            is StringResource -> stringResource(id, *args)
        }
    }
}