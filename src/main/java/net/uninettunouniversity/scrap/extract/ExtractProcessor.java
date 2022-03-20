package net.uninettunouniversity.scrap.extract;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import com.google.api.services.sheets.v4.model.AppendValuesResponse;

import net.uninettunouniversity.scrap.dto.OpzioneProdotto;
import net.uninettunouniversity.scrap.google.SheetsOperation;
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
		LinkedList<OpzioneProdotto> elencoOpzioneProdotto = dataExtractor.eseguiScrap(in.getEsperimento(), in.getDataEstrazione());
		
		SheetsOperation shOp= new SheetsOperation();
		AppendValuesResponse r = shOp.salvaSuFoglioGoogle(elencoOpzioneProdotto, in.getDataEstrazione(), in.getEsperimento().getId_sheet_extracted_data(),
				"Foglio1!A:AF");

		this.logger.info("Aggiunti dati al foglio");
	}
}
