package com.sghore.chimtubeworld.core.data.di

import com.sghore.chimtubeworld.core.data.repository.BookmarkRepositoryImpl
import com.sghore.chimtubeworld.core.data.repository.CafeRepositoryImpl
import com.sghore.chimtubeworld.core.data.repository.TwitchRepositoryImpl
import com.sghore.chimtubeworld.core.data.repository.YoutubeRepositoryImpl
import com.sghore.chimtubeworld.core.domain.repository.BookmarkRepository
import com.sghore.chimtubeworld.core.domain.repository.CafeRepository
import com.sghore.chimtubeworld.core.domain.repository.TwitchRepository
import com.sghore.chimtubeworld.core.domain.repository.YoutubeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds
    abstract fun bindCafeRepository(impl: CafeRepositoryImpl): CafeRepository

    @Binds
    abstract fun bindTwitchRepository(impl: TwitchRepositoryImpl): TwitchRepository

    @Binds
    abstract fun bindYoutubeRepository(impl: YoutubeRepositoryImpl): YoutubeRepository
}
