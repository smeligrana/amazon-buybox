package net.uninettunouniversity.scrap.browsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import net.uninettunouniversity.scrap.message.BrowsingMessage;
import net.uninettunouniversity.scrap.message.ExtractingMessage;

@Configuration
public class BrowsingProcessor {
	private final Logger logger = LoggerFactory.getLogger(BrowsingProcessor.class);

	@Autowired
	private KafkaTemplate<Object, Object> template;

	@KafkaListener(id = "browsing", topics = "browsing", concurrency = "1")
	public void listen(BrowsingMessage in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		this.logger.info("Received: {} from {} ", in, topic);
		
		// TODO Per ogni url ricevuto effetuo il browsing e salvo png e html nella cartella specificata

		ExtractingMessage message = new ExtractingMessage( "id html", "id sheet");
		this.template.send("extracting", message);
	}
}
