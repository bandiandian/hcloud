package com.haier.interconn.hcloud.trace;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.actuate.trace.TraceProperties;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

/**
 * 扩展WebRequestTraceFilter类，加上收集延迟
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-10-17  16:58
 */
public class CustomWebRequestTraceFilter extends OncePerRequestFilter implements Ordered {

    private static final Log logger = LogFactory.getLog(CustomWebRequestTraceFilter.class);

    private static final String ATTRIBUTE_STOP_WATCH = CustomWebRequestTraceFilter.class.getName()
            + ".StopWatch";


    private boolean dumpRequests = false;

    // Not LOWEST_PRECEDENCE, but near the end, so it has a good chance of catching all
    // enriched headers, but users can add stuff after this if they want to
    private int order = Ordered.LOWEST_PRECEDENCE - 10;

    private final TraceRepository repository;

    private ErrorAttributes errorAttributes;

    private final TraceProperties properties;

    /**
     * Create a new {@link CustomWebRequestTraceFilter} instance.
     *
     * @param repository the trace repository
     * @param properties the trace properties
     */
    public CustomWebRequestTraceFilter(TraceRepository repository, TraceProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    /**
     * Debugging feature. If enabled, and trace logging is enabled then web request
     * headers will be logged.
     *
     * @param dumpRequests if requests should be logged
     */
    public void setDumpRequests(boolean dumpRequests) {
        this.dumpRequests = dumpRequests;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Map<String, Object> trace = getTrace(request);
        logTrace(request, trace);
        //创建计时器
        StopWatch stopWatch = createStopWatchIfNecessary(request);
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        try {
            filterChain.doFilter(request, response);
            status = response.getStatus();
        } finally {
            //结束计时器
            stopWatch.stop();
            request.removeAttribute(ATTRIBUTE_STOP_WATCH);
            //stopWatch.getTotalTimeMillis()
            //把延迟的数据放进去
            trace.put("method.delay.ms",stopWatch.getTotalTimeMillis());
            enhanceTrace(trace, status == response.getStatus() ? response
                    : new CustomStatusResponseWrapper(response, status));
            this.repository.add(trace);


        }
    }

    protected Map<String, Object> getTrace(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Throwable exception = (Throwable) request
                .getAttribute("javax.servlet.error.exception");
        Principal userPrincipal = request.getUserPrincipal();
        Map<String, Object> trace = new LinkedHashMap<String, Object>();
        Map<String, Object> headers = new LinkedHashMap<String, Object>();
        trace.put("method", request.getMethod());
        trace.put("path", request.getRequestURI());
        trace.put("headers", headers);
        if (isIncluded(TraceProperties.Include.REQUEST_HEADERS)) {
            headers.put("request", getRequestHeaders(request));
        }
        add(trace, TraceProperties.Include.PATH_INFO, "pathInfo", request.getPathInfo());
        add(trace, TraceProperties.Include.PATH_TRANSLATED, "pathTranslated",
                request.getPathTranslated());
        add(trace, TraceProperties.Include.CONTEXT_PATH, "contextPath", request.getContextPath());
        add(trace, TraceProperties.Include.USER_PRINCIPAL, "userPrincipal",
                (userPrincipal == null ? null : userPrincipal.getName()));
        if (isIncluded(TraceProperties.Include.PARAMETERS)) {
            trace.put("parameters", getParameterMapCopy(request));
        }
        add(trace, TraceProperties.Include.QUERY_STRING, "query", request.getQueryString());
        add(trace, TraceProperties.Include.AUTH_TYPE, "authType", request.getAuthType());
        add(trace, TraceProperties.Include.REMOTE_ADDRESS, "remoteAddress", request.getRemoteAddr());
        add(trace, TraceProperties.Include.SESSION_ID, "sessionId",
                (session == null ? null : session.getId()));
        add(trace, TraceProperties.Include.REMOTE_USER, "remoteUser", request.getRemoteUser());
        if (isIncluded(TraceProperties.Include.ERRORS) && exception != null
                && this.errorAttributes != null) {
            trace.put("error", this.errorAttributes
                    .getErrorAttributes(new ServletRequestAttributes(request), true));
        }
        return trace;
    }

    private Map<String, Object> getRequestHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new LinkedHashMap<String, Object>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            List<String> values = Collections.list(request.getHeaders(name));
            Object value = values;
            if (values.size() == 1) {
                value = values.get(0);
            } else if (values.isEmpty()) {
                value = "";
            }
            headers.put(name, value);
        }
        if (!isIncluded(TraceProperties.Include.COOKIES)) {
            headers.remove("Cookie");
        }
        postProcessRequestHeaders(headers);
        return headers;
    }

    private Map<String, String[]> getParameterMapCopy(HttpServletRequest request) {
        return new LinkedHashMap<String, String[]>(request.getParameterMap());
    }

    /**
     * Post process request headers before they are added to the trace.
     *
     * @param headers a mutable map containing the request headers to trace
     * @since 1.4.0
     */
    protected void postProcessRequestHeaders(Map<String, Object> headers) {
    }

    @SuppressWarnings("unchecked")
    protected void enhanceTrace(Map<String, Object> trace, HttpServletResponse response) {
        if (isIncluded(TraceProperties.Include.RESPONSE_HEADERS)) {
            Map<String, Object> headers = (Map<String, Object>) trace.get("headers");
            headers.put("response", getResponseHeaders(response));
        }
    }

    private Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new LinkedHashMap<String, String>();
        for (String header : response.getHeaderNames()) {
            String value = response.getHeader(header);
            headers.put(header, value);
        }
        if (!isIncluded(TraceProperties.Include.COOKIES)) {
            headers.remove("Set-Cookie");
        }
        headers.put("status", "" + response.getStatus());
        return headers;
    }

    private void logTrace(HttpServletRequest request, Map<String, Object> trace) {
        if (logger.isTraceEnabled()) {
            logger.trace("Processing request " + request.getMethod() + " "
                    + request.getRequestURI());
            if (this.dumpRequests) {
                logger.trace("Headers: " + trace.get("headers"));
            }
        }
    }

    private void add(Map<String, Object> trace, TraceProperties.Include include, String name,
                     Object value) {
        if (isIncluded(include) && value != null) {
            trace.put(name, value);
        }
    }

    private boolean isIncluded(TraceProperties.Include include) {
        return this.properties.getInclude().contains(include);
    }

    public void setErrorAttributes(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    private static final class CustomStatusResponseWrapper
            extends HttpServletResponseWrapper {

        private final int status;

        private CustomStatusResponseWrapper(HttpServletResponse response, int status) {
            super(response);
            this.status = status;
        }

        @Override
        public int getStatus() {
            return this.status;
        }

    }

    /**
     * 创建计时器 用于记录调用时长
     * @param request
     * @return
     */
    private StopWatch createStopWatchIfNecessary(HttpServletRequest request) {
        StopWatch stopWatch = (StopWatch) request.getAttribute(ATTRIBUTE_STOP_WATCH);
        if (stopWatch == null) {
            stopWatch = new StopWatch();
            stopWatch.start();
            request.setAttribute(ATTRIBUTE_STOP_WATCH, stopWatch);
        }
        return stopWatch;
    }



}
