FROM oracle/graalvm-ce:latest
COPY target/scala-2.13/joeAPI-assembly-1.0.jar .

ENTRYPOINT ["java","-jar","joeAPI-assembly-1.0.jar"]