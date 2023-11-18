package com.maximum0.board.controller

import com.maximum0.board.controller.dto.PostCreatedRequest
import com.maximum0.board.controller.dto.PostDetailResponse
import com.maximum0.board.controller.dto.PostSearchRequest
import com.maximum0.board.controller.dto.PostSummaryResponse
import com.maximum0.board.controller.dto.PostUpdatedRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class PostController {

    @GetMapping("/posts")
    fun getPosts(
        pageable: Pageable,
        postSearchRequest: PostSearchRequest
    ): Page<PostSummaryResponse> {
        return Page.empty()
    }

    @GetMapping("/posts/{id}")
    fun getPost(
        @PathVariable id: Long
    ): PostDetailResponse {
        return PostDetailResponse(1, "title", "content", "createdBy", LocalDateTime.now().toString())
    }

    @PostMapping("/posts")
    fun createPost(
        @RequestBody postCreatedRequest: PostCreatedRequest
    ): Long {
        return 1L
    }

    @PutMapping("/posts/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @RequestBody postUpdatedRequest: PostUpdatedRequest
    ): Long {
        return 1L
    }

    @DeleteMapping("/posts/{id}")
    fun deletePost(
        @PathVariable id: Long,
        @RequestParam createdBy: String
    ): Long {
        return 1L
    }
}
