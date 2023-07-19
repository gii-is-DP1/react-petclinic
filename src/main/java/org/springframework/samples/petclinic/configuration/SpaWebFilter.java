package org.springframework.samples.petclinic.configuration;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class SpaWebFilter extends OncePerRequestFilter {

	    @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	                                    FilterChain filterChain) throws ServletException, IOException {
//	        String path = request.getRequestURI();
//	        Authentication user = SecurityContextHolder.getContext().getAuthentication();
	        request.getRequestDispatcher("/").forward(request, response);
//	        if (user != null && !path.startsWith("/api") && !path.contains(".") && path.matches("/(.*)")) {
//	            request.getRequestDispatcher("/").forward(request, response);
//	            return;
//	        }

	        filterChain.doFilter(request, response);
	    }
}
