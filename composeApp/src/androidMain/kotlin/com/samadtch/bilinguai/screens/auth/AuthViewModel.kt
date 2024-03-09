package com.samadtch.bilinguai.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samadtch.bilinguai.data.repositories.base.UserRepository
import com.samadtch.bilinguai.ui.screens.LoginUiState
import com.samadtch.bilinguai.ui.screens.RegisterUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.samadtch.bilinguai.utilities.exceptions.AuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val _registerState= MutableStateFlow<RegisterUiState?>(null)
    val registerState: StateFlow<RegisterUiState?> = _registerState.asStateFlow()
    private val _loginState= MutableStateFlow<LoginUiState?>(null)
    val loginState: StateFlow<LoginUiState?> = _loginState.asStateFlow()
    private val _passwordResetState= MutableStateFlow<Int?>(null)
    val passwordResetState: StateFlow<Int?> = _passwordResetState.asStateFlow()

    /***********************************************************************************************
     * ************************* Methods
     */
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registerState.update { RegisterUiState() }//Loading
            val registerState = userRepository.register(email, password)
            _registerState.update {
                it?.copy(
                    isLoading = false,
                    errorCode = (registerState.exceptionOrNull() as AuthException?)?.code,
                    userId = registerState.getOrNull()
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.update { LoginUiState() }//Loading
            val loginState = userRepository.login(email, password)
            _loginState.update {
                it?.copy(
                    isLoading = false,
                    errorCode = (loginState.exceptionOrNull() as AuthException?)?.code,
                    userId = loginState.getOrNull()
                )
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _passwordResetState.emit(-99)//Loading State
            try {
                userRepository.sendPasswordResetEmail(email)
                _passwordResetState.emit(-1)
            } catch (e: AuthException) {
                _passwordResetState.emit(e.code)
            }
        }
    }


}