#! /bin/bash

docker build -t cinema-family:1.0 --build-arg JAR_FILE=target/cinema-family-1.0.jar .