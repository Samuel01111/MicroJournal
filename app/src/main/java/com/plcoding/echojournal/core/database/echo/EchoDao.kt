package com.plcoding.echojournal.core.database.echo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.plcoding.echojournal.core.database.echo_topic.EchoTopicCrossRef
import com.plcoding.echojournal.core.database.echo_topic.EchoWithTopics
import com.plcoding.echojournal.core.database.topic.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EchoDao {

    @Query("SELECT * FROM EchoEntity ORDER BY recordedAt DESC")
    fun observeEchos(): Flow<List<EchoWithTopics>>

    @Query("SELECT * FROM TopicEntity ORDER BY topic DESC")
    fun observeTopics(): Flow<List<TopicEntity>>

    @Query("""
        SELECT * FROM TopicEntity
        WHERE topic LIKE "%" || :query || "%"
        ORDER BY topic ASC
    """)
    fun searchTopics(query: String): Flow<List<TopicEntity>>

    @Insert
    suspend fun insertEcho(echoEntity: EchoEntity): Long

    @Upsert
    suspend fun upsertTopic(topicEntity: TopicEntity)

    @Insert
    suspend fun insertEchoTopicCrossRef(crossRef: EchoTopicCrossRef)

    @Transaction
    suspend fun insertEchoWithTopics(echoWithTopics: EchoWithTopics) {
        val echoId = insertEcho(echoWithTopics.echo)

        echoWithTopics.topics.forEach { topic ->
            upsertTopic(topic)
            insertEchoTopicCrossRef(
                crossRef = EchoTopicCrossRef(
                    echoId = echoId.toInt(),
                    topic = topic.topic
                )
            )
        }
    }
}