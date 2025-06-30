//package com.sing4u.kr;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestConstructor;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//
//import java.util.List;
//
//import com.sing4u.kr.jwt.provider.JwtTokenProvider;
//import com.sing4u.kr.user.entity.enums.UserType;
//import com.sing4u.kr.user.enums.UserRole;
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@RequiredArgsConstructor
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@ActiveProfiles("test")
//class Sing4UApplicationTests {
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Test
//    void createAccessTokenTest() {
//        Long userId = 1L;
//        List<UserRole> roles = List.of(UserRole.FAN);
//        String accessToken = jwtTokenProvider.generateAccessToken(userId, roles, "test", UserType.FAN);
//
//        System.out.println("Access Token: " + accessToken);
//    }
//
//
//}
