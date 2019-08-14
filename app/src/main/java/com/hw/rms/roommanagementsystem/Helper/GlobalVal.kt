package com.hw.rms.roommanagementsystem.Helper

import com.hw.rms.roommanagementsystem.Model.DummyModel
import com.hw.rms.roommanagementsystem.Model.Json4Kotlin_Base

class GlobalVal {
    companion object{
        const val FRESH_INSTALL_KEY = "FRESH_INSTALL_KEY"
        var configModel : Json4Kotlin_Base? = null
    }
}