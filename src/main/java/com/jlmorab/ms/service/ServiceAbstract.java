package com.jlmorab.ms.service;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import com.jlmorab.ms.data.dto.MetaDTO;
import com.jlmorab.ms.data.dto.WebResponseDTO;
import com.jlmorab.ms.exception.LogicException;
import com.jlmorab.ms.utils.ObjectUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ServiceAbstract {

	protected WebResponseDTO executeFlow( HttpServletResponse response, Logger log, Supplier<?> logic) {
		log.trace(">> {}()", getMethodInvocation());
		try {
			Object data = logic.get();
			traceLeavingMethod( log, data );
			return responseOK( response, data );
		} catch( IllegalArgumentException e ) {
			log.error("Invalid parameter provided", e);
			return responseWithMessage( response, HttpStatus.BAD_REQUEST, e.getMessage() );
		} catch( LogicException e ) {
			log.error("An error occurred in the business rule", e);
			return responseWithMessage( response, e.getHttpStatus(), e.getMessage() );
		} catch( Exception e ) {
			log.error("An error occurred while retrieving data", e);
			return responseWithMessage( response, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}//end try
	}//end executeFlow()
	
	protected WebResponseDTO responseOK( HttpServletResponse response ) {
		return response( response, HttpStatus.OK );
	}//end responseOK()
	
	protected WebResponseDTO responseOK( HttpServletResponse response, Object data ) {
		return responseWithData( response,HttpStatus.OK, data );
	}//end responseOK()
	
	protected WebResponseDTO responseWithMessage( HttpServletResponse response, HttpStatus httpStatus, String message ) {
		WebResponseDTO webResponse = response( response, httpStatus );
		webResponse.getMeta().addMessage( message );
		return webResponse;
	}//end responseWithMessage()
	
	protected WebResponseDTO responseWithData( HttpServletResponse response, HttpStatus httpStatus, Object data ) {
		WebResponseDTO webResponse = response( response, httpStatus );
		webResponse.setData(data);
		return webResponse;
	}//end responseWithData()
	
	protected WebResponseDTO response( HttpServletResponse response, HttpStatus httpStatus ) {
		response.setStatus( httpStatus.value() );
		MetaDTO meta = new MetaDTO( httpStatus );
		return WebResponseDTO.builder()
				.meta( meta )
				.build();
	}//end response()
	
	
	private void traceLeavingMethod( Logger log, Object object ) {
		if (log.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("<< %s() < Returning ", getMethodInvocation()));
			if (object != null) {
				sb.append( object.getClass().getName() + " = " );
				sb.append( ObjectUtils.getJsonString(object) );
			} else {
				sb.append("null");
			}//end if
			log.trace(sb.toString());
		}//end if
	}//end traceLeavingMethod()
	
	private String getMethodInvocation() {
		return StackWalker.getInstance()
				.walk(frames -> frames.skip(1)
						.filter(frame -> !frame.getClassName().equals(ServiceAbstract.class.getName()))
						.findFirst()
						.map(frame -> String.format("%s.%s", frame.getClassName(), frame.getMethodName()))
						.orElse("Method not available"));
	}//end getMethodInvocation()
	
}
