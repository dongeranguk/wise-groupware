package com.wise.groupware.domain.response;

import com.wise.groupware.dto.ArticleDto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        String email,
        String nickname,
        LocalDateTime createdAt
) implements Serializable {

    public static ArticleResponse of(Long id, String title, String content, String hashtag, String email, String nickname, LocalDateTime createdAt) {
        return new ArticleResponse(id, title, content, hashtag, email, nickname, createdAt);
    }

    public static ArticleResponse from(ArticleDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if(nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return ArticleResponse.of(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.userAccountDto().email(),
                nickname,
                dto.createdAt()
        );
    }
}
