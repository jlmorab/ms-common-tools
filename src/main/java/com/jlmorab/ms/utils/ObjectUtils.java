package com.jlmorab.ms.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectUtils {
	
	/**
	 * Get object representation in JSON string
	 * @param object Is any object {@code Object}
	 * @return Object JSON in string
	 */
	public static String getJsonString(Object object) {
		
		String result = "";
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		try {
			result = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			result = "{}";
		}//end try
		
		return result;
		
	}//end getJsonString()
	
}
