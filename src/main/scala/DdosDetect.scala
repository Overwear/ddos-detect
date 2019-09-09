/* DdosDetect.scala */
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext


object DdosDetect {
  
  def updateFunction(newValues: Seq[Int], runningCount: Option[Int]): Option[Int] = {
    val newCount = runningCount.getOrElse(0) + newValues.sum  // add the new values with the previous running count to get the new count
    Some(newCount)
  }
  
  def functionToCreateContext(): StreamingContext = {
    val sparkConf = new SparkConf()
                  .setAppName("ddos-detect")
                  .setMaster("local[4]")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc, Seconds(5))   // new context
    
    // Kafka config
    val topics = Map("msg" -> 1)
    val zookeeper = "127.0.0.1:2181"
    val groupId = "group_1"
    
    // Output files loc
    val streamDataFileLoc = "/home/edureka/Desktop/ddos-detect/result/Stream/"
    val ddosIpFileLoc = "/home/edureka/Desktop/ddos-detect/result/ddos_ip/"
    
    // Create dstream with kafka
    val kafkaStream = KafkaUtils.createStream(ssc, zookeeper, groupId, topics)
    
    // Get the IP address of the record
    val ipAddress = kafkaStream.map(_._2).map(x => x.split(" ")(0))
    
    // Count the IP address
    val ipAddressCountsPair = ipAddress.map(x => (x,1))
    val runningCounts = ipAddressCountsPair.updateStateByKey[Int](updateFunction _)
    
    // Sort the IP addresses by descending order
    val sortedRes = runningCounts.transform(x => x.sortBy(_._2, false))
    
    // Get IP addresses that have more than 50 counts
    val ddosIp = sortedRes.filter(x => x._2 > 30)
    
    // Save Ddos IP addresses to files in hdfs
    sortedRes.repartition(1).saveAsTextFiles(streamDataFileLoc)
    ddosIp.repartition(1).saveAsTextFiles(ddosIpFileLoc)
    ssc.checkpoint("checkpoint")
    ssc
  }

  def main(args: Array[String]) {

    val ssc = StreamingContext.getOrCreate("checkpoint", functionToCreateContext _)
    
    ssc.start()
    Thread.sleep(180000)
    ssc.stop()
    //ssc.awaitTerminationOrTimeout(15000)
    println("Program Finished")
    
    
  }
}