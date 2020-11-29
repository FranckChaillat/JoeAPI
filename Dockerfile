FROM oracle/graalvm-ce:latest
COPY joeAPI-assembly-1.0.jar .

ENTRYPOINT ["java","-jar","joeAPI-assembly-1.0.jar"]