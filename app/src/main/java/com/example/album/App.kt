package com.example.album

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(), ImageLoaderFactory {

  override fun newImageLoader(): ImageLoader {
    return ImageLoader.Builder(this)
      .memoryCachePolicy(CachePolicy.ENABLED)
      .memoryCache {
        MemoryCache.Builder(this)
          .maxSizePercent(0.20)
          .build()
      }
      .diskCachePolicy(CachePolicy.ENABLED)
      .diskCache {
        DiskCache.Builder()
          .directory(cacheDir.resolve("image_cache"))
          .maxSizeBytes(5 * 1024 * 1024)
          .build()
      }
      .logger(DebugLogger())
      .respectCacheHeaders(false)
      .build()
  }
}
