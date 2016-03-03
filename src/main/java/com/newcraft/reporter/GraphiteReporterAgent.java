// Copyright 2016 Job Tiel Groenestege (Newcraftgroup)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.newcraft.reporter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

/**
 * Reports all cassandra metrics directly to graphite
 *
 */
public class GraphiteReporterAgent {

	private static final Logger LOG = LoggerFactory.getLogger(GraphiteReporterAgent.class);

	private static final String GRAPHITE_HOST = "localhost";
	private static final int GRAPHITE_PORT = 2003;
	private static final long REFRESH_SECONDS = 30;

	public static void premain(String agentArgs) throws UnknownHostException {

		// We assume agentArgs is the host to send the data to:
		// TODO add support for port as: 'host:port'
		// TODO add support for 'refresh' interval.
		String host = null;
		if (agentArgs != null && !agentArgs.trim().isEmpty()) {
			LOG.info("Using graphite host: '{}'", agentArgs);
			host = agentArgs;
		} else {
			LOG.info("No host passed using default: 'prom4'");
			host = GRAPHITE_HOST;
		}

		String hostname = InetAddress.getLocalHost().getHostName();
		String prefix = "cassandra." + hostname.split("\\.")[0];

		MetricRegistry registry = org.apache.cassandra.metrics.CassandraMetricsRegistry.Metrics;

		Graphite graphite = new Graphite(new InetSocketAddress(host, GRAPHITE_PORT));

		GraphiteReporter reporter = GraphiteReporter.forRegistry(registry).prefixedWith(prefix)
				.convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).filter(MetricFilter.ALL)
				.build(graphite);

		reporter.start(REFRESH_SECONDS, TimeUnit.SECONDS);

	}
}
