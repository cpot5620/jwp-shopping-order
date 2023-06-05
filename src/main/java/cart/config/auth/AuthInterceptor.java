package cart.config.auth;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import cart.dto.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {
    private final AuthProvider authProvider;

    public AuthInterceptor(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        User user = authProvider.resolveUser(request.getHeader(AUTHORIZATION));
        request.setAttribute("user", user);
        return true;
    }
}