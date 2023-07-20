package com.wise.groupware.service;

import com.wise.groupware.domain.Article;
import com.wise.groupware.domain.type.SearchType;
import com.wise.groupware.dto.ArticleDto;
import com.wise.groupware.dto.ArticleUpdateDto;
import com.wise.groupware.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType title, String search_keyword) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public ArticleDto searchArticle(long articleId) {
        return null;
    }

    public void saveArticle(ArticleDto dto) {}

    public void updateArticle(long articleId, ArticleUpdateDto dto) {}

    public void deleteArticle(long articleId) {}
}
