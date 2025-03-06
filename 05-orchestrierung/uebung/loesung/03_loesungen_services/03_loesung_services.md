# Lösung 3. Services

Aufgaben:

1. Legt für die App `Hello-Service` einen Service an

- siehe `02_service.yaml`
- deployment: `kubectl apply -f 02_service.yaml`

2. Startet einen temporären Pod und überprüft, dass der Service erreichbar ist.

```shell script
❯ kubectl run my-shell --rm -i --tty --image byrnedo/alpine-curl --command sh
If you don't see a command prompt, try pressing enter.
/ # curl helloservice-service:8000/hello
Howdy, World!/ #
```

Bonus:

3. Load Balancing testen

Füge folgende Dependency zur `pom.xml` hinzu:

```xml
		<dependency>
			<groupId>jakarta.servlet</groupId>
			<artifactId>jakarta.servlet-api</artifactId>
			<version>6.0.0</version>
			<scope>provided</scope>
		</dependency>
```

Ersetze `HelloWorldController.java`:
```java
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HelloWorldController {
    @GetMapping("/hello")
    public String hello(HttpServletRequest clientRequest) {
        String localIp = clientRequest.getLocalAddr();
        return  "My ip is " + localIp;
    }
}

```
