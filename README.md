# Cassandra Graphite

Cassandra Graphite sends metrics data (read from JMX) to a Graphite server.

When adding this to cassandra the following steps are required:

1. Get the code, make sure that the metrics library has the same version as cassandra is using.
   Cassandra stores it's libraries in `/usr/share/cassandra/lib` 
2. Compile the project: `maven clean install`; it will copy all dependencies into the `target/lib` folder
3. Copy the `target/cassandra-graphite-{version}.jar` to the `/usr/share/cassandra/lib` on all nodes
4. Copy the `target/lib/metrics-graphite-{version}.jar` to the `/usr/share/cassandra/lib` on all nodes
5. Make sure the `/etc/default/cassandra` is not defining anything (with the current setup all nodes will fail
6. Edit the `/etc/cassandra/cassandra-env.sh` and add the following line:
   ```bash
   # Add Metrics agent:
   JVM_OPTS="$JVM_OPTS -javaagent:/usr/share/cassandra/lib/cassandra-graphite-{version}.jar"
   ```
7. Restart the nodes one be one; observe graphite since it should show the metrics.
