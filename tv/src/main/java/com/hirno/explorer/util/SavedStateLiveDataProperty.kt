package com.hirno.explorer.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SavedStateLiveDataProperty<T>(
    private val savedStateHandle: SavedStateHandle
) : ReadWriteProperty<ViewModel, MutableLiveData<T>> {
    override fun getValue(thisRef: ViewModel, property: KProperty<*>): MutableLiveData<T> {
        return savedStateHandle.getLiveData(property.name)
    }

    override operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: MutableLiveData<T>) {
        savedStateHandle[property.name] = value.value
    }
}

fun <T> SavedStateHandle.liveData() = SavedStateLiveDataProperty<T>(this)