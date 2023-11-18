package com.maximum0.board.controller.dto

data class PostSummaryResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdBy: String,
    val createdAt: String
)
