package tw.edu.fju.miniclinic.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object loggedIn = session.getAttribute("loggedInDoctorId");

        if (loggedIn == null) {
            String path = request.getRequestURI();
            if (path.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"請先登入\"}");
            } else {
                response.sendRedirect("/login");
            }
            return false;
        }

        return true;
    }
}