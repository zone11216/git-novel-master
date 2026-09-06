package io.github.xxyopen.novel.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class MenuTreeRespDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long parentId;

    private String name;

    private String url;

    private Integer type;

    private String icon;

    private Integer sort;

    private List<MenuTreeRespDto> children;
}