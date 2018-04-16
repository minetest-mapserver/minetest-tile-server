package io.rudin.minetest.tileserver.transformer;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.ResponseTransformer;

@Singleton
public class JsonTransformer implements ResponseTransformer {

	public JsonTransformer() {
		this.mapper = new ObjectMapper();
	}
	
	private final ObjectMapper mapper;
	
	@Override
	public String render(Object model) throws Exception {
		return mapper.writeValueAsString(model);
	}

}
