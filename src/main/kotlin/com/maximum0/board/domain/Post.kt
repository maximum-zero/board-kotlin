package com.maximum0.board.domain

import com.maximum0.board.exception.PostNotUpdatableException
import com.maximum0.board.service.dto.PostUpdatedRequestDto
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Post(
    title: String,
    content: String,
    createdBy: String
) : BaseEntity(createdBy) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    var title: String = title
        protected set

    var content: String = content
        protected set

    fun update(request: PostUpdatedRequestDto) {
        if (request.updatedBy != this.createdBy) {
            throw PostNotUpdatableException()
        }

        this.title = request.title
        this.content = request.content
        super.updatedBy = request.updatedBy
    }
}
