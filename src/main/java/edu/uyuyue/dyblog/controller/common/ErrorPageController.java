package edu.uyuyue.dyblog.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebResult;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by DuanYuan on 2019-10-15 14:44:26
 * Copyright © 2019 DuanYuan. All rights reserved.
 */

@Controller
public class ErrorPageController implements org.springframework.boot.web.servlet.error.ErrorController {
    private static ErrorPageController errorPageController;

    @Autowired
    private ErrorAttributes errorAttributes;

    private final static String ERROR_PATH = "/error";

    private ErrorPageController(ErrorAttributes errorAttributes){
        this.errorAttributes = errorAttributes;
    }

    public ErrorPageController(){
        if (errorPageController == null){
            errorPageController = new ErrorPageController(errorAttributes);
        }
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public ModelAndView errorHtml(HttpServletRequest request){
        HttpStatus status = getStatus(request);
        if (HttpStatus.BAD_REQUEST == status){
            return new ModelAndView("error/error_400");
        } else if (HttpStatus.NOT_FOUND == status){
            return new ModelAndView("error/error_404");
        } else {
            return new ModelAndView("error/error_5xx");
        }
    }

    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request){
        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Map<String, Object>>(body, status);
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null){
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    protected Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace){
        WebRequest webRequest = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
    }

    private HttpStatus getStatus(HttpServletRequest request){
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
        if (statusCode != null){
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception ex){
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
