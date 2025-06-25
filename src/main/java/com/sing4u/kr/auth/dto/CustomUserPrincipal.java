package com.sing4u.kr.auth.dto;

import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.enums.UserRole;
import lombok.Getter;
import com.sing4u.kr.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// UserDetails와 OAuth2User를 모두 구현하여 일반/소셜 로그인에 공통으로 사용
@Getter
public class CustomUserPrincipal implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes; // OAuth2 로그인 시 사용

    // 일반 로그인을 위한 생성자
    public CustomUserPrincipal(User user) {
        this.user = user;
    }

    // OAuth2 로그인을 위한 추가 생성자
    public CustomUserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

//    // 이 생성자는 User 엔티티를 직접 받지 않고, 필요한 정보만 받음.
//    public CustomUserPrincipal(Long userId, String role) {
//        // 내부적으로 최소한의 정보만 가진 User 객체를 생성
//        // 이 User 객체는 DB와 연동(영속성)되지 않은, 순수한 데이터 전달용 객체
//        this.user = User.testUserBuilder(userId, null, null); // User 엔티티의 testUserBuilder 활용
//        this.user.setRole(UserRole.valueOf(role)); // 역할 설정
//    }

    @Override
    public Map<String, Object> getAttributes() { return attributes; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
//        collection.add(() -> user.getRole().name()); // .name()으로 enum의 문자열 이름을 가져온다.
        collection.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return collection;
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    // Spring Security에서 username은 고유 식별자여야 함. email을 사용
    @Override
    public String getUsername() { return user.getEmail(); }

    // OAuth2 표준의 name 속성.  사용자의 실제 이름을 반환
    @Override
    public String getName() { return user.getNickname(); }

    // 우리 시스템 내부에서 사용할 userId를 가져오는 커스텀 메소드
    public Long getUserId() { return user.getId();}

    public String getNickname() {
        return user.getNickname();
    }

    public UserType getUserType() {
        return user.getUserType();
    }

    // UserDetails 구현
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return user.getDeletedAt() == null; } // soft-delete 고려
}
