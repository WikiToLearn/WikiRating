#!/bin/sh

docker stop neo4j
docker rm neo4j

docker run  -d \
      	--publish=7474:7474 --publish=7687:7687 \
    	--volume=$HOME/neo4j/data:/data  \
     	--volume=$HOME/neo4j/logs:/logs  \
       	--name neo4j \
	neo4j:3.1
