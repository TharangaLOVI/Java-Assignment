package com.assignment.rest.common;

public class CommonConfig {
	
	public enum MessageTemplate{
		
		SUCCESS_INSERT_HOTEL("Successfully inserted hotel"),
		UNABLE_TO_FOUND_HOTEL("Unable to found hotel"),
		EMPTY_HOTELS("Empty Hotels"),
		DA_ERROR("DB Error"),
		JSON_ERROR("DB Error");
		
		private String message;
		
		private MessageTemplate(String mesage){
			this.message = mesage;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return message;
		}
		
	}

	public enum CommonKey{
		
		STATUS("status"), 
		VALUE("value");
		
		private String message;
		
		private CommonKey(String mesage){
			this.message = mesage;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return message;
		}
		
	}
	
}
