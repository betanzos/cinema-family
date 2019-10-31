#! /bin/bash

docker run --name 'cinema-family' --restart on-failure:3 -d -p 80:8080 -v /home/user/Videos:/mnt/videos cinema-family:1.0