package com.likeminds.feedsx.utils.file

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.os.Environment.getExternalStorageDirectory
import android.text.TextUtils
import com.likeminds.feedsx.utils.file.Constants.SDCard.SDCARD_PATHS
import java.io.File
import java.io.File.pathSeparator
import java.io.File.separator
import java.lang.System.getenv

object SDCardUtils {

    private val envExternalStorage = getenv("EXTERNAL_STORAGE").orEmpty()
    private val envSecondaryStorage = getenv("SECONDARY_STORAGE").orEmpty()
    private val envEmulatedStorageTarget = getenv("EMULATED_STORAGE_TARGET").orEmpty()

    @Suppress("DEPRECATION")
    private val emulatedStorage: String
        get() {
            var rawStorageId = ""
            val path = getExternalStorageDirectory().absolutePath
            val folders = path.split(separator)
            val lastSegment = folders.last()
            if (lastSegment.isNotBlank() && TextUtils.isDigitsOnly(lastSegment)) {
                rawStorageId = lastSegment
            }
            return if (rawStorageId.isBlank()) {
                envEmulatedStorageTarget
            } else {
                envEmulatedStorageTarget + separator + rawStorageId
            }
        }

    private val secondaryStorage: List<String>
        get() = if (envSecondaryStorage.isNotBlank()) {
            envSecondaryStorage.split(pathSeparator)
        } else {
            listOf()
        }

    private val availableSDCardsPaths: List<String>
        get() {
            val availableSDCardsPaths = mutableListOf<String>()
            SDCARD_PATHS.forEach { path ->
                val file = File(path)
                if (file.exists()) {
                    availableSDCardsPaths.add(path)
                }
            }
            return availableSDCardsPaths
        }

    fun getStorageDirectories(context: Context): Array<String> {
        val availableDirectories = HashSet<String>()
        if (envEmulatedStorageTarget.isNotBlank()) {
            availableDirectories.add(emulatedStorage)
        } else {
            availableDirectories.addAll(getExternalStorage(context))
        }
        availableDirectories.addAll(secondaryStorage)
        return availableDirectories.toTypedArray()
    }

    private fun getExternalStorage(context: Context): Set<String> {
        val availableDirectories = HashSet<String>()
        if (SDK_INT >= M) {
            val files = getExternalFilesDirs(context)
            files?.forEach { file ->
                val applicationSpecificAbsolutePath = file.absolutePath
                var rootPath = applicationSpecificAbsolutePath
                    .substring(9, applicationSpecificAbsolutePath.indexOf("Android/data"))
                rootPath = rootPath.substring(rootPath.indexOf("/storage/") + 1)
                rootPath = rootPath.substring(0, rootPath.indexOf("/"))
                if (rootPath != "emulated") {
                    availableDirectories.add(rootPath)
                }
            }
        } else {
            if (envExternalStorage.isBlank()) {
                availableDirectories.addAll(availableSDCardsPaths)
            } else {
                availableDirectories.add(envExternalStorage)
            }
        }
        return availableDirectories
    }

    private fun getExternalFilesDirs(context: Context): Array<File>? {
        return context.getExternalFilesDirs(null)
    }
}