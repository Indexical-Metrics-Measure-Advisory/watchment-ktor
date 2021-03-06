package com.imma.service.console

import com.imma.model.CollectionNames
import com.imma.model.console.Subject
import com.imma.model.determineFakeOrNullId
import com.imma.service.TupleService
import com.imma.utils.getCurrentDateTime
import com.imma.utils.getCurrentDateTimeAsString
import io.ktor.application.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import kotlin.contracts.ExperimentalContracts

class SubjectService(application: Application) : TupleService(application) {
    @ExperimentalContracts
    fun saveSubject(subject: Subject) {
        val fake = determineFakeOrNullId({ subject.subjectId },
            true,
            { subject.subjectId = nextSnowflakeId().toString() })

        if (fake) {
            createTuple(subject)
        } else {
            updateTuple(subject)
        }

        ReportService(application).saveReports(subject.reports.onEach {
            it.connectId = subject.connectId
            it.subjectId = subject.subjectId
            it.userId = subject.userId
        })
    }

    fun findSubjectById(subjectId: String): Subject? {
        return findFromMongo {
            it.findById(subjectId, Subject::class.java, CollectionNames.SUBJECT)
        }
    }

    fun renameSubject(subjectId: String, name: String? = "") {
        writeIntoMongo {
            it.updateFirst(
                Query.query(Criteria.where("subjectId").`is`(subjectId)),
                Update().apply {
                    set("name", name)
                    set("lastModifyTime", getCurrentDateTimeAsString())
                    set("lastModified", getCurrentDateTime())
                },
                Subject::class.java,
                CollectionNames.SUBJECT
            )
        }
    }

    fun deleteSubject(subjectId: String) {
        writeIntoMongo {
            // delete reports
            ReportService(application).deleteReportsBySubject(subjectId)
            // delete subject
            it.remove(
                Query.query(Criteria.where("subjectId").`is`(subjectId)),
                Subject::class.java,
                CollectionNames.SUBJECT
            )
        }
    }

    @ExperimentalContracts
    fun saveSubjects(subjects: List<Subject>) {
        subjects.forEach { saveSubject(it) }
    }

    fun listSubjectsByConnectedSpaces(connectedSpaceIds: List<String>): List<Subject> {
        val query: Query = Query.query(Criteria.where("connectId").`in`(connectedSpaceIds))
        val subjects = findListFromMongo(Subject::class.java, CollectionNames.SUBJECT, query)

        val reports = ReportService(application).listReportsByConnectedSpaces(connectedSpaceIds)

        // assemble reports to subjects
        val subjectMap = subjects.map { it.subjectId to it }.toMap()
        reports.forEach { report ->
            val subjectId = report.subjectId
            val subject = subjectMap[subjectId]!!
            subject.reports.add(report)
        }

        return subjects
    }

    fun deleteSubjectsByConnectedSpace(connectId: String) {
        writeIntoMongo {
            it.remove(
                Query.query(Criteria.where("connectId").`is`(connectId)),
                Subject::class.java,
                CollectionNames.SUBJECT
            )
        }
    }

    fun isSubjectBelongsTo(subjectId: String, userId: String): Boolean {
        return getFromMongo {
            it.exists(
                Query.query(Criteria.where("subjectId").`is`(subjectId).and("userId").`is`(userId)),
                Subject::class.java,
                CollectionNames.SUBJECT
            )
        }
    }
}