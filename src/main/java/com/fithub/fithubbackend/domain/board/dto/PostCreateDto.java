package com.fithub.fithubbackend.domain.board.dto;

import com.fithub.fithubbackend.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Schema(description = "게시글 생성 dto")
public class PostCreateDto {

    @NotNull
    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 해시태그")
    private String hashTags;

    @Schema(description = "게시글 이미지")
    private List<MultipartFile> images;


}
