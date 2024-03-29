package com.muud.auth.jwt;

import com.muud.auth.service.AuthService;
import com.muud.global.error.ApiException;
import com.muud.global.error.ExceptionType;
import com.muud.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import static com.muud.auth.jwt.Auth.Role.ADMIN;
import static com.muud.user.entity.Authority.ROLE_ADMIN;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtTokenUtils jwtTokenUtils;
    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if(request.getMethod().equals("OPTIONS")) {
            return true;
        }
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        Auth auth = ((HandlerMethod)handler).getMethodAnnotation(Auth.class);
        if(auth == null){
            return true;
        }else{
            String token = jwtTokenUtils.getTokenFromHeader(request);
            User user = authService.getLoginUser(Long.valueOf(jwtTokenUtils.getUserIdFromToken(token)));
            if(auth.role().compareTo(ADMIN)==0 && !user.getRole().equals(ROLE_ADMIN)){
                throw new ApiException(ExceptionType.FORBIDDEN_USER);
            }
            request.setAttribute("user", user);
        }

        return true;
    }
}