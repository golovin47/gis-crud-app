package com.gis.giscrudapp.application

import android.app.Application
import com.gis.featureusers.di.usersModule
import com.gis.navigation.di.navigationModule
import com.gis.repoimpl.di.repoModule
import com.gis.utils.di.utilsModule
import org.koin.android.ext.android.startKoin

class GISCrudApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(usersModule, navigationModule, repoModule, utilsModule))
    }
}