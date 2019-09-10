/* DdosDetect.scala */
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext


object DdosDetect {
  
  def functionToCreateContext(): StreamingContext = {
    val sparkConf = new SparkConf()
                  .setAppName("ddos-detect")
                  .setMaster("local[4]")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc, Seconds(1))
    
    // Kafka config
    val topics = Map("msg" -> 1)
    val zookeeper = "127.0.0.1:2181"
    val groupId = "group_1"
    
    // Output files loc
    val streamDataFileLoc = "/ddos_project/results/Stream/"
    val ddosIpFileLoc = "/ddos_project/results/ddos_ip/"
    
    // Create dstream with kafka
    val kafkaStream = KafkaUtils.createStream(ssc, zookeeper, groupId, topics)
    
    // Get the IP address of the record
    val ipAddress = kafkaStream.map(_._2).map(x => x.split(" ")(0))
    
    // Count the IP address
    val ipAddressCountsPair = ipAddress.map(x => (x,1))
    
    // Reduce each batch in a 150 seconds window every 10 seconds
    val runningIpAddresses = ipAddressCountsPair.reduceByKeyAndWindow((a:Int,b:Int) => a+b, (a:Int,b:Int) => a-b, Seconds(150), Seconds(10))
    
    // Sort the IP addresses by descending order
    val sortedRes = runningIpAddresses.transform(x => x.sortBy(_._2, false))
    
    // Get IP addresses that have more than 20 counts
    val ddosIp = sortedRes.filter(x => x._2 > 20)
    
    // Save Ddos IP addresses to files in hdfs
    sortedRes.repartition(1).saveAsTextFiles(streamDataFileLoc)
    ddosIp.repartition(1).saveAsTextFiles(ddosIpFileLoc)
    ssc.checkpoint("/ddos_project/checkpoint/")
    ssc
  }

  def main(args: Array[String]) {

    val ssc = StreamingContext.getOrCreate("/ddos_project/checkpoint/", functionToCreateContext _)
    
    //start the stream
    ssc.start()
    Thread.sleep(140000)
    ssc.stop()
    println("Program Finished")
    
    
  }
}