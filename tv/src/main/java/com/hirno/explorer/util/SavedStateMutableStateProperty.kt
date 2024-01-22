package com.hirno.explorer.util

import androidx.compose.runtime.MutableState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

//class SavedStateMutableStateProperty<T>(
//    private val savedStateHandle: SavedStateHandle
//) : ReadWriteProperty<ViewModel, MutableState<T>> {
//    override fun getValue(thisRef: ViewModel, property: KProperty<*>): MutableState<T> {
//        return savedStateHandle.getLiveData(property.name)
//    }
//
//    override operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: MutableState<T>) {
//        savedStateHandle[property.name] = value.value
//    }
//}
//
//fun <T> SavedStateHandle.state() = SavedStateMutableStateProperty<T>(this)