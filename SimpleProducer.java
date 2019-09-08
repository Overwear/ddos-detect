
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.*;
import java.io.File;


public class SimpleProducer {

	public static void main(String[] args) throws Exception{
		Properties kafkaProps = new Properties();
		kafkaProps.put("bootstrap.servers", "localhost:9091");
		kafkaProps.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
		kafkaProps.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
		String dataFileLoc = "/home/edureka/Desktop/apache-access-log.txt";
		Producer<String,String> producer = new KafkaProducer<>(kafkaProps);
		
		try {
			long startTime = System.nanoTime();
			File file = new File(dataFileLoc);
			Scanner scanner = new Scanner(file);	
			long[] samples = new long[500];
	        int pauseInMicros = 1;
	        
			//Read data line by line
			while(scanner.hasNextLine()) {
				String line;
				line = scanner.nextLine();
				ProducerRecord<String, String> record = new ProducerRecord<>("msg", "key1", line);
				producer.send(record);
				//TimeUnit.MICROSECONDS.sleep(1);
				
				//delay before sending next record
		        for (int i = 0; i < samples.length; i++) {
		            long firstTime = System.nanoTime();
		            busyWaitMicros(pauseInMicros);
		            long timeForNano = System.nanoTime() - firstTime;
		            samples[i] = timeForNano;
		        }
			}
			long endTime = System.nanoTime();
			long duration = (endTime - startTime)/1000000;
			scanner.close();
			System.out.println("finished reading file");
			System.out.println("It took " + duration + "ms to send");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		producer.close();
		
	}


	private static void busyWaitMicros(long micros) {
        long waitUntil = System.nanoTime() + (micros * 1_000);
        while(waitUntil > System.nanoTime()){
            ;
		
        }
	}
}


