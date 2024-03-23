package com.samadtch.bilinguai.ui.screens.home

import com.samadtch.bilinguai.data.repositories.base.ConfigRepository
import com.samadtch.bilinguai.data.repositories.base.DataRepository
import com.samadtch.bilinguai.data.repositories.base.UserRepository
import com.samadtch.bilinguai.utilities.exceptions.APIException
import com.samadtch.bilinguai.utilities.exceptions.AuthException
import com.samadtch.bilinguai.utilities.exceptions.DataException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class HomeViewModel(
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
                    isVerified = userRepository.checkEmailVerified().getOrNull(),
                    //Initially sorted by date
                    data = data.getOrNull()?.sortedByDescending { data -> data.createdAt }
                )
            }
        }
    }

    fun sortData(byDate: Boolean) {
        sortByDate = byDate
        _uiState.update { state ->
            state.copy(
                data = if (byDate) state.data?.sortedByDescending { it.createdAt }
                else state.data?.sortedBy { it.topic }
            )
        }
    }

    fun generateData(inputs: Map<String, Any>, temperature: Float) {
        viewModelScope.launch {
            //Start Loading
            _generationState.emit(GenerationUiState())

            //Generate Data
            try {
                val gState = configRepository.getGenerationState().getOrThrow()
                if (userRepository.checkEmailVerified()
                        .getOrNull() == false
                ) _generationState.update {
                    it?.copy(isLoading = false, errorCode = "VERIFICATION")
                } else if (
                    gState.remaining > 0 || (gState.cooldown?.minus(Clock.System.now().epochSeconds)
                        ?: -1) < 0
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
                            },
                            success = response.isSuccess
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
                                data = if (sortByDate) newData.sortedByDescending { it.createdAt }
                                else newData.sortedBy { it.topic },
                            )
                        }
                        configRepository.handleGenerationState(gState.remaining)
                    }
                } else _generationState.update {
                    it?.copy(
                        isLoading = false,
                        errorCode = "COOLDOWN::${gState.cooldown?.minus(Clock.System.now().epochSeconds)}",
                        success = false
                    )
                }
            } catch (e: Exception) {
                _generationState.update {
                    it?.copy(
                        isLoading = false,
                        errorCode = if (e is AuthException) "AUTH::" + e.code else "DATA::" + (e as DataException).code,
                        success = false
                    )
                }
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
                        errorCode = if (newData.isEmpty()) "DATA::32" else null,
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

    fun verifyEmail() = viewModelScope.launch { userRepository.verifyEmail() }

}