
TAG=registry.rudin.io/x86/minetest-tileserver
VOLUMES=-v maven-repo:/root/.m2 -v $(shell pwd)/src:/src -v $(shell pwd)/pom.xml:/pom.xml -v $(shell pwd)/target/docker:/target
MAVEN_TAG=maven:3.6-jdk-11

build:
	rm -rf target/docker
	docker run -it --rm $(VOLUMES) -e M3_HOME=/root/.m2 $(MAVEN_TAG) mvn install
	docker build . -t $(TAG)

push:
	docker push $(TAG)
