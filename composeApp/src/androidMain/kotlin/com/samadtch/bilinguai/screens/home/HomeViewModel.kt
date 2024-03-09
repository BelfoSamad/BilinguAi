package com.samadtch.bilinguai.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samadtch.bilinguai.data.repositories.base.ConfigRepository
import com.samadtch.bilinguai.data.repositories.base.DataRepository
import com.samadtch.bilinguai.data.repositories.base.UserRepository
import com.samadtch.bilinguai.ui.screens.DataUiState
import com.samadtch.bilinguai.ui.screens.GenerationUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.samadtch.bilinguai.utilities.exceptions.APIException
import com.samadtch.bilinguai.utilities.exceptions.AuthException
import com.samadtch.bilinguai.utilities.exceptions.DataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val dataRepository: DataRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /***********************************************************************************************
     * ************************* Declarations
     */
    private var initializeCalled = false
    private var sortByDate = true
    private val _uiState = MutableStateFlow(DataUiState())
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()
    private val _generationState = MutableStateFlow<GenerationUiState?>(null)
    val generationState = _generationState.asStateFlow()
    private val _deletedState = MutableStateFlow<Int?>(null)
    val deletedState: StateFlow<Int?> = _deletedState.asStateFlow()
    private val _deleteAccountState = MutableStateFlow<Int?>(null)
    val deleteAccountState: StateFlow<Int?> = _deleteAccountState.asStateFlow()
    private val _outState = MutableSharedFlow<Unit>()
    val outState: SharedFlow<Unit> = _outState.asSharedFlow()

    /***********************************************************************************************
     * ************************* Methods
     */
    fun initialize() {
        if (initializeCalled) return
        initializeCalled = true

        viewModelScope.launch {
            val data = dataRepository.getData()
            val exception = data.exceptionOrNull()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorCode = when (exception) {
                        is AuthException -> "AUTH::" + exception.code
                        is DataException -> "DATA::" + exception.code
                        else -> null
                    },
                    email = userRepository.getEmail().getOrNull(),
                    //Initially sorted by date
                    data = data.getOrNull()?.sortedBy { data -> data.createdAt }
                )
            }
        }
    }

    fun getLinks() = configRepository.getAppDetails()

    fun sortData(byDate: Boolean) {
        sortByDate = byDate
        _uiState.update { state ->
            state.copy(
                data = if (byDate) state.data?.sortedBy { it.createdAt }
                else state.data?.sortedBy { it.topic }
            )
        }
    }

    fun generateData(inputs: Map<String, Any>, temperature: Float) {
        viewModelScope.launch {
            //Start Loading
            _generationState.emit(GenerationUiState())

            //Generate Data
            if (configRepository.generationsRemaining() ||
                (configRepository.getCooldown()?.minus(Clock.System.now().epochSeconds) ?: -1) < 0
            ) {
                val response = dataRepository.generateData(inputs, temperature)
                val exception = response.exceptionOrNull()
                _generationState.update {
                    GenerationUiState(
                        isLoading = false,
                        errorCode = when (exception) {
                            is APIException -> "API::" + exception.code
                            is AuthException -> "AUTH::" + exception.code
                            is DataException -> "DATA::" + exception.code
                            else -> null
                        }
                    )
                }

                //If Successful, subtract credit and change data
                if (response.isSuccess) {
                    //Add Data to UiState
                    _uiState.update { state ->
                        val newData = state.data?.toMutableList() ?: mutableListOf()
                        newData.add(response.getOrNull()!!)
                        state.copy(
                            errorCode = null,
                            //List Re-Sorted
                            data = if (sortByDate) newData.sortedBy { it.createdAt }
                            else newData.sortedBy { it.topic },
                        )
                    }
                    configRepository.dropRemaining()
                    configRepository.setCooldown(Clock.System.now().epochSeconds + configRepository.getBaseCooldown())
                }
            } else _generationState.update {
                it?.copy(
                    isLoading = false,
                    errorCode = "COOLDOWN::${configRepository.getCooldown()?.minus(Clock.System.now().epochSeconds)}"
                )
            }
        }
    }

    fun deleteData(dataId: String) {
        viewModelScope.launch {
            _deletedState.emit(-99)//Loading State
            try {
                dataRepository.deleteData(dataId)
                //Delete Data from UiState
                _uiState.update {
                    val newData = it.data!!.filter { data -> data.dataId != dataId }
                    it.copy(
                        errorCode = if (newData.isEmpty()) "DATA::94" else null,
                        data = newData
                    )
                }
                _deletedState.emit(null)
            } catch (e: AuthException) {
                _deletedState.emit(AuthException.AUTH_ERROR_USER_LOGGED_OUT)
            } catch (e: DataException) {
                _deletedState.emit(e.code)
            }
        }
    }

    fun logout() = viewModelScope.launch {
        userRepository.logout()
        _outState.emit(Unit)
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _deleteAccountState.emit(-99)//Loading State
            try {
                userRepository.deleteAccount(password)
                _deleteAccountState.emit(-1)//Loading State
                _outState.emit(Unit)//Logged Out
            } catch (e: AuthException) {
                _deleteAccountState.emit(e.code)//AUTH_ERROR_USER_NOT_FOUND and AUTH_ERROR_USER_WRONG_CREDENTIALS
            } catch (e: AuthException) {
                _deleteAccountState.emit(e.code)//DATA_ERROR_OTHER
            }
        }
    }

}