package com.jlmorab.ms.data.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class MetaDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String status;
	private int statusCode;
	private final String timestamp = LocalDateTime.now().toString();
	private final String transactionID = UUID.randomUUID().toString();
	private List<String> messages;
	private Boolean rollback;
	
	public MetaDTO( HttpStatus httpStatus ) {
    	this.status = httpStatus.name();
    	this.statusCode = httpStatus.value();
    }//end constructor
    
    public void addMessage( String message ) {
    	if( this.messages == null ) this.messages = new ArrayList<>();
    	this.messages.add(message);
    }//end addMessage()
	
}
