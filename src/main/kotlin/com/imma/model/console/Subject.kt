package com.imma.model.console

import com.imma.model.CollectionNames
import com.imma.model.Tuple
import com.imma.model.compute.ParameterDelegate
import com.imma.model.compute.ParameterJointDelegate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.ZoneOffset
import java.util.*

data class SubjectDataSetColumn(
    var columnId: String = "",
    var parameter: ParameterDelegate = mutableMapOf(),
    var alias: String = ""
)

enum class TopicJoinType(val type: String) {
    left("left"),
    right("right"),
    `inner`("inner");
}

data class SubjectDataSetJoin(
    var topicId: String = "",
    var factorId: String = "",
    var secondaryTopicId: String = "",
    var secondaryFactorId: String = "",
    var type: TopicJoinType = TopicJoinType.`inner`,
)

data class SubjectDataSet(
    var filters: ParameterJointDelegate = mutableMapOf(),
    var columns: List<SubjectDataSetColumn> = mutableListOf(),
    var joins: List<SubjectDataSetJoin> = mutableListOf()
)

@Document(collection = CollectionNames.SUBJECT)
data class Subject(
    @Id
    var subjectId: String? = null,
    @Field("name")
    var name: String? = null,
    @Field("connect_id")
    var connectId: String? = null,
    @Field("user_id")
    var userId: String? = null,
    @Field("dataset")
    var dataset: SubjectDataSet = SubjectDataSet(),
    @Field("last_visit_time")
    var lastVisitTime: String? = null,
    @Field("create_time")
    override var createTime: String? = null,
    @Field("last_modify_time")
    override var lastModifyTime: String? = null,
    @LastModifiedDate
    @Field("last_modified")
    override var lastModified: Date = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC)).time
) : Tuple() {
    @Transient
    var reports: MutableList<Report> = mutableListOf()
}