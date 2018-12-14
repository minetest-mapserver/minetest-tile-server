
TAG=registry.rudin.io/x86/minetest-tileserver

VOLUMES= -v maven-repo:/root/.m2
VOLUMES += -v $(shell pwd)/src:/src:ro
VOLUMES += -v $(shell pwd)/pom.xml:/pom.xml:ro
VOLUMES += -v $(shell pwd)/target/docker:/target
VOLUMES += -v $(shell pwd)/testdata:/testdata:ro

MAVEN_TAG=maven:3.6-jdk-11

build:
	rm -rf target/docker
	docker run -it --rm $(VOLUMES) $(MAVEN_TAG) mvn install
	docker build . -t $(TAG)

push:
	docker push $(TAG)
