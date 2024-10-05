package org.example.expert.domain.common.dto;

import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String nickname;

    public AuthUser(Long id, String email, UserRole role, String nickname) {
        this.id = id;
        this.email = email;
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
        this.nickname = nickname;

    }

    /**
     * authorities에서 첫 번째 권한(GrantedAuthority)을 가져옵니다.
     * 그 권한의 이름을 getAuthority() 메서드를 사용해 문자열로 추출합니다.
     * 그 권한 이름을 기반으로 UserRole.of()를 호출하여 해당 문자열을 UserRole Enum 값으로 변환합니다.
     * @return
     */
    public UserRole getRole() {
        return UserRole.of(authorities.iterator().next().getAuthority());
    }
}
