package com.wise.groupware.service;

import com.wise.groupware.domain.Article;
import com.wise.groupware.domain.ArticleComment;
import com.wise.groupware.domain.UserAccount;
import com.wise.groupware.domain.type.SearchType;
import com.wise.groupware.dto.ArticleCommentDto;
import com.wise.groupware.dto.ArticleDto;
import com.wise.groupware.dto.ArticleWithCommentsDto;
import com.wise.groupware.dto.UserAccountDto;
import com.wise.groupware.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService sut;

    @Mock
    private ArticleRepository articleRepository;

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {

        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // When & Then
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitle(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitle(searchKeyword, pageable);
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenRequestingArticle_thenReturnsArticle() {

        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When & Then
        ArticleWithCommentsDto dto = sut.getArticle(articleId);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("없는 게시글을 조회하면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_thenSearchingArticle_thenThrowsException() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다. - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // Given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        // When
        sut.saveArticle(dto);

        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenUpdateArticleInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("new title", "new content", "#springboot");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        // When
        sut.updateArticle(dto);

        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 출력하고 아무것도 하지 않는다.")
    @Test
    void givenNonexistentArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        // Given
        ArticleDto dto = createArticleDto("new title", "new content", "#sprinboot");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticle(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("게시글 ID를 입력하면, 게시글을 삭제한다.")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // Given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);

        // When
        sut.deleteArticle(1L);

        // Then
        then(articleRepository).should().deleteById(articleId);
    }

    private Article createArticle() {
        return Article.of(
                createUserAccount(),
                "content",
                "content",
                "#java"
        );
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "du",
                "password",
                "du@mail.com",
                "Du",
                null
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "du",
                "password",
                "du@mail.com",
                "Du",
                "This is memo",
                LocalDateTime.now(),
                "du",
                LocalDateTime.now(),
                "du"
        );
    }


    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(
                1L,
                createUserAccountDto(),
                title,
                content,
                hashtag,
                LocalDateTime.now(),
                "du",
                LocalDateTime.now(),
                "du"
        );
    }

    private ArticleWithCommentsDto createArticleWithCommentDto() {
        return ArticleWithCommentsDto.of(
                1L,
                UserAccountDto.from(createUserAccount()),
                "title",
                "content",
                "#spring",
                createArticleComments(),
                LocalDateTime.now(),
                "du",
                LocalDateTime.now(),
                "du"
        );
    }

    private Set<ArticleCommentDto> createArticleComments() {
        return Set.of(ArticleCommentDto.of(1L,
                1L,
                UserAccountDto.from(createUserAccount()),
                "content",
                LocalDateTime.now(),
                "du",
                LocalDateTime.now(),
                "du")
        );
    }
}