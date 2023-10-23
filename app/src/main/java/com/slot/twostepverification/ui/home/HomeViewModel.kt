package com.slot.twostepverification.ui.home

import com.slot.twostepverification.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
data class TwoUiState(
    var updateTime:Long = 0
)
class HomeViewModel: BaseViewModel() {
    private val _uiState = MutableStateFlow(TwoUiState())

    val uiState: StateFlow<TwoUiState> = _uiState.asStateFlow()
    fun onRestore() {}

}