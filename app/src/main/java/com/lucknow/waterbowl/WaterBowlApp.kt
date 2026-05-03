package com.lucknow.waterbowl

import android.app.Application
import com.lucknow.waterbowl.data.auth.AuthManager

class WaterBowlApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthManager.init(this)
    }
}
