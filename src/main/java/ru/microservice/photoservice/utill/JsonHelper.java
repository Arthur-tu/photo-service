package ru.microservice.photoservice.utill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JsonHelper {
    static Logger logger = LoggerFactory.getLogger(JsonHelper.class);

    private JsonHelper() {
        throw new IllegalStateException("Utility class");
    }
    public static String toJson(Object obj) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(obj);
    }

    public static <T> T fromJson(Class<T> clazz,String jsonString)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, clazz);
        }catch(Exception e)
        {
            logger.error("Ошибка конвертации",e);
            return null;
        }
    }
}
