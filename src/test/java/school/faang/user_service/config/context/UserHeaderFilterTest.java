package school.faang.user_service.config.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserHeaderFilterTest {

    @InjectMocks
    private UserHeaderFilter userHeaderFilter;

    @Mock
    private UserContext userContext;

    @Mock
    private FilterChain filterChain;

    @Test
    public void testDoFilterShouldSetUserIdWhenHeaderIsValid() throws ServletException, IOException {
        //arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("x-user-id", "42");

        //act
        userHeaderFilter.doFilter(request, response, filterChain);

        //assert
        verify(userContext).setUserId(42L);
        verify(filterChain).doFilter(request, response);
        verify(userContext).clear();
        assertEquals(MockHttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void testDoFilterShouldReturnBadRequestWhenHeaderIsInvalid() throws ServletException, IOException {
        //arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("x-user-id", "invalid-id");

        //act
        userHeaderFilter.doFilter(request, response, filterChain);

        //assert
        assertEquals(MockHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals("Invalid x-user-id header", response.getErrorMessage());
        verify(userContext, never()).setUserId(anyLong());
        verify(filterChain, never()).doFilter(request, response);
        verify(userContext).clear();
    }

    @Test
    public void testDoFilterShouldContinueChainWhenHeaderIsMissing() throws ServletException, IOException {
        //arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        //act
        userHeaderFilter.doFilter(request, response, filterChain);

        //assert
        verify(userContext, never()).setUserId(anyLong());
        verify(filterChain).doFilter(request, response);
        verify(userContext).clear();
        assertEquals(MockHttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void testDoFilterShouldClearUserContextWhenChainThrowsException() throws ServletException, IOException {
        //arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("x-user-id", "42");
        doThrow(new ServletException("Downstream failure")).when(filterChain).doFilter(request, response);

        //act + assert
        assertThrows(
                ServletException.class,
                () -> userHeaderFilter.doFilter(request, response, filterChain)
        );
        verify(userContext).setUserId(42L);
        verify(filterChain).doFilter(request, response);
        verify(userContext).clear();
    }
}
