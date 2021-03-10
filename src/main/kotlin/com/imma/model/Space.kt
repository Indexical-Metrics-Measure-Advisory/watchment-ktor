package com.imma.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.ZoneOffset
import java.util.*

@Document(collection = CollectionNames.SPACE)
data class Space(
    @Id
    var spaceId: String? = null,
    @Indexed(unique = true)
    @Field("name")
    var name: String? = null,
    @Field("description")
    var description: String? = null,
    @Field("topic_ids")
    var topicIds: List<String>? = mutableListOf(),
    @Field("create_time")
    override var createTime: String? = null,
    @Field("last_modify_time")
    override var lastModifyTime: String? = null,
    @LastModifiedDate
    @Field("last_modified")
    override var lastModified: Date = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC)).time
) : Tuple() {
    @Transient
    var groupIds: List<String>? = mutableListOf()
}