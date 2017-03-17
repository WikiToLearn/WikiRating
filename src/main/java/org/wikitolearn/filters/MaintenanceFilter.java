/**
 * 
 */
package org.wikitolearn.filters;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
/**
 * @author alessandro
 *
 */
@Component
public class MaintenanceFilter implements Filter {
	
	private @Value("${maintenance.uri}") String MAINTENANCE_URI;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		// Maintenance lock file to be checked
		File lockFile = new File("maintenance.lock");
		// Allow any maintenance URI request, otherwise check if maintenance mode is set and allow GET requests only
		if(MAINTENANCE_URI.equals(request.getRequestURI())){
			filterChain.doFilter(servletRequest, servletResponse);
		}else if(!HttpMethod.GET.matches(request.getMethod()) && lockFile.exists()){
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, 
					"Application is in maintenance mode. Only GET requests are supported.");
		}else{
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

}
