docker run -d  -p 2424:2424 -p 2480:2480   \
        -v orientdb_config:/orientdb/config \
        -v orient_database:/orientdb/databases \
        -v orient_backup:/orientdb/backup  \
        -e ORIENTDB_ROOT_PASSWORD=$1   \
        --name orientdb \
        wikitolearn/orientdb:latest

