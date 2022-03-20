package net.uninettunouniversity.scrap.extract;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
	public void listen(ExtractingMessage in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
			throws GeneralSecurityException, IOException {

		this.logger.info("Received: {} from {} ", in, topic);

		DataExtractor dataExtractor = new DataExtractor();
		dataExtractor.eseguiScrap(in.getEsperimento(), in.getDataEstrazione());

	}
}
