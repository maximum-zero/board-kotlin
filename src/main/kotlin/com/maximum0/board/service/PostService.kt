package com.maximum0.board.service

import com.maximum0.board.domain.Post
import com.maximum0.board.exception.PostNotDeletableException
import com.maximum0.board.exception.PostNotFoundException
import com.maximum0.board.repository.PostRepository
import com.maximum0.board.service.dto.PostCreatedRequestDto
import com.maximum0.board.service.dto.PostDetailResponseDto
import com.maximum0.board.service.dto.PostSearchRequestDto
import com.maximum0.board.service.dto.PostSummaryResponseDto
import com.maximum0.board.service.dto.PostUpdatedRequestDto
import com.maximum0.board.service.dto.toDetailResponseDto
import com.maximum0.board.service.dto.toEntity
import com.maximum0.board.service.dto.toSummaryResponseDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository
) {
    @Transactional
    fun createPost(request: PostCreatedRequestDto): Long {
        return postRepository.save(request.toEntity()).id
    }

    @Transactional
    fun updatePost(id: Long, request: PostUpdatedRequestDto): Long {
        val post: Post = postRepository.findByIdOrNull(id) ?: throw PostNotFoundException()
        post.update(request)
        return id
    }

    @Transactional
    fun deletePost(id: Long, deletedBy: String): Long {
        val post: Post = postRepository.findByIdOrNull(id) ?: throw PostNotFoundException()
        if (post.createdBy != deletedBy) {
            throw PostNotDeletableException()
        }
        postRepository.delete(post)
        return id
    }

    fun getPost(id: Long): PostDetailResponseDto {
        return postRepository.findByIdOrNull(id)?.toDetailResponseDto() ?: throw PostNotFoundException()
    }

    fun findPageBy(pageRequest: Pageable, postSearchRequestDto: PostSearchRequestDto): Page<PostSummaryResponseDto> {
        return postRepository.findPageBy(pageRequest, postSearchRequestDto).toSummaryResponseDto()
    }
}
