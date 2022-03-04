package net.uninettunouniversity.scrap.productslist;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import net.uninettunouniversity.scrap.dto.Esperimento;
import net.uninettunouniversity.scrap.google.URLRetriever;
import net.uninettunouniversity.scrap.message.BrowsingMessage;

@Configuration
public class ProductListProcessor {
	private final Logger logger = LoggerFactory.getLogger(ProductListProcessor.class);

	@Autowired
	private KafkaTemplate<Object, Object> template;

	@KafkaListener(id = "productslist", topics = "productslist", concurrency = "1")
	public void listen(String in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
			throws GeneralSecurityException, IOException {

		this.logger.info("Received: {} from {} ", in, topic);

		URLRetriever urlRetriever;
		urlRetriever = new URLRetriever();
		List<Esperimento> prodotti = urlRetriever.leggiFileConfig("1W8UeooEtPwf3tykbbfe0JPIiu6UsNQz1Qic1hn2IVL8");

		prodotti.forEach((p)->{
			logger.info(p.toString());
			
			BrowsingMessage message = new BrowsingMessage(p.getUrl(), p.getId_folder_experiment());
			this.template.send("browsing", message);
		});
		
	}
}
