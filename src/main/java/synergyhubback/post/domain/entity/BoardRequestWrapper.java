package synergyhubback.post.domain.entity;

import lombok.Getter;
import lombok.Setter;
import synergyhubback.post.dto.request.BoardRequest;
import synergyhubback.post.dto.request.PostRoleRequest;
@Getter
@Setter
public class BoardRequestWrapper {
    private BoardRequest boardRequest;
    private PostRoleRequest ReadpostRoleRequest;
    private PostRoleRequest WritepostRoleRequest;
    private PostRoleRequest AdminpostRoleRequest;

}
