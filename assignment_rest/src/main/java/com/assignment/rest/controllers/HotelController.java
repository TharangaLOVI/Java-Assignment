package com.assignment.rest.controllers;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.assignment.rest.common.CommonConfig.CommonKey;
import com.assignment.rest.common.CommonConfig.MessageTemplate;
import com.assignment.rest.models.Hotel;
import com.assignment.rest.models.dao.HotelDAO;

@Controller
@RequestMapping(value = "/hotel")
public class HotelController {
	
	@Autowired
	HotelDAO hotelDAO;
	
	@RequestMapping()
	public @ResponseBody String listAll() {
		try{
			List<Hotel> hotels = hotelDAO.list();
			
			if(hotels.isEmpty())
				throw new Exception(MessageTemplate.EMPTY_HOTELS.toString());
			
			return prepareJSONArrayresult(hotels);
			
		}catch(Exception e){
			
			System.out.println(e.getMessage());
			
			return prepareResult(-1, e.getMessage());
			
		}
	}
	
	@RequestMapping("/{id}")
	public @ResponseBody String findById(@PathVariable("id") int hotelId) {
		try{
			
			Hotel hotel = hotelDAO.find(hotelId);
			
			if(hotel == null)
				throw new Exception(MessageTemplate.UNABLE_TO_FOUND_HOTEL.toString());
			
			return prepareJSONObjectResult(hotel);
			
		}catch(Exception e){
			System.out.println(e.getMessage());
			return prepareResult(-1, e.getMessage());
		}
	}
	
	@RequestMapping("/name/{name}")
	public @ResponseBody String findByName(@PathVariable("name") String name) {
		try{
			
			List<Hotel> hotels = hotelDAO.customQueryDataList("FROM Hotel WHERE name = '" + name + "'");
			
			if(hotels.isEmpty())
				throw new Exception(MessageTemplate.UNABLE_TO_FOUND_HOTEL.toString());
			
			return prepareJSONArrayresult(hotels);
			
		}catch(Exception e){
			System.out.println(e.getMessage());
			return prepareResult(-1, e.getMessage());
		}
	}
	
	@RequestMapping("/keyword/{key}")
	public @ResponseBody String findByKeyWord(@PathVariable("key") String key) {
		try{
			
			List<Hotel> hotels = hotelDAO.customQueryDataList("FROM Hotel WHERE name LIKE '"+key+"%'");
			
			if(hotels.isEmpty())
				throw new Exception(MessageTemplate.UNABLE_TO_FOUND_HOTEL.toString());
			
			return prepareJSONArrayresult(hotels);
			
		}catch(Exception e){
			System.out.println(e.getMessage());
			return prepareResult(-1, e.getMessage());
		}
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody String insertHotel(@ModelAttribute Hotel hotel) {
		
		try{
			
			hotelDAO.create(hotel);
			
			return prepareResult(1, MessageTemplate.SUCCESS_INSERT_HOTEL.toString());
			
		}catch(Exception e){
			
			System.out.println(e.getMessage());
			
			return prepareResult(-1, e.getMessage());
		}
		
	}
	
	private String prepareResult(int status, String value) {

		JSONObject jsonResponce = new JSONObject();
		jsonResponce.put(CommonKey.STATUS.toString(), status);
		jsonResponce.put(CommonKey.VALUE.toString(), value);
		return jsonResponce.toString();

	}
	
	private String prepareJSONArrayresult(Object objectValue) {

		JSONObject jsonResponce = new JSONObject();
		try {
			if (objectValue != null) {
				
				jsonResponce.put(CommonKey.STATUS.toString(), 1);
				ObjectMapper objectMapper = new ObjectMapper();
				String stringObject = objectMapper.writeValueAsString(objectValue);
				JSONArray jsonArrayValue = new JSONArray(stringObject);
				jsonResponce.put(CommonKey.VALUE.toString(), jsonArrayValue);

			} else {
				throw new Exception(MessageTemplate.JSON_ERROR.toString());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			jsonResponce.put(CommonKey.STATUS.toString(), -1);
			jsonResponce.put(CommonKey.VALUE.toString(), e.getMessage().toString());
		}
		return jsonResponce.toString();

	}
	
	private String prepareJSONObjectResult(Object objectValue) {

		JSONObject jsonResponce = new JSONObject();
		try {
			if (objectValue != null) {
				
				jsonResponce.put(CommonKey.STATUS.toString(), 1);
				jsonResponce.put(CommonKey.VALUE.toString(), new JSONObject(objectValue));

			} else {
				throw new Exception(MessageTemplate.JSON_ERROR.toString());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage().toString());
			jsonResponce.put(CommonKey.STATUS.toString(), -1);
			jsonResponce.put(CommonKey.VALUE.toString(), e.getMessage().toString());
		}
		return jsonResponce.toString();

	}

}
