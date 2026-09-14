package com.wpsa.tester

import android.app.Application
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WpsApplication : Application() {
    
    init {
        // Set libsu global configuration
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )
    }

    override fun onCreate() {
        super.onCreate()
    }
}
