package sangiorgi.wps.opensource.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sangiorgi.wps.opensource.wps.AndroidVendorSupplicantAdapter
import sangiorgi.wps.opensource.wps.SupplicantAdapter

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindSupplicantAdapter(
        adapter: AndroidVendorSupplicantAdapter
    ): SupplicantAdapter
}
