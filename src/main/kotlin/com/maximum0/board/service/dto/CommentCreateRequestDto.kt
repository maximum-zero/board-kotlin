package com.maximum0.board.service.dto

import com.maximum0.board.domain.Comment
import com.maximum0.board.domain.Post

data class CommentCreateRequestDto(
    val content: String,
    val createdBy: String
)

fun CommentCreateRequestDto.toEntity(post: Post) = Comment(
    content = content,
    createdBy = createdBy,
    post = post
)
