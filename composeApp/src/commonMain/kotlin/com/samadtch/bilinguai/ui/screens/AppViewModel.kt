package com.samadtch.bilinguai.ui.screens

import com.samadtch.bilinguai.AppUiState
import com.samadtch.bilinguai.data.repositories.base.ConfigRepository
import com.samadtch.bilinguai.data.repositories.base.UserRepository
import com.samadtch.bilinguai.utilities.exceptions.AuthException
import com.samadtch.bilinguai.utilities.exceptions.DataException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class AppViewModel(
    private val configRepository: ConfigRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val _initUiState = MutableStateFlow(AppUiState())
    val initUiState: StateFlow<AppUiState> = _initUiState.asStateFlow()
    private val _deleteAccountState = MutableStateFlow<Int?>(null)
    val deleteAccountState: StateFlow<Int?> = _deleteAccountState.asStateFlow()
    private val _outState = MutableStateFlow<Unit?>(null)
    val outState: StateFlow<Unit?> = _outState.asStateFlow()

    /***********************************************************************************************
     * ************************* Init
     */
    init {
        viewModelScope.launch {
            _initUiState.emit(
                AppUiState(
                    email = userRepository.getEmail().getOrNull(),
                    links = configRepository.getAppDetails()
                )
            )
        }
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    fun logout() = viewModelScope.launch {
        userRepository.logout()
        _outState.emit(Unit)
    }

    fun resetOutState() = viewModelScope.launch {
        _outState.emit(null)
    }

    fun refetchEmail() {
        viewModelScope.launch {
            _initUiState.update {
                it.copy(email = userRepository.getEmail().getOrNull())
            }
        }
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _deleteAccountState.emit(-99)//Loading State
            try {
                userRepository.deleteAccount(password)
                _deleteAccountState.emit(-1)//End State
                _outState.emit(Unit)//Logged Out
            } catch (e: AuthException) {
                _deleteAccountState.emit(e.code)//AUTH_ERROR_USER_NOT_FOUND and AUTH_ERROR_USER_WRONG_CREDENTIALS
            } catch (e: DataException) {
                _deleteAccountState.emit(e.code)//DATA_ERROR_OTHER
            }
        }
    }

}