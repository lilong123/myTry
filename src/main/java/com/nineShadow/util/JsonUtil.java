package com.nineShadow.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/*
map与string转换，实体类与string转换
*/
public class JsonUtil {

	public static String getStringFromObject(Object object) {

		ObjectMapper mapper = new ObjectMapper();

		String jsonfromList = null;
		try {
			// jsonfromList = mapper.writeValueAsString(object);
			jsonfromList = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (IOException e) {
			System.out.println("Json解析: " + object.toString() + "出错:"+e);
		}
		System.out.println("object: " + object + "json数据:" + jsonfromList);
		return jsonfromList;
	}

	public static Object getObjectFromString(String json, Class parserClass)throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		// mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,
		// true);
		return mapper.readValue(json, parserClass);
	}

	public static class NullSerializer extends JsonSerializer<Object> {
		public void serialize(Object value, JsonGenerator jgen,
				SerializerProvider provider) throws IOException,
				JsonProcessingException {
			// any JSON value you want...
			jgen.writeString("");
		}
	}

	public static HashMap<String, Object> StringtoMap(String json)
	{
		 Map<String, Object> map = new HashMap<String, Object>();  
	     ObjectMapper mapper = new ObjectMapper();
	     try{
	    	 map=mapper.readValue(json,new TypeReference<HashMap<String,Object>>() {});
	    	 return (HashMap<String, Object>) map;
	     }catch (Exception e) {
			// TODO: handle exception
	    	 e.printStackTrace();
	    	 return null;
		}
	}
	
	public static String MaptoString(@SuppressWarnings("rawtypes") Map map)
	{
		String json="";
		try {
			ObjectMapper mapper=new ObjectMapper();
			json=mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
//	public static void main(String[] args) throws JsonParseException,
//			JsonMappingException, IOException {
//		// String json = "{\"Result\":\"100\"" + "}";
//		// String json = "{\"Result\":\"100\",\"" + "" + "Devices\":null}";
//		// AckPassGetDevicesNewResp resp = (AckPassGetDevicesNewResp)
//		// getObjectFromString(
//		// json, AckPassGetDevicesNewResp.class);
//		// System.out.println(resp);
//		//
//		// if(resp.getDevices() == null){
//		// System.out.println("is null");
//		// }
//
//		// GameGetFriendListResp ggflresp = new GameGetFriendListResp();
//		// ggflresp.setSuccess("1");
//		// System.out.println(ObjectToJson.getGameGetFriendListResp(ggflresp));
//	}
}
