package com.sghore.chimtubeworld.core.network.di

import com.sghore.chimtubeworld.core.network.datasource.CafeRemoteDataSource
import com.sghore.chimtubeworld.core.network.datasource.CafeRemoteDataSourceImpl
import com.sghore.chimtubeworld.core.network.datasource.ChzzkRemoteDataSource
import com.sghore.chimtubeworld.core.network.datasource.ChzzkRemoteDataSourceImpl
import com.sghore.chimtubeworld.core.network.datasource.YoutubeRemoteDataSource
import com.sghore.chimtubeworld.core.network.datasource.YoutubeRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteDataSourceModule {
    @Binds
    abstract fun bindYoutubeRemoteDataSource(impl: YoutubeRemoteDataSourceImpl): YoutubeRemoteDataSource

    @Binds
    abstract fun bindCafeRemoteDataSource(impl: CafeRemoteDataSourceImpl): CafeRemoteDataSource

    @Binds
    abstract fun bindChzzkRemoteDataSource(impl: ChzzkRemoteDataSourceImpl): ChzzkRemoteDataSource
}
