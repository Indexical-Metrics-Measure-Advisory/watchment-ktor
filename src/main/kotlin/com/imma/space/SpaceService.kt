package com.imma.space

import com.imma.model.*
import com.imma.rest.DataPage
import com.imma.rest.Pageable
import com.imma.service.Service
import com.imma.user.UserGroupService
import io.ktor.application.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

class SpaceService(application: Application) : Service(application) {
    private fun createSpace(space: Space) {
        forceAssignDateTimePair(space)
        this.writeIntoMongo { it.insert(space) }
    }

    private fun updateSpace(space: Space) {
        assignDateTimePair(space)
        writeIntoMongo { it.save(space) }
    }

    fun saveSpace(space: Space) {
        val fake = determineFakeId({ space.spaceId }, true, { space.spaceId = nextSnowflakeId().toString() })

        if (fake) {
            createSpace(space)
        } else {
            updateSpace(space)
        }

        val userGroupIds = space.groupIds
        val userGroupService = UserGroupService(application)
        userGroupService.unassignSpace(space.spaceId!!)
        if (!userGroupIds.isNullOrEmpty()) {
            userGroupService.assignSpace(userGroupIds, space.spaceId!!)
        }
    }

    fun findSpaceById(spaceId: String): Space? {
        return findFromMongo {
            it.findById(spaceId, Space::class.java, CollectionNames.SPACE)
        }
    }

    fun findSpacesByName(name: String? = "", pageable: Pageable): DataPage<Space> {
        val query: Query
        if (name!!.isEmpty()) {
            query = Query.query(Criteria.where("name").all())
        } else {
            query = Query.query(Criteria.where("name").regex(name, "i"))
        }
        return findPageFromMongo(Space::class.java, CollectionNames.SPACE, query, pageable)
    }

    fun findSpacesByNameForHolder(name: String? = ""): List<SpaceForHolder> {
        val query: Query
        if (name!!.isEmpty()) {
            query = Query.query(Criteria.where("name").all())
        } else {
            query = Query.query(Criteria.where("name").regex(name, "i"))
        }
        query.fields().include("spaceId", "name")
        return findListFromMongo(SpaceForHolder::class.java, CollectionNames.SPACE, query)
    }

    fun findSpacesByIdsForHolder(spaceIds: List<String>): List<SpaceForHolder> {
        val query: Query = Query.query(Criteria.where("spaceId").`in`(spaceIds))
        query.fields().include("spaceId", "name")
        return findListFromMongo(SpaceForHolder::class.java, CollectionNames.SPACE, query)
    }

    fun unassignSpaceGroup(spaceGroupId: String) {
        writeIntoMongo {
            it.updateMulti(
                Query.query(Criteria.where("groupIds").`is`(spaceGroupId)),
                Update().pull("groupIds", spaceGroupId),
                Space::class.java,
                CollectionNames.SPACE
            )
        }
    }

    fun assignSpaceGroup(spaceIds: List<String>, spaceGroupId: String) {
        writeIntoMongo {
            it.updateMulti(
                Query.query(Criteria.where("spaceId").`in`(spaceIds)),
                Update().push("groupIds", spaceGroupId),
                Space::class.java,
                CollectionNames.SPACE
            )
        }
    }
}
