package org.lanjianghao.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanjianghao.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtils {
    public static void out(HttpServletResponse response, Result<?> r) {
        out(response, r, new ObjectMapper());
    }

    public static void out(HttpServletResponse response, Result<?> r, ObjectMapper objectMapper) {
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            mapper.writeValue(response.getWriter(), r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
