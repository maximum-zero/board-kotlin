package com.maximum0.board.repository

import com.maximum0.board.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {
    fun findByPostId(postId: Long): List<Tag>
}
