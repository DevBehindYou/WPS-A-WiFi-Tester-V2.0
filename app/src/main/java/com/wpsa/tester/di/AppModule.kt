package com.wpsa.tester.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.wpsa.tester.wps.AndroidVendorSupplicantAdapter
import com.wpsa.tester.wps.SupplicantAdapter

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindSupplicantAdapter(
        adapter: AndroidVendorSupplicantAdapter
    ): SupplicantAdapter
}
