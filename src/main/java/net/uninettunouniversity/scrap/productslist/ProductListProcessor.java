package net.uninettunouniversity.scrap.productslist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import net.uninettunouniversity.scrap.message.BrowsingMessage;

@Configuration
public class ProductListProcessor {
	private final Logger logger = LoggerFactory.getLogger(ProductListProcessor.class);
	
	@Autowired
	private KafkaTemplate<Object, Object> template;

	@KafkaListener(id = "productslist", topics = "productslist", concurrency = "1")
	public void listen(String in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

		this.logger.info("Received: {} from {} ", in, topic);
		
		// TODO Leggo il foglio identificato da id e per ogni riga nel foglio
		// sottometto una richiesta per attivare la procedura di browsing

		BrowsingMessage message = new BrowsingMessage( "https://....", "id folder");
		this.template.send("browsing", message);
	}
}
