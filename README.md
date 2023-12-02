# Virtual Threads POC on Spring Boot 3.2

POC for testing Virtual Threads on a typical web application.

The application has 1 REST api with a thread sleep of 400 milliseconds. Test the performance with jMeter

1. Toggle `spring.threads.virtual.enabled` property
2. Run jmeter with `VirtualThreads.jmx`
3. Read the aggregate reports, in particular **Throughput (request/seconds)** and **95% percentile (milliseconds)** (see https://jmetervn.com/2016/10/10/analyze-the-aggregate-report-in-jmeter/ for better understand the report)

References:

* Enable Virtual threads before Spring Boot 3.2: https://spring.io/blog/2022/10/11/embracing-virtual-threads
* Virtual Threads Explained: https://inside.java/2023/10/30/sip086/
