package org.example.project

interface Subject<T> {
    fun addObserver(observer: T)
    fun removeObserver(observer: T)
    fun notifyObservers()
}