package com.maximum0.board.service

import com.maximum0.board.domain.Comment
import com.maximum0.board.domain.Post
import com.maximum0.board.exception.PostNotDeletableException
import com.maximum0.board.exception.PostNotFoundException
import com.maximum0.board.exception.PostNotUpdatableException
import com.maximum0.board.repository.CommentRepository
import com.maximum0.board.repository.PostRepository
import com.maximum0.board.service.dto.PostCreatedRequestDto
import com.maximum0.board.service.dto.PostDetailResponseDto
import com.maximum0.board.service.dto.PostSearchRequestDto
import com.maximum0.board.service.dto.PostUpdatedRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
) : BehaviorSpec({
    beforeSpec {
        postRepository.saveAll(
            listOf(
                Post(title = "제목1", content = "내용1", createdBy = "maximum1"),
                Post(title = "제목2", content = "내용2", createdBy = "maximum1"),
                Post(title = "제목3", content = "내용3", createdBy = "maximum1"),
                Post(title = "제목4", content = "내용4", createdBy = "maximum1"),
                Post(title = "제목5", content = "내용5", createdBy = "maximum1"),
                Post(title = "제목6", content = "내용6", createdBy = "maximum0"),
                Post(title = "제목7", content = "내용7", createdBy = "maximum0"),
                Post(title = "제목8", content = "내용8", createdBy = "maximum0"),
                Post(title = "제목9", content = "내용9", createdBy = "maximum0"),
                Post(title = "제목10", content = "내용10", createdBy = "maximum0")
            )
        )
    }

    given("게시글 생성시") {
        When("게시글 인풋이 정상적으로 들어오면") {
            val postId = postService.createPost(
                PostCreatedRequestDto(
                    title = "제목",
                    content = "내용",
                    createdBy = "maximum0"
                )
            )

            then("게시물이 정상적으로 생성됨을 확인한다.") {
                postId shouldBeGreaterThan 0L
                val post = postRepository.findByIdOrNull(postId)
                post shouldNotBe null
                post?.title shouldBe "제목"
                post?.content shouldBe "내용"
                post?.createdBy shouldBe "maximum0"
            }
        }
    }

    given("게시글 수정시") {
        val saved = postRepository.save(Post(title = "제목", content = "내용", createdBy = "maximum0"))

        When("정상 수정시") {
            val updatedId = postService.updatePost(
                saved.id,
                PostUpdatedRequestDto(
                    title = "update 제목",
                    content = "update 내용",
                    updatedBy = "maximum0"
                )
            )

            then("게시글이 정상적으로 수정됨을 확인한다.") {
                saved.id shouldBe updatedId
                val updated: Post? = postRepository.findByIdOrNull(updatedId)
                updated?.title shouldBe "update 제목"
                updated?.content shouldBe "update 내용"
                updated?.updatedBy shouldBe "maximum0"
            }
        }

        When("게시글이 없을 떄") {
            then("게시글을 찾을 수 없다라는 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> {
                    postService.updatePost(
                        9999L,
                        PostUpdatedRequestDto(
                            title = "update 제목",
                            content = "update 내용",
                            updatedBy = "update maximum0"
                        )
                    )
                }
            }
        }

        When("작성자가 동일하지 않으면") {
            then("수정할 수 없는 게시물 입니다라는 예외가 발생한다.") {
                shouldThrow<PostNotUpdatableException> {
                    postService.updatePost(
                        1L,
                        PostUpdatedRequestDto(
                            title = "update 제목",
                            content = "update 내용",
                            updatedBy = "update maximum0"
                        )
                    )
                }
            }
        }
    }

    given("게시글 삭제시") {
        val saved = postRepository.save(Post(title = "제목", content = "내용", createdBy = "maximum0"))
        When("정상 삭제시") {
            val postId = postService.deletePost(saved.id, "maximum0")
            then("게시글이 정상적으로 삭제됨을 확인한다.") {
                postId shouldBe saved.id
                postRepository.findByIdOrNull(postId) shouldBe null
            }
        }

        When("작성자가 동일하지 않으면") {
            val saved2 = postRepository.save(Post(title = "제목", content = "내용", createdBy = "maximum0"))
            then("삭제할 수 없는 게시물 입니다라는 예외가 발생한다.") {
                shouldThrow<PostNotDeletableException> {
                    postService.deletePost(saved2.id, "maximum1")
                }
            }
        }
    }

    given("게시글 상세 조회 시") {
        val saved = postRepository.save(Post(title = "제목", content = "내용", createdBy = "maximum0"))
        When("정상 조회 시") {
            println(saved.id)
            val post: PostDetailResponseDto = postService.getPost(saved.id)
            then("게시글이 정상적으로 조회됨을 확인한다.") {
                post.id shouldNotBe saved.id
                post.title shouldBe "제목"
                post.content shouldBe "내용"
                post.createdBy shouldBe "maximum0"
            }
        }

        When("게시글이 없다면") {
            then("게시글을 찾을 수 없습니다라는 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> {
                    postService.getPost(9999L)
                }
            }
        }

        When("댓글 추가시") {
            commentRepository.save(Comment(content = "댓글 내용1", post = saved, createdBy = "댓글 작성자"))
            commentRepository.save(Comment(content = "댓글 내용2", post = saved, createdBy = "댓글 작성자"))
            commentRepository.save(Comment(content = "댓글 내용3", post = saved, createdBy = "댓글 작성자"))

            val post = postService.getPost(saved.id)
            then("댓글이 함께 조회됨을 확인한다.") {
                post.comments.size shouldBe 3
                post.comments[0].content shouldBe "댓글 내용1"
                post.comments[1].content shouldBe "댓글 내용2"
                post.comments[2].content shouldBe "댓글 내용3"

                post.comments[0].createdBy shouldBe "댓글 작성자"
                post.comments[1].createdBy shouldBe "댓글 작성자"
                post.comments[2].createdBy shouldBe "댓글 작성자"
            }
        }
    }

    given("게시글 목록 조회 시") {
        When("정상 조회 시") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto())
            then("게시글이 정상적으로 조회됨을 확인한다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBeLessThanOrEqual 5
                postPage.content[0].title shouldContain "제목"
                postPage.content[0].createdBy shouldContain "maximum"
            }
        }

        When("타이틀로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(title = "제목1"))
            then("타이틀에 해당하는 게시글이 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBeLessThanOrEqual 5
                postPage.content[0].title shouldContain "제목1"
                postPage.content[0].createdBy shouldContain "maximum"
            }
        }

        When("작성자로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(createdBy = "maximum1"))
            then("타이틀에 해당하는 게시글이 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBeLessThanOrEqual 5
                postPage.content[0].title shouldContain "제목"
                postPage.content[0].createdBy shouldContain "maximum"
            }
        }
    }
})
