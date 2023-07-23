package com.wise.groupware.domain.response;

import com.wise.groupware.dto.ArticleCommentDto;
import com.wise.groupware.dto.UserAccountDto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ArticleCommentResponse(
        Long id,
        String content,
        String email,
        String nickname,
        LocalDateTime createdAt

) implements Serializable {

    public static ArticleCommentResponse of(Long id, String content, String email, String nickname, LocalDateTime createdAt) {
        return new ArticleCommentResponse(id, content, email, nickname, createdAt);
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if(nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return ArticleCommentResponse.of(
                dto.id(),
                dto.content(),
                dto.userAccountDto().email(),
                nickname,
                dto.createdAt()
        );
    }
}
