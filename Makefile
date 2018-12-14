
TAG=registry.rudin.io/x86/minetest-tileserver
VOLUMES=-v maven-repo:/root/.m2 -v $(shell pwd)/src:/src -v $(shell pwd)/pom.xml:/pom.xml -v $(shell pwd)/target/docker:/target
MAVEN_TAG=maven:3.6-jdk-11

build:
	rm -rf target/docker
	docker run -it --rm $(VOLUMES) $(MAVEN_TAG) mvn install
	docker build . -t $(TAG)

push:
	docker push $(TAG)
