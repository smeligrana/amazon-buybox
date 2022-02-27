package net.uninettunouniversity.scrap.extract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import net.uninettunouniversity.scrap.message.ExtractingMessage;

@Configuration
public class ExtractProcessor {
	private final Logger logger = LoggerFactory.getLogger(ExtractProcessor.class);

//	@Autowired
//	private KafkaTemplate<Object, Object> template;

	@KafkaListener(id = "extracting", topics = "extracting", concurrency = "1")
	public void listen(ExtractingMessage in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		this.logger.info("Received: {} from {} ", in, topic);
		
		// TODO Per ogni html ricevuto estraggo i dati e li salvo nello sheet specifico
		
	}
}
