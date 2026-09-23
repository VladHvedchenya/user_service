package school.faang.user_service.config.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {

    private final UserContext userContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        try {
            String userId = req.getHeader("x-user-id");

            if (userId != null) {
                try {
                    userContext.setUserId(Long.parseLong(userId));
                } catch (NumberFormatException e) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid x-user-id header");
                    return;
                }
            }

            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }
}
