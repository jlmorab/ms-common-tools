# ms-common-tools
Configuration and common tools for web projects in a microservice environment

## 0.3.0

Include @EnableScheduling in Spring context. To disable it, you must include the property:

~~~batch
# With application.properties
spring.task.scheduling.enabled = true
~~~

~~~yaml
# With application.yml
spring:
	task:
		scheduling:
			enabled: true
~~~