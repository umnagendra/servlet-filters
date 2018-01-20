package com.umnagendra.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Implements a {@link Filter} to dump HTTP requests
 * into a log file.
 *
 * @author Nagendra Mahesh
 */
public class RequestDumperFilter implements Filter {

    private static final String DUMP_FORMAT                 = "--------------------------------\n" +
                                                              "  %s\n" +
                                                              "  HTTP %s %s\n" +
                                                              "  QUERY PARAMS: %s\n" +
                                                              "  %s\n" +
                                                              "--------------------------------\n" +
                                                              "%s\n\n";
    private static final String HEADER_SECTION_FORMAT       = "\nHEADERS:\n%s\n";
    private static final String HEADER_FORMAT               = "\t%s: %s\n";

    public static final String INIT_PARAM_METHODS           = "methods";
    public static final String INIT_PARAM_HEADERS           = "dumpHeaders";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy (z) HH:mm:ss.SSS");

    private static final Logger logger = Logger.getLogger(RequestDumperFilter.class.getName());

    private List<String> methodList;
    private boolean dumpHeaders;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        dumpHeaders = Boolean.valueOf(filterConfig.getInitParameter(INIT_PARAM_HEADERS));

        String methods = filterConfig.getInitParameter(INIT_PARAM_METHODS);
        if (methods != null && !methods.isEmpty()) {
            methodList = Arrays.asList(methods.trim().split("\\s*,\\s*"));
        }

        logger.info("Initialized. [dumpHeaders = " + dumpHeaders + "], methods = [" + methodList + "]");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            if (servletRequest instanceof HttpServletRequest) {
                HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

                if (methodList == null || methodList.contains(httpRequest.getMethod().toUpperCase())) {
                    System.out.println(createDump(httpRequest));
                } else {
                    logger.fine("Received a request with method = " + httpRequest.getMethod() + ", skipping dump.");
                }
            } else {
                logger.fine("Received a request that is *NOT* HTTP. Doing nothing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Something bad happened. Exception = [" + e.getMessage() + "], check STDOUT for stack trace");
        } finally {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        logger.info("Destroyed");
    }

    private String createDump(HttpServletRequest request) {
        return String.format(DUMP_FORMAT,
                getCurrentTimestamp(),
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                dumpHeaders ? createHeaderSection(request) : "",
                extractPayload(request)
        );
    }

    private String getCurrentTimestamp() {
        return DATE_FORMAT.format(new Date());
    }

    private String createHeaderSection(HttpServletRequest request) {

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            StringBuilder headers = new StringBuilder();
            while (headerNames.hasMoreElements()) {
                String thisHeader = headerNames.nextElement();
                headers.append(String.format(HEADER_FORMAT, thisHeader, request.getHeader(thisHeader)));
            }
            return String.format(HEADER_SECTION_FORMAT, headers.toString());
        } else {
            logger.fine("No request headers found. [SESSION-ID: " + request.getSession().getId() + "]");
        }

        return "";
    }

    private String extractPayload(HttpServletRequest request) {
        // TODO
        return "";
    }
}
