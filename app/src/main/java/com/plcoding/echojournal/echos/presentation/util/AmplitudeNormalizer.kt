package com.plcoding.echojournal.echos.presentation.util

import kotlin.math.roundToInt

object AmplitudeNormalizer {

    private const val AMPLITUDE_MIN_OUTPUT_THRESHOLD = 0.1f
    private const val MIN_OUTPUT = 0.25f
    private const val MAX_OUTPUT = 1f

    fun normalize(
        sourceAmplitudes: List<Float>,
        trackWidth: Float,
        barWidth: Float,
        spacing: Float
    ): List<Float> {
        require(trackWidth >= 0f) {
            "Track width must be positive"
        }
        require(trackWidth >= barWidth + spacing) {
            "Track width must be at least the size of one bar and spacing"
        }
        if(sourceAmplitudes.isEmpty()) {
            return emptyList()
        }

        val barsCount = (trackWidth / (barWidth + spacing)).roundToInt()
        val resampled = resampleAmplitudes(sourceAmplitudes, barsCount)
        val remapped = remapAmplitudes(resampled)

        return remapped
    }

    private fun remapAmplitudes(amplitudes: List<Float>): List<Float> {
        val outputRange = MAX_OUTPUT - MIN_OUTPUT
        val scaleFactor = MAX_OUTPUT - AMPLITUDE_MIN_OUTPUT_THRESHOLD
        return amplitudes.map { amplitude ->
            if(amplitude <= AMPLITUDE_MIN_OUTPUT_THRESHOLD) {
                MIN_OUTPUT
            } else {
                val amplitudeRange = amplitude - AMPLITUDE_MIN_OUTPUT_THRESHOLD

                MIN_OUTPUT + (amplitudeRange * outputRange / scaleFactor)
            }
        }
    }

    private fun resampleAmplitudes(
        sourceAmplitudes: List<Float>,
        targetSize: Int
    ): List<Float> {
        return when {
            targetSize == sourceAmplitudes.size -> sourceAmplitudes
            targetSize < sourceAmplitudes.size -> downSample(sourceAmplitudes, targetSize)
            else -> upSample(sourceAmplitudes, targetSize)
        }
    }

    // [0.5, 0.7, 0.3, 0.4, 0.3, 0.8] -> [0.7, 0.4, 0.8]
    private fun downSample(
        source: List<Float>,
        targetSize: Int
    ): List<Float> {
        val ratio = source.size.toFloat() / targetSize
        return List(targetSize) { index ->
            val start = (index * ratio).toInt()
            val end = ((index + 1) * ratio).toInt().coerceAtMost(source.size)

            source.subList(start, end).max()
        }
    }

    //Scenario one - Average
    // [0, 0.2, 0.3] -> [0,   x, 0.2,    x, 0.3]
    // [0, 0.2, 0.3] -> [0, 0.1, 0.2, 0.25, 0.3]
    //Scenario two - Linear Interpolation
    // [0, 0.1, 0] -> [0,     x,    x,     x, 0.1,     x,    x,     x, 0]
    // [0, 0.1, 0] -> [0, 0.025, 0.05, 0.075, 0.1, 0.075, 0.05, 0.025, 0]
    private fun upSample(
        source: List<Float>,
        targetSize: Int
    ): List<Float> {
        val result = mutableListOf<Float>()

        val step = (source.size - 1).toFloat() / (targetSize - 1)
        for(i in 0 until targetSize) {
            //how far we moved along to source list
            val pos = i * step
            //Which existing element lies exactly to the left of this position?
            val index = pos.toInt()

            //How far we are past that item as a percentage of the gap to the next one
            val fraction = pos - index

            val value = if(index + 1 < source.size) {
                (1 - fraction) * source[index] + fraction * source[index + 1]
            } else {
                source[index]
            }
            result.add(value)
        }

        return result.toList()
    }
}