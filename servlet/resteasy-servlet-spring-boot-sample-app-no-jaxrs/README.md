## Usage

### Start the server

```bash
$ mvn spring-boot:run
...
2022-02-22 20:44:30.299  INFO 61839 --- [           main] com.sample.app2.MyApp                    : Started MyApp in 1.219 seconds (JVM running for 1.401)
```

## Access the service


Use the following command to access the service:

```bash
curl --location --request POST 'localhost:8080/echo' \
--header 'Content-Type: text/plain' \
--data-raw 'foo'
```

And here is the output:

```bash
{"timestamp":1645534458892,"echoText":"foo"}
```

