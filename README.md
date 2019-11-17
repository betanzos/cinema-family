# Cinema Family
Is a very simple Video Center designed for share family videos over home local networks.

Created for my wife, Helen. She has loved it! :grinning:

# Subtitles
Support subtitle formats VTT and STR (converted to VTT on the fly). In order to detect 
subtitles for an specific video, its file name must be equal to video name (without extension).

# How to use
## JAR file
You can to obtain an Uber-JAR by compiling the project using Maven and, at least, JDK 11 or 
downloading one from dist directory.

The official JARs have been built with JDK 11.

### Compiling with Maven
```
> mvn clean package
```
JAR file will be generated into `target` directory.

### Run from JAR file
In order to run the app must use the following:
```
> java -jar cinema-family-<version>.jar --root.dir="/path/to/videos/directory"
```

This command run an Apache Tomcat server on port 8080 which can be changed using the parameter
`--server.port` (e.g. `--server.port=80`).

Parameter `--root.dir` allow to set the video root directory to scan. Default value is
`/home/user/Videos`.

## Docker
### Making the image
You can make your own docker image using `docker-build.sh` script. Once you have the image you can use
`docker-run.sh` script for run one instance of it.

Note that `docker-run.sh` use the following parameter in order to bind a local directory with one inside 
the container: `-v /home/user/Videos:/mnt/videos`. So, you need to edit this parameter and specify you videos
directory (e.g. `-v /home/juan/MyVideos:/mnt/videos`). 

### From Docker Hub
If you don't want to built the image by yourself, official versions are published in 
[ebetanzos/cinema-family](https://hub.docker.com/r/ebetanzos/cinema-family) [Docker Hub](https://hub.docker.com/) 
repository.

The following command copy the image from Docher Hub to your local machine:
```
> docker pull ebetanzos/cinema-family
```

Now you can run one instance of this image using `docker-run.sh` script changing image name to `ebetanzos\cinema-family[:tag]`
and the value of `-v` parameter.

# License
Cinema Family is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).