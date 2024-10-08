package cn.yurn.yutori.application.model

import cn.yurn.yutori.Channel
import cn.yurn.yutori.Guild
import cn.yurn.yutori.User
import cn.yurn.yutori.application.ChannelSerializer
import cn.yurn.yutori.application.GuildSerializer
import cn.yurn.yutori.application.UserSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Identify(
    val platform: String,
    val selfId: String
)

@Serializable
data class Conversation(
    @Serializable(ChannelSerializer::class)
    val channel: Channel,
    val type: String,
    @Serializable(GuildSerializer::class)
    val guild: Guild? = null,
    @Serializable(UserSerializer::class)
    val user: User? = null,
    val avatar: String? = null,
    val name: String,
    val content: String,
    val updatedAt: Long = 0,
    val mute: Boolean = false,
    val unread: Boolean = false
)

@Serializable
data class ConnectSetting(
    val host: String,
    val port: Int,
    val path: String,
    val token: String
)