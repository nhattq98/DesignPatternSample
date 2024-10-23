package com.tahn.sampledesignpattern.flyweight

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// https://proandroiddev.com/kotlin-design-patterns-flyweight-08aa2be80d80
fun main() {
    val factory = ImageFactory()
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch {
        val image = factory.get("image")
    }
    scope.launch {
        val image = factory.get("image")
    }
}

data class Image(val bytes: ByteArray)

class ImageFactory {
    private val cache = mutableMapOf<String, Image>()
    private val locks = mutableMapOf<String, Mutex>()
    private val lock = Mutex()

    suspend fun get(url: String): Image? {
        val imageMutex = lock.withLock {
            locks.getOrPut(url) { Mutex() }
        }

        val image = imageMutex.withLock {
            getImage(url)
        }
        locks.remove(url)
        return image
    }

    private suspend fun getImage(url: String): Image? =
        cache[url] ?: fetchImage(url).also { image ->
            image?.let {
                cache[url] = it
            }
        }

    fun fetchImage(url: String): Image? {
        return null
    }
}