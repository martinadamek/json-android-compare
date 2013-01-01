package com.martinadamek.jsonandroid;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonObjectMapperJson implements TestJson {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public String getName() {
		return "JacksonObjectMapper";
	}

	@Override
	public List<Map> parsePublicTimeline(InputStream inputStream) {

		Object timeline = null;
		try {
			timeline = mapper.readValue(inputStream, Object.class);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return (List<Map>) timeline;

	}
}
