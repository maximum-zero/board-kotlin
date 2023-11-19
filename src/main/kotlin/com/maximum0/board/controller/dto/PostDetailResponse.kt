package com.maximum0.board.controller.dto

import com.maximum0.board.service.dto.PostDetailResponseDto

data class PostDetailResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdBy: String,
    val createdAt: String
)

fun PostDetailResponseDto.toResponse() = PostDetailResponse(
    id = id,
    title = title,
    content = content,
    createdBy = createdBy,
    createdAt = createdAt
)
