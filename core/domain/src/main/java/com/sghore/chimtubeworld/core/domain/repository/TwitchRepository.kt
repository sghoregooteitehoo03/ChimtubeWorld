package com.sghore.chimtubeworld.core.domain.repository

import com.sghore.chimtubeworld.core.model.Channel

interface TwitchRepository {
    suspend fun getChannels(): List<Channel>
}
