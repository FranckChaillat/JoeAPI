FROM oracle/graalvm-ce:20.2.0-java11
COPY joeAPI-assembly-1.0.jar .

ENTRYPOINT ["java","-jar","joeAPI-assembly-1.0.jar"]