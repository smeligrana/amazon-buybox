package net.uninettunouniversity.scrap.browsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import net.uninettunouniversity.scrap.google.DriveOperation;
import net.uninettunouniversity.scrap.message.BrowsingMessage;
import net.uninettunouniversity.scrap.message.ExtractingMessage;

@Configuration
public class BrowsingProcessor {
	private final Logger logger = LoggerFactory.getLogger(BrowsingProcessor.class);

	@Autowired
	private KafkaTemplate<Object, Object> template;

	@KafkaListener(id = "browsing", topics = "browsing", concurrency = "1")
	public void listen(BrowsingMessage in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
			throws GeneralSecurityException, IOException {

		this.logger.info("Received: {} from {} ", in, topic);

		DriveOperation driveOperation = new DriveOperation();

		Browsing browsing = new Browsing();
		browsing.espandiPagina(in.getEsperimento().getUrl());

		String html = browsing.getHtml();
		String dataCompatta = DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss");
		dataCompatta = dataCompatta.replace(" ", "").replace("\'", "").replace(":", "");
		caricaFileHtml(in.getEsperimento().getNome(), in.getEsperimento().getId_folder_html(), html, dataCompatta,
				driveOperation);
		caricaFileScreenShot(in.getEsperimento().getNome(), in.getEsperimento().getId_folder_img(), dataCompatta,
				browsing.getDriver(), driveOperation);

		browsing.getDriver().close();

		ExtractingMessage message = new ExtractingMessage(in.getEsperimento(), dataCompatta);
		this.template.send("extracting", message);
	}

	private void caricaFileScreenShot(String nomeFile, String folderImg, String data, WebDriver driver,
			DriveOperation driveOperation) throws GeneralSecurityException, IOException {
		String fn =  nomeFile.replace(" ", "").replace("\'", "").replace(":", "");
		fn += data.replace(" ", "").replace("\'", "").replace(":", "");
		
		TakesScreenshot scrShot = ((TakesScreenshot) driver);
		File screen = scrShot.getScreenshotAs(OutputType.FILE);
		File toUp = new File("./img/" +fn + ".png");
		screen.renameTo(toUp);
		driveOperation.upload(toUp, folderImg);
		toUp.delete();
	}

	private void caricaFileHtml(String nomeFile, String folderHtml, String html, String data,
			DriveOperation driveOperation) throws FileNotFoundException, GeneralSecurityException, IOException {
		
		String fn =  nomeFile.replace(" ", "").replace("\'", "").replace(":", "");
		fn += data.replace(" ", "").replace("\'", "").replace(":", "");
		
		File ht = new File("./html/" + fn + ".html");
		PrintWriter pw = new PrintWriter(ht);
		pw.append(html);
		pw.close();
		driveOperation.upload(ht, folderHtml);
		ht.delete();
	}
}
