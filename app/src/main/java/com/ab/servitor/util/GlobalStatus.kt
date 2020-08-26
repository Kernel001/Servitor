package com.ab.servitor.util

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

object GlobalStatus {
    val netConnection = ObservableBoolean(false)
    val http1CConnectio = ObservableBoolean(false)
    val lastError = ObservableField<String>("Не инициализирован")
    val currentOper = ObservableField<String>("НЕТ ОПЕР")
}