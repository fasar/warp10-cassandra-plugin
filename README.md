# Cassandra Module for Warp10 (not official)

This [Warp 10](https://www.warp10.io/) extension allow access to the [Cassandra](https://cassandra.apache.org/) database.

## Intallation

* Compile with ./gradlew shadowJar
* Copy the build/libs/*.jar in WARP10_CONF_FOLDER/lib
* Copy fasar.warp10.cassandra-extension.conf on WARP10_CONF_FOLDER/etc/conf.d/81-CassandraPlugin.conf
* Enjoy the Warp10 + Cassandra

## Quick Start

You can have a try with the docker-compose example on : https://github.com/fasar/warp10-dockerfile/tree/w10-CassandraPlugin.
Take care, the docker file with the Warp10 Cassandra Plugin is base on the branch  ***w10-CassandraPlugin***.

Run the docker-compose configuration with : `docker-compose up`.
It will create a docker network on sub-network 10.0.0.0/24.
If this network is already use on you computer, change IPs on docker-compose.yml file.

Now you have to create a keyspace and a table.
To do that run the cqlsh application with the followings: 

```bash
user@local:~$ docker run -it --rm --network warp10-dockerfile_warp10_net  cassandra:3.11.10 cqlsh cassandra-dc1

Connected to Test Cluster at 10.0.0.101:9042.
[cqlsh 5.0.1 | Cassandra 3.11.10 | CQL spec 3.4.4 | Native protocol v4]
Use HELP for help.
```

And execute the CQL script:

```sql
CREATE KEYSPACE IF NOT EXISTS kyspc WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE IF NOT EXISTS kyspc.kv (
    key text PRIMARY KEY,
    value text
);

INSERT INTO kyspc.kv (key, value) VALUES ('p1', 'v1');
```

Now you can select data with the Warpscript : `"SELECT * from kyspc.kv" CSELECT`

## Use CFETCH command.

To use the CFETCH command you need to have a table compatible with time series.

You can add a table with the CQL script:

```sql
CREATE KEYSPACE IF NOT EXISTS kyspc WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE IF NOT EXISTS kyspc.ts (
    name text,
    ts timestamp,
    val double,
    PRIMARY KEY (name, ts)
) WITH CLUSTERING ORDER BY (ts ASC)
AND compaction = {'class': 'org.apache.cassandra.db.compaction.TimeWindowCompactionStrategy', 'compaction_window_size': '1', 'compaction_window_unit': 'DAYS', 'max_threshold': '32', 'min_threshold': '4'}
;

INSERT INTO kyspc.ts (name, ts, val) VALUES ('sensor1', '1970-01-01T00:00:00Z', 12.2);
INSERT INTO kyspc.ts (name, ts, val) VALUES ('sensor1', '1970-01-01T00:01:00Z', 12.8);
INSERT INTO kyspc.ts (name, ts, val) VALUES ('sensor1', '1970-01-01T00:02:00Z', 13.1);
INSERT INTO kyspc.ts (name, ts, val) VALUES ('sensor1', '1970-01-01T00:03:00Z', 23.0);
INSERT INTO kyspc.ts (name, ts, val) VALUES ('sensor1', '1970-01-01T00:10:00Z', 15.8);
INSERT INTO kyspc.ts (name, ts, val) VALUES ('sensor1', '1970-01-01T00:12:00Z', 14.5);
INSERT INTO kyspc.ts (name, ts, val) VALUES ('sensor1', '1970-01-01T00:14:00Z', 13.1);
INSERT INTO kyspc.ts (name, ts, val) VALUES ('sensor1', '1970-01-01T00:15:00Z', 12.4);
INSERT INTO kyspc.ts (name, ts, val) VALUES ('sensor1', '1970-01-01T00:16:00Z', 8.5);
```

Then use the Warpscript: 

```
"SELECT ts, val FROM kyspc.ts WHERE name = 'sensor1';" 
[ 0 NaN NaN NaN 1 ]
CFETCH
```

## Final World

Be happy in your life !

