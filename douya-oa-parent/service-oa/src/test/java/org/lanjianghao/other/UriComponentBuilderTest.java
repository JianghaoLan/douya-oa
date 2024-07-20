package org.lanjianghao.other;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

public class UriComponentBuilderTest {

    @Test
    public void testUriComponentBuilder() {
        String address = "http://test.com";
//        String path1 = "path1";
//        String path2 = "path2/path3";
//        String path4 = "0";
        Map<String, Object> map = new HashMap<>();
        map.put("id", 45);
        map.put("name", 33);
        String url;
        url = UriComponentsBuilder
                .fromUriString(address)
                .path("/#/path1/{id}/{name}")
                .uriVariables(map)
                .build()
                .toString();
        System.out.println("url: " + url);

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        url = factory.uriString(address)
                .path("/#/path1/{id}/{name}")
                .build(map)
                .toString();
        System.out.println("url: " + url);
    }
}
