package com.samadtch.bilinguai.screens.boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samadtch.bilinguai.data.repositories.base.ConfigRepository
import com.samadtch.bilinguai.data.repositories.base.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardingViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /***********************************************************************************************
     * ************************* Declarations
     */
    val uiState = flow {
        emit(
            BoardingUiState(
                isFirstTime = configRepository.isFirstTime(),
                isLoggedIn = userRepository.isLoggedIn()
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )


    /***********************************************************************************************
     * ************************* Methods
     */
    fun setFirstTime() {
        viewModelScope.launch { configRepository.setFirstTime() }
    }

    /***********************************************************************************************
     * ************************* UI States
     */
    data class BoardingUiState(
        val isFirstTime: Boolean,
        val isLoggedIn: Boolean
    )
}