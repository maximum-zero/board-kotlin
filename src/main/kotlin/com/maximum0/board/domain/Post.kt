package com.maximum0.board.domain

import com.maximum0.board.exception.PostNotUpdatableException
import com.maximum0.board.service.dto.PostUpdatedRequestDto
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Post(
    title: String,
    content: String,
    createdBy: String,
    tags: List<String> = emptyList()
) : BaseEntity(createdBy) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    var title: String = title
        protected set

    var content: String = content
        protected set

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = [CascadeType.ALL])
    var comments: MutableList<Comment> = mutableListOf()
        protected set

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = [CascadeType.ALL])
    var tags: MutableList<Tag> = tags.map { Tag(it, this, createdBy) }.toMutableList()
        protected set

    fun update(request: PostUpdatedRequestDto) {
        if (request.updatedBy != this.createdBy) {
            throw PostNotUpdatableException()
        }

        this.title = request.title
        this.content = request.content
        replaceTag(request.tags)
        super.updatedBy = request.updatedBy
    }

    private fun replaceTag(tags: List<String>) {
        if (this.tags.map { it.name } != tags) {
            this.tags.clear()
            this.tags.addAll(tags.map { Tag(it, this, this.createdBy) })
        }
    }
}
