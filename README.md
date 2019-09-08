# Simple Application To Detect Ddos Attacks In Real Time

# Problem Statement

The customer runs a website and periodically is attacked by a botnet in a Distributed Denial of Service (DDOS) attack. You’ll be given a log file in Apache log format from a given attack. Use this log to build a simple real-time detector of DDOS attacks.  

# Requirements

* Ingest: Read a file from local disk and write to a message system such as Kafka.
* Write an application which reads messages from the message system and detects whether the attacker is part of the DDOS attack
* Once an attacker is found, the ip-address should be written to a results directory which could be used for further processing
* An attack should be detected one to two minutes after starting

# Tools Used

* Java 1.8
* Scala 2.11.8
* Apache Kafka 0.11.0.0
* Apache Spark 2.2.0
* Eclipse Scala IDE
* SBT
* GoAccess https://goaccess.io
* Virtual Box with Centos VM from Edureka


# Apache Log Format
Combined Log Format (https://httpd.apache.org/docs/1.3/logs.html)

# Log Example
127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326 "http://www.example.com/start.html" "Mozilla/4.08 [en] (Win98; I ;Nav)

# Execution Steps Taken (locally in IDE)
1. Download apache-access-log.txt into hdfs or local drive
2. Start Zookeeper
3. Start single node single cluster Kafka broker
4. Create topic "msg" on Kafka
5. Run spark application in Eclipse IDE
6. Run java jar file that reads apache-access-log.txt line by line into Kafka

# Architecture
log file -> kafka producer -> spark streaming -> output file in hdfs

# Visual Log Analysis with GoAccess


# References
https://goaccess.io/get-started
https://spark.apache.org/docs/2.2.0/streaming-programming-guide.html
https://spark.apache.org/docs/2.2.0/streaming-kafka-0-8-integration.html
https://www.youtube.com/watch?v=qrPjAyIapFY


