package com.bob.wechat.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Document xmlParser(String text) throws Exception {
        StringReader sr = new StringReader(text);
        InputSource is = new InputSource(sr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    public static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }

    public static Matcher getMatcher(String regEx, String text) {
        Pattern pattern = Pattern.compile(regEx);
        return pattern.matcher(text);
    }
}
