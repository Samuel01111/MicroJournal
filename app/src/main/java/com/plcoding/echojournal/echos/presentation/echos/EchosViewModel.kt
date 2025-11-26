package com.plcoding.echojournal.echos.presentation.echos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.echojournal.R
import com.plcoding.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.plcoding.echojournal.core.presentation.util.UiText
import com.plcoding.echojournal.echos.presentation.echos.models.EchoFilterChip
import com.plcoding.echojournal.echos.presentation.echos.models.MoodChipContent
import com.plcoding.echojournal.echos.presentation.models.MoodUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class EchosViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())

    private val _state = MutableStateFlow(EchosState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeFilters()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EchosState()
        )

    fun onAction(action: EchosAction) {
        when (action) {
            EchosAction.OnFabClick -> {}
            EchosAction.OnFabLongClick -> {}
            EchosAction.OnSettingsClick ->  {}
            is EchosAction.OnRemoteFilters -> {
                when(action.filterType) {
                    EchoFilterChip.MOODS -> selectedMoodFilters.update { emptyList() }
                    EchoFilterChip.TOPICS -> selectedTopicFilters.update { emptyList() }
                }
            }
            EchosAction.OnTopicChipClick -> {
                _state.update { it.copy(
                    selectedEchoFilterChip = EchoFilterChip.TOPICS
                ) }
            }
            EchosAction.OnMoodChipClick -> {
                _state.update { it.copy(
                    selectedEchoFilterChip = EchoFilterChip.MOODS
                ) }
            }
            EchosAction.OnDismissTopicDropDown,
            EchosAction.OnDismissMoodDropDown -> {
                _state.update { it.copy(
                    selectedEchoFilterChip = null
                ) }
            }
            is EchosAction.OnFilterByMoodClick -> {
                toggleMoodFilter(action.moodUi)
            }
            is EchosAction.OnFilterByTopicClick -> {
                toggleTopicFilter(action.topic)
            }
        }
    }

    private fun toggleMoodFilter(moodUi: MoodUi) {
        selectedMoodFilters.update { selectedMoods ->
            if(moodUi in selectedMoods) {
                selectedMoods - moodUi
            } else {
                selectedMoods + moodUi
            }
        }
    }

    private fun toggleTopicFilter(topic: String) {
        selectedTopicFilters.update { selectedTopic ->
            if(topic in selectedTopic) {
                selectedTopic - topic
            } else {
                selectedTopic + topic
            }
        }
    }

    private fun observeFilters() {
        combine(
            selectedTopicFilters,
            selectedMoodFilters
        ) { selectedTopics, selectedMoods ->
            _state.update { it.copy(
                topics = it.topics.map { selectableTopic ->
                    Selectable(
                        item = selectableTopic.item,
                        selected = selectedTopics.contains(selectableTopic.item)
                    )
                },
                moods = MoodUi.entries.map { mood ->
                    Selectable(
                        item = mood,
                        selected = selectedMoods.contains(mood)
                    )
                },
                hasActiveMoodFilters = selectedMoods.isNotEmpty(),
                hasActiveTopicFilters = selectedTopics.isNotEmpty(),
                topicChipTitle = selectedTopics.deriveTopicsToText(),
                moodChipContent = selectedMoods.asMoodChipContent()
            ) }
        }.launchIn(viewModelScope)
    }

    private fun List<String>.deriveTopicsToText(): UiText {
        return when(size) {
            0 -> UiText.StringResource(R.string.all_topics)
            1 -> UiText.Dynamic(this.first())
            2 -> UiText.Dynamic("${this.first()}, ${this.last()}")
            else -> {
                val extraElementCount = size - 2
                UiText.Dynamic("${this.first()}, ${this[1]} +$extraElementCount")
            }
        }
    }

    private fun List<MoodUi>.asMoodChipContent(): MoodChipContent {
        if(this.isEmpty()) {
            return MoodChipContent()
        }

        val icons = this.map { it.iconSet.fill }
        val moodNames = this.map { it.title }

        return when(this.size) {
            1 -> MoodChipContent(
                iconsRes = icons,
                title = moodNames.first()
            )
            2 -> MoodChipContent(
                iconsRes = icons,
                title = UiText.Combined(
                    format = "%s, %s",
                    uiTexts = moodNames.toTypedArray()
                )
            )
            else -> {
                val extraElementCount = size - 2
                MoodChipContent(
                    iconsRes = icons,
                    title = UiText.Combined(
                        format = "%s, %s +$extraElementCount",
                        uiTexts = moodNames.take(2).toTypedArray()
                    )
                )
            }
        }
    }
}
