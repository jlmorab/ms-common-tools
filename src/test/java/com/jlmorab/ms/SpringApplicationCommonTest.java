package com.jlmorab.ms;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class SpringApplicationCommonTest {

	@Test
	void executeSpringBootContext() {
		SpringApplicationCommon springApplication = new SpringApplicationCommon();
		try ( MockedStatic<SpringApplication> mockedStatic = Mockito.mockStatic(SpringApplication.class) ) {
			assertDoesNotThrow( () -> springApplication.init(new String[] {}) );
			mockedStatic.verify( () -> SpringApplication.run(SpringApplicationCommon.class, new String[] {}), times(1) );
		}//end try
	}//end executeSpringBootContext()

}
