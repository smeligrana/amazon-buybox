package net.uninettunouniversity.scrap.extract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import net.uninettunouniversity.scrap.dto.Esperimento;
import net.uninettunouniversity.scrap.dto.OpzioneProdotto;
import net.uninettunouniversity.scrap.google.DriveOperation;

public class DataExtractor {

	private XPathRetriever xpathRetr;
	private HashMap<String, String> xpath;
	private Document document;
	private final int DATA_PICCOLA = 13;
	private DriveOperation drOp;

	public DataExtractor() throws IOException, GeneralSecurityException {
		xpathRetr = new XPathRetriever();
		xpath = xpathRetr.letturaXpath();
		drOp = new DriveOperation();
	}

	public LinkedList<OpzioneProdotto> eseguiScrap(Esperimento esp, String dataEstrazione)
			throws IOException, GeneralSecurityException {
		String dataAcquisizione = null;
//		if (dataEstrazione.length() == DATA_PICCOLA) {//Esperimento in differita
		dataAcquisizione = dataEstrazione;
		List<com.google.api.services.drive.model.File> lf = drOp.listFiles(esp.getId_folder_html());
		// primo file da esaminare
		com.google.api.services.drive.model.File f = estraiFile(dataEstrazione, lf);

		Scanner s = new Scanner(drOp.getContentFile(f));
		StringBuilder html_ = new StringBuilder();
		while (s.hasNextLine()) {
			html_.append(s.nextLine());
		}
		s.close();
		String html = html_.toString();
		// trasforma in oggetto Document
		document = Jsoup.parse(html);
//		}else {//Esperimento in diretta
//			dataAcquisizione = dataEstrazione.substring(0, dataEstrazione.length() - 6);
//			Browsing br = new Browsing();
//			br.espandiPagina(esp.getUrl());
//			// estrai html
//			String html = br.getHtml();
//			String dataCompatta = dataEstrazione.substring(0, dataEstrazione.length() - 6);
//			// salva html
//			caricaFileHtml(esp.getNome(), esp.getId_folder_html(), html, dataCompatta);
//			// salva screenshot
//			caricaFileScreenShot(esp.getNome(), esp.getId_folder_img(), dataCompatta, br.getDriver());
//			// chiudiamo il browser
//			br.getDriver().close();
//			// trasforma in oggetto Document
//			document = Jsoup.parse(html);
//			
//		}
		int numVend = getNumVenditori();

		LinkedList<OpzioneProdotto> elencoOpzioni = catturaValori(numVend, dataAcquisizione);

		// calcoliamo differenza di prezzo rispetto al minimo

		double prezzoMinore = getPrezzoMin(elencoOpzioni);
		double prezzoSpedizioneMinore = getPrezzoSpedizioneMin(elencoOpzioni);
		double prezzoTotaleMinore = getPrezzoTotaleMin(elencoOpzioni);
		/// calcoliamo i minimi tempi di consegna
		int consegnaPiuVeloce = getConsegnaPiuVeloce(elencoOpzioni);
		// calcoliamo altri valori di riferimento per ottenere i relativi
		int maxNumVal = getMaxValutazioni(elencoOpzioni);
		int maxValPos = getMaxValPositive(elencoOpzioni);
		for (OpzioneProdotto o : elencoOpzioni) {
			o.setDifferenzaPrezzo(o.getPrezzo() - prezzoMinore);
			o.setDifferenzaPrezzoSpedizione(o.getPrezzoSpedizione() - prezzoSpedizioneMinore);
			o.setDifferenzaPrezzoTotale(o.getPrezzoTotale() - prezzoTotaleMinore);
			// calcoliamo delta_consegna
			int consegnaMin = 0;
			if (o.getGiorniConsegnaVelMin() > 0) {
				consegnaMin = o.getGiorniConsegnaVelMin();
			} else {
				consegnaMin = o.getGiorniConsegnaMin();
			}
			o.setDeltaConsegna(consegnaMin - consegnaPiuVeloce);
			o.setDeltaNumVal(maxNumVal - o.getNumValutazioni());
			o.setDeltaValPos(maxValPos - o.getValPositive());
			o.setRapportoPiuBasso(o.getPrezzoTotale() / prezzoTotaleMinore);
		}
		return elencoOpzioni;
	}

	// estraiamo i valori da un file html inserendo la data di estrazione originale
	private LinkedList<OpzioneProdotto> catturaValori(int numVend, String data) {
		LinkedList<OpzioneProdotto> elencoOpzioni = new LinkedList<>();

		for (int i = 0; i <= numVend; i++) {
			OpzioneProdotto op = new OpzioneProdotto();

			Integer buyBox = setBuyBox(i);
			Double pr = getPrezzo(i);
			String speditoDa = getSpeditoDa(i);
			String vendutoDa = getVendutoDa(i);
			String condizione = getCondizione(i);
			String spedizione = getSpedizioneConsegna(i);
			Double stelle = getStelle(i);
			String tipoSpedizione = getTipoSpedizione(spedizione);// gratuita o a pagamento
			Double prezzoSpedizione = getPrezzoSpedizione(tipoSpedizione, spedizione);
			Integer qta_min_venduta = getQtaMin(i);
			Integer numV = getNValutazioni(i);
			Integer valPos = getValutazPos(i);

			op.setBuyBox(buyBox);
			op.setPrezzo(pr);
			op.setStelle(stelle);
			op.setTipoSpedizione(tipoSpedizione);
			op.setPrezzoSpedizione(prezzoSpedizione);
			op.setNumValutazioni(numV);
			op.setValPositive(valPos);
			op.setQtaMinVenduta(qta_min_venduta);
			op.setCondizione(condizione);
			op.setSpedizioneConsegna(spedizione);
			op.setVisibilityOrder(i);

			// tempi consegna normale/veloce
			try {
				// i giorni alla consegna vengono calcolati dal momento della data di
				// caricamento del file html
				estraiTempiConsegna(op, spedizione, data);
			} catch (java.util.NoSuchElementException e) {

			}

			// dati sui prodotti usati
			if (condizione.contains("Usato")) {
				String condizioneUsato = speditoDa;
				String spedizioneUsato = getSpedizioneUSato(i);
				String venditoreUsato = getVenditoreUsato(i);
				op.setCondizioneUsato(condizioneUsato);
				op.setSpeditoDa(spedizioneUsato);
				op.setVendutoDa(venditoreUsato);
			}

			if (condizione.equalsIgnoreCase("nuovo")) {
				op.setSpeditoDa(speditoDa);
				op.setVendutoDa(vendutoDa);
			}

			elencoOpzioni.addLast(op);
		}
		return elencoOpzioni;
	}

	private int getMaxValPositive(LinkedList<OpzioneProdotto> elencoOpzioni) {
		int max = 0;
		for (OpzioneProdotto o : elencoOpzioni) {
			if (o.getValPositive() > max) {
				max = o.getValPositive();
			}
		}
		return max;
	}

	private int getMaxValutazioni(LinkedList<OpzioneProdotto> elencoOpzioni) {
		int max = 0;
		for (OpzioneProdotto o : elencoOpzioni) {
			if (o.getNumValutazioni() > max) {
				max = o.getNumValutazioni();
			}
		}
		return max;
	}

	private int getConsegnaPiuVeloce(LinkedList<OpzioneProdotto> elencoOpzioni) {
		int minConsegna = Integer.MAX_VALUE;
		int minConsegnaVeloce = Integer.MAX_VALUE;
		for (OpzioneProdotto o : elencoOpzioni) {
			if (o.getGiorniConsegnaMin() > 0 && o.getGiorniConsegnaMin() < minConsegna) {
				minConsegna = o.getGiorniConsegnaMin();
			}
			if (o.getGiorniConsegnaVelMin() > 0 && o.getGiorniConsegnaVelMin() < minConsegnaVeloce) {
				minConsegnaVeloce = o.getGiorniConsegnaVelMin();
			}
		}
		if (minConsegnaVeloce < minConsegna) {
			return minConsegnaVeloce;
		} else {
			return minConsegna;
		}

	}

	private void caricaFileScreenShot(String nomeFile, String folderImg, String data, WebDriver driver)
			throws GeneralSecurityException, IOException {
		TakesScreenshot scrShot = ((TakesScreenshot) driver);
		File screen = scrShot.getScreenshotAs(OutputType.FILE);
		File toUp = new File("./img/" + nomeFile + data + ".png");
		screen.renameTo(toUp);
		drOp.upload(toUp, folderImg);
		toUp.delete();
	}

	private void caricaFileHtml(String nomeFile, String folderHtml, String html, String data)
			throws FileNotFoundException, GeneralSecurityException, IOException {
		File ht = new File("./html/" + nomeFile + data + ".html");
		PrintWriter pw = new PrintWriter(ht);
		pw.append(html);
		pw.close();
		drOp.upload(ht, folderHtml);
		ht.delete();
	}

	private com.google.api.services.drive.model.File estraiFile(String data,
			List<com.google.api.services.drive.model.File> lf) {
		com.google.api.services.drive.model.File f = null;
		for (com.google.api.services.drive.model.File fl : lf) {
			if (fl.getName().substring(fl.getName().length() - 18, fl.getName().length() - 5).equalsIgnoreCase(data)) {
				f = fl;
			}
		}
		return f;
	}

	private double getPrezzoTotaleMin(LinkedList<OpzioneProdotto> elencoOpzioni) {
		double res = elencoOpzioni.get(0).getPrezzoTotale();
		for (OpzioneProdotto o : elencoOpzioni) {
			if (o.getPrezzoTotale() < res) {
				res = o.getPrezzoTotale();
			}
		}
		return res;
	}

	private double getPrezzoSpedizioneMin(LinkedList<OpzioneProdotto> elencoOpzioni) {
		double res = elencoOpzioni.get(0).getPrezzoSpedizione();
		for (OpzioneProdotto o : elencoOpzioni) {
			if (o.getPrezzoSpedizione() < res) {
				res = o.getPrezzoSpedizione();
			}
		}
		return res;
	}

	private double getPrezzoMin(LinkedList<OpzioneProdotto> elencoOpzioni) {
		double res = elencoOpzioni.get(0).getPrezzo();
		for (OpzioneProdotto o : elencoOpzioni) {
			if (o.getPrezzo() < res) {
				res = o.getPrezzo();
			}
		}
		return res;
	}

	private String getTipoSpedizione(String spedizione) {
		if (spedizione.contains("GRATUITA")) {
			return "Gratuita";
		} else {
			return "A Pagamento";
		}
	}

	private Double getPrezzoSpedizione(String tipoSpedizione, String spedizione) {
		Double ps = 0.0;

		if (tipoSpedizione.equalsIgnoreCase("Gratuita")) {
			return ps;
		} else {

			Pattern pattern = Pattern.compile("[0-9]+\\,[0-9]{2}");
			Matcher matcher = pattern.matcher(spedizione);
			String estr = null;
			if ((matcher.find())) {
				estr = matcher.group();
			}
			try {
				ps = Double.parseDouble(estr.replace(",", "."));

			} catch (java.lang.NullPointerException ee) {

			}
			return ps;
		}
	}

	private int setBuyBox(int i) {
		if (i == 0) {
			return 1;
		} else {
			return 0;
		}
	}

	private Integer getValutazPos(int i) {
		if (i == 0) {
			return 0;
		}
		String nValutaz = null;
		try {
			nValutaz = document.selectXpath(xpath.get("nValutazioni").replace("*id*", String.valueOf(i))).text();
		} catch (NoSuchElementException e) {

		}
		int valPos = 0;
		if (nValutaz != null) {
			// valutazioni positive
			Pattern pattern2 = Pattern.compile("[0-9]{2,3}\\%");
			Matcher matcher2 = pattern2.matcher(nValutaz);
			String estr2 = null;
			if ((matcher2.find())) {
				estr2 = matcher2.group();
			}
			if (estr2 != null) {
				valPos = Integer.parseInt(estr2.substring(0, estr2.length() - 1));// valutazioni positive
			}
		}
		return valPos;
	}

	private Integer getNValutazioni(int i) {
		if (i == 0) {
			// valutazioni elemento principale nValutazioniBuyBox
			String valPr = document.selectXpath(xpath.get("nValutazioniBuyBox")).text();
			int numV = 0;
			Pattern pattern = Pattern.compile("[0-9]+\\.*[0-9]+");
			Matcher matcher = pattern.matcher(valPr);
			String estr = null;
			if ((matcher.find())) {
				estr = matcher.group();
			}
			estr = estr.replace(".", "");
			try {
				numV = Integer.parseInt(estr);// num Valutazioni
			} catch (NumberFormatException | NoSuchElementException e) {

			}
			return numV;

		} else {
			String nValutaz = document.selectXpath(xpath.get("nValutazioni").replace("*id*", String.valueOf(i))).text();
			int numV = 0;
			StringTokenizer st;
			st = new StringTokenizer(nValutaz, " ");
			try {
				numV = Integer.parseInt(st.nextToken().substring(1));// num Valutazioni
			} catch (NumberFormatException | NoSuchElementException e) {

			}
			return numV;
		}
	}

	private Double getStelle(int i) {
		if (i == 0) {
			double stelle = 0;
			Elements ee = document.selectXpath(xpath.get("stelleBuyBox"));
			Element ele = ee.first();
			String testo = ele.html();

			Pattern patt = Pattern.compile("-[0-9](-[0-9]){0,1}");
			Matcher matc = patt.matcher(testo);
			String estr1 = null;
			if ((matc.find())) {
				estr1 = matc.group();
			}

			if (estr1 != null) {
				estr1 = estr1.substring(1);
				if (estr1.length() > 1 && estr1.charAt(1) == '-') {
					estr1 = estr1.replace('-', '.');
				}

				try {
					stelle = Double.parseDouble(estr1);
				} catch (NumberFormatException | NoSuchElementException e) {

				}

			}
			return stelle;
		} else {
			double stelle = 0;

			Elements el = document.selectXpath(xpath.get("stelleNoBuyBox").replace("*id*", String.valueOf(i)));
			Iterator<Element> it = el.iterator();
			while (it.hasNext()) {
				String daPulire = it.next().html();

				Pattern pattern = Pattern.compile("-[0-9](-[0-9]){0,1}");
				Matcher matcher = pattern.matcher(daPulire);
				String estr = null;
				if ((matcher.find())) {
					estr = matcher.group();
				}
				if (estr != null) {
					estr = estr.substring(1);
					if (estr.length() > 1 && estr.charAt(1) == '-') {
						estr = estr.replace('-', '.');
					}
					try {
						stelle = Double.parseDouble(estr);
					} catch (NumberFormatException | NoSuchElementException e) {

					}

				}

			}
			return stelle;
		}

	}

	private void estraiTempiConsegna(OpzioneProdotto op, String spedizione, String data) {

		// date consegna
		StringTokenizer tok = new StringTokenizer(spedizione, ":");
		tok.nextToken();// eliminiamo il primo token
		String consegnaNormale = tok.nextToken();
		String consegnaVeloce = null;
		if (tok.hasMoreTokens()) {
			consegnaVeloce = tok.nextToken();
		}

		boolean consegnaOk = false;
		boolean consegnaVelOk = false;
		// consegna 1 giorno
		if (consegnaNormale.contains("domani")) {
			op.setGiorniConsegnaMin(1);
			op.setGiorniConsegnaMax(1);
			consegnaOk = true;
		}
		if (consegnaVeloce != null && consegnaVeloce.contains("domani")) {
			op.setGiorniConsegnaVelMin(1);
			op.setGiorniConsegnaVelMax(1);
			consegnaVelOk = true;
		}
		Pattern p = Pattern.compile("[0-9]+ (- [0-9]+){0,1}( )*[a-z]{3}");
		Matcher m = p.matcher(consegnaNormale);
		Matcher m2 = null;
		if (consegnaVeloce != null) {
			m2 = p.matcher(consegnaVeloce);
		}

		if (!consegnaOk) {
			estraiConsegnaNormale(op, m, data);
		}
		if (consegnaVeloce != null && !consegnaVelOk) {
			estraiConsegnaVeloce(op, m2, data);
		}
	}

	private void estraiConsegnaVeloce(OpzioneProdotto op, Matcher m2, String data) {

		Integer anno_x = Integer.parseInt(data.substring(0, 4));
		Integer mese_x = Integer.parseInt(data.substring(5, 7));
		mese_x = mese_x - 1;
		Integer giorno_x = Integer.parseInt(data.substring(8, 10));

		// data corrente aggiornata a mezzanotte
		GregorianCalendar calendar = new GregorianCalendar(anno_x, mese_x, giorno_x);

		azzeraOra(calendar);

		ArrayList<String> dateConsegnaVeloce_ = new ArrayList<>();
		ArrayList<String> dateConsegnaVeloce = new ArrayList<>();

		while ((m2.find())) {
			dateConsegnaVeloce_.add(m2.group());
		}
		// eliminiamo le stringhe che non sono date
		for (String st : dateConsegnaVeloce_) {
			// estraiamo il mese dalla data
			Pattern p1_ = Pattern.compile("[a-z]{3}");
			Matcher m1 = p1_.matcher(st);
			String mese_1 = null;
			if ((m1.find())) {
				mese_1 = m1.group();
			}

			int mese = associaMese(mese_1);
			if (mese != -1) {
				dateConsegnaVeloce.add(st);
			}
		}

		// Consegna veloce

		// data nel formato x-y mese oppure x mese
		if (dateConsegnaVeloce.size() == 1) {
			String d = dateConsegnaVeloce.get(0);
			int anno = calendar.get(Calendar.YEAR);
			int mese = -1;
			int giornoMin = 0;
			int giornoMax = 0;
			// catturiamo il mese
			Pattern p1 = Pattern.compile("[a-z]{3}");
			Matcher m1 = p1.matcher(d);
			String mese_ = null;
			if ((m1.find())) {
				mese_ = m1.group();
			}

			mese = associaMese(mese_);
			if (mese != -1) {// vera data

				// catturiamo il giorno
				if (mese != -1) {
					LinkedList<Integer> giorni = new LinkedList<>();
					Pattern p2 = Pattern.compile("[0-9]+");
					m1 = p2.matcher(d);
					while ((m1.find())) {
						giorni.addLast(Integer.parseInt(m1.group()));
					}
					// data con un giorno
					if (giorni.size() == 1) {
						giornoMin = giorni.getFirst();
						giornoMax = giorni.getFirst();
					}
					// data con 2 giorni dello stesso mese
					if (giorni.size() == 2) {
						giornoMin = giorni.getFirst();
						giornoMax = giorni.getLast();
					}

					Calendar dataMin = new GregorianCalendar(anno, mese, giornoMin);
					Calendar dataMax = new GregorianCalendar(anno, mese, giornoMax);

					long dataMinInMs = dataMin.getTimeInMillis();
					long dataMaxInMs = dataMax.getTimeInMillis();
					long dataCorrente = calendar.getTimeInMillis();
					long timeDiffMin = dataMinInMs - dataCorrente;
					long timeDiffMax = dataMaxInMs - dataCorrente;
					int daysDiffMin = (int) (timeDiffMin / (1000 * 60 * 60 * 24));
					int daysDiffMax = (int) (timeDiffMax / (1000 * 60 * 60 * 24));

					// consegna veloce
					op.setGiorniConsegnaVelMin(daysDiffMin);
					op.setGiorniConsegnaVelMax(daysDiffMax);

				}
			}
		}
		// data nel formato x mese1 y mese2
		else if (dateConsegnaVeloce.size() == 2) {
			String data1 = dateConsegnaVeloce.get(0);
			String data2 = dateConsegnaVeloce.get(1);
			int anno = calendar.get(Calendar.YEAR);
			int mese1 = -1;
			int mese2 = -1;
			int giorno1 = 0;
			int giorno2 = 0;

			// catturiamo il mese 1
			Pattern p1 = Pattern.compile("[a-z]{3}");
			Matcher m1 = p1.matcher(data1);
			String mese_ = null;
			if ((m1.find())) {
				mese_ = m1.group();
			}
			// catturiamo il mese 2
			m1 = p1.matcher(data2);
			String mese2_ = null;
			if ((m1.find())) {
				mese2_ = m1.group();
			}

			mese1 = associaMese(mese_);
			mese2 = associaMese(mese2_);

			// catturiamo il giorno 1
			if (mese1 != -1) {
				Pattern p2 = Pattern.compile("[0-9]+");
				m1 = p2.matcher(data1);
				if ((m1.find())) {
					giorno1 = Integer.parseInt(m1.group());
				}
			}
			// catturiamo il giorno 2
			if (mese2 != -1) {
				Pattern p3 = Pattern.compile("[0-9]+");
				m1 = p3.matcher(data2);

				if ((m1.find())) {
					giorno2 = Integer.parseInt(m1.group());
				}
			}

			Calendar dataMin = new GregorianCalendar(anno, mese1, giorno1);
			Calendar dataMax = new GregorianCalendar(anno, mese2, giorno2);

			long dataMinInMs = dataMin.getTimeInMillis();
			long dataMaxInMs = dataMax.getTimeInMillis();
			long dataCorrente = calendar.getTimeInMillis();
			long timeDiffMin = dataMinInMs - dataCorrente;
			long timeDiffMax = dataMaxInMs - dataCorrente;
			int daysDiffMin = (int) (timeDiffMin / (1000 * 60 * 60 * 24));
			int daysDiffMax = (int) (timeDiffMax / (1000 * 60 * 60 * 24));

			// consegna veloce
			op.setGiorniConsegnaVelMin(daysDiffMin);
			op.setGiorniConsegnaVelMax(daysDiffMax);

		}

	}

	private void estraiConsegnaNormale(OpzioneProdotto op, Matcher m, String data) {

		Integer anno_x = Integer.parseInt(data.substring(0, 4));
		Integer mese_x = Integer.parseInt(data.substring(5, 7));
		mese_x = mese_x - 1;
		Integer giorno_x = Integer.parseInt(data.substring(8, 10));

		// data corrente aggiornata a mezzanotte
		GregorianCalendar calendar = new GregorianCalendar(anno_x, mese_x, giorno_x);
		azzeraOra(calendar);

		LinkedList<String> dateConsegnaNormale = new LinkedList<>();
		while ((m.find())) {
			dateConsegnaNormale.add(m.group());
		}

		// Consegna normale

		// data nel formato x-y mese oppure x mese
		if (dateConsegnaNormale.size() == 1) {
			String d = dateConsegnaNormale.get(0);
			int anno = calendar.get(Calendar.YEAR);
			int mese = -1;
			int giornoMin = 0;
			int giornoMax = 0;
			// catturiamo il mese
			Pattern p1 = Pattern.compile("[a-z]{3}");
			Matcher m1 = p1.matcher(d);
			String mese_ = null;
			if ((m1.find())) {
				mese_ = m1.group();
			}

			mese = associaMese(mese_);

			// catturiamo il giorno

			LinkedList<Integer> giorni = new LinkedList<>();
			Pattern p2 = Pattern.compile("[0-9]+");
			m1 = p2.matcher(d);
			while ((m1.find())) {
				giorni.addLast(Integer.parseInt(m1.group()));
			}
			// data con un giorno
			if (giorni.size() == 1) {
				giornoMin = giorni.getFirst();
				giornoMax = giorni.getFirst();
			}
			// data con 2 giorni dello stesso mese
			if (giorni.size() == 2) {
				giornoMin = giorni.getFirst();
				giornoMax = giorni.getLast();
			}
			// calcoliamo la differenza tra la data corrente e la/le date trovate
			Calendar dataMin = new GregorianCalendar(anno, mese, giornoMin);
			Calendar dataMax = new GregorianCalendar(anno, mese, giornoMax);

			// date in millisecondi
			long dataMinInMs = dataMin.getTimeInMillis();
			long dataMaxInMs = dataMax.getTimeInMillis();

			long dataCorrente = calendar.getTimeInMillis();
			// differenza tra le date
			long timeDiffMin = dataMinInMs - dataCorrente;
			long timeDiffMax = dataMaxInMs - dataCorrente;
			int daysDiffMin = (int) (timeDiffMin / (1000 * 60 * 60 * 24));
			int daysDiffMax = (int) (timeDiffMax / (1000 * 60 * 60 * 24));

			// consegna normale
			op.setGiorniConsegnaMin(daysDiffMin);
			op.setGiorniConsegnaMax(daysDiffMax);

		}
		// data nel formato x mese1 y mese2
		else if (dateConsegnaNormale.size() == 2) {
			String data1 = dateConsegnaNormale.get(0);
			String data2 = dateConsegnaNormale.get(1);
			int anno = calendar.get(Calendar.YEAR);
			int mese1 = -1;
			int mese2 = -1;
			int giorno1 = 0;
			int giorno2 = 0;

			// catturiamo il mese 1
			Pattern p1 = Pattern.compile("[a-z]{3}");
			Matcher m1 = p1.matcher(data1);
			String mese_ = null;
			if ((m1.find())) {
				mese_ = m1.group();
			}
			// catturiamo il mese 2
			m1 = p1.matcher(data2);
			String mese2_ = null;
			if ((m1.find())) {
				mese2_ = m1.group();
			}

			mese1 = associaMese(mese_);
			mese2 = associaMese(mese2_);

			// catturiamo il giorno 1
			if (mese1 != -1) {
				Pattern p2 = Pattern.compile("[0-9]+");
				m1 = p2.matcher(data1);
				if ((m1.find())) {
					giorno1 = Integer.parseInt(m1.group());
				}
			}
			// catturiamo il giorno 2
			if (mese2 != -1) {
				Pattern p3 = Pattern.compile("[0-9]+");
				m1 = p3.matcher(data2);

				if ((m1.find())) {
					giorno2 = Integer.parseInt(m1.group());
				}
			}

			// data con un giorno

			Calendar dataMin = new GregorianCalendar(anno, mese1, giorno1);
			Calendar dataMax = new GregorianCalendar(anno, mese2, giorno2);

			long dataMinInMs = dataMin.getTimeInMillis();
			long dataMaxInMs = dataMax.getTimeInMillis();
			long dataCorrente = calendar.getTimeInMillis();
			long timeDiffMin = dataMinInMs - dataCorrente;
			long timeDiffMax = dataMaxInMs - dataCorrente;
			int daysDiffMin = (int) (timeDiffMin / (1000 * 60 * 60 * 24));
			int daysDiffMax = (int) (timeDiffMax / (1000 * 60 * 60 * 24));

			// consegna normale
			op.setGiorniConsegnaMin(daysDiffMin);
			op.setGiorniConsegnaMax(daysDiffMax);

		}

	}

	private void azzeraOra(GregorianCalendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	private int associaMese(String mese_) {
		int mese;
		switch (mese_) {
		case "gen":
			mese = 0;
			break;
		case "feb":
			mese = 1;
			break;
		case "mar":
			mese = 2;
			break;
		case "apr":
			mese = 3;
			break;
		case "mag":
			mese = 4;
			break;
		case "giu":
			mese = 5;
			break;
		case "lug":
			mese = 6;
			break;
		case "ago":
			mese = 7;
			break;
		case "set":
			mese = 8;
			break;
		case "ott":
			mese = 9;
			break;
		case "nov":
			mese = 10;
			break;
		case "dic":
			mese = 11;
			break;
		default:
			mese = -1;
			break;
		}
		return mese;
	}

	private Integer getQtaMin(int i) {
		String s = document.selectXpath(xpath.get("qta_min_venduta").replace("*id*", String.valueOf(i))).text();
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(s);
		String estr = "1";
		if ((matcher.find())) {
			estr = matcher.group();
		}
		return Integer.parseInt(estr);
	}

	private String getVenditoreUsato(int i) {
		String r = document.selectXpath(xpath.get("venditoreUsato").replace("*id*", String.valueOf(i))).text();
		if (r.equalsIgnoreCase("")) {
			String st = null;
			Elements el = document.selectXpath(xpath.get("venditoreUsato2").replace("*id*", String.valueOf(i)));
			for (org.jsoup.nodes.Element e : el) {
				st = e.html();
			}
			if (st == "" || st == null) {
				Element el2 = document.selectXpath(xpath.get("venditoreUsato3")).get(0);

				st = el2.html();

				return st;
			}
			return st;
		}
		return r;
	}

	private String getSpedizioneUSato(int i) {
		String r = document.selectXpath(xpath.get("spedizioneUsato").replace("*id*", String.valueOf(i))).text();
		if (r.equalsIgnoreCase("")) {
			r = document.selectXpath(xpath.get("spedizioneUsato2").replace("*id*", String.valueOf(i))).text();
		}
		if (r.equalsIgnoreCase("")) {
			r = document.selectXpath(xpath.get("spedizioneUsato3")).get(0).text();
		}
		return r;
	}

	/*
	 * private String getCondizioneUsato(int i) { return
	 * document.selectXpath(xpath.get("condizioneUsato").replace("*id*",
	 * String.valueOf(i))).text(); }
	 */
	private String getSpedizioneConsegna(int i) {
		if (i == 0) {
			org.jsoup.nodes.Element e = document.getElementById("unified-delivery-message-");
			if (e != null) {
				return e.text();
			} else {
				return document.getElementById(xpath.get("SpedizioneBuyBoxVuoto")).text();
			}
		} else {
			Elements el = document.select(xpath.get("spedizione").replace("*id*", String.valueOf((i - 1) % 10)));
			Iterator<org.jsoup.nodes.Element> it = el.iterator();
			int index = (i - 1) / 10;
			String e = null;
			while (it.hasNext() && index >= 0) {
				e = it.next().text();
				index--;
			}
			return e;
		}
	}

	private String getCondizione(int i) {
		if (i == 0) {
			String r = document.selectXpath(xpath.get("condizioneBuyBox")).text();
			if (r.equalsIgnoreCase("")) {
				String r1 = document.selectXpath(xpath.get("condizioneBuyBoxVuoto")).html();
				if (r1 != null && r1.length() > 0) {
					r1 = r1.substring(0, r1.length() - 1);
				} else {
					r1 = "Nuovo";// nuovo di default
				}
				return r1;
			} else {
				return r;
			}
		} else {
			return document.selectXpath(xpath.get("condizione").replace("*id*", String.valueOf(i))).text();
		}
	}

	private String getVendutoDa(int i) {
		String venditore = null;
		if (i == 0) {
			venditore = document.selectXpath(xpath.get("vendutoDaBuyBox")).text();
			// amazon non � in prima posizione
			if (venditore == null || venditore.equalsIgnoreCase("")) {
				StringTokenizer strT = new StringTokenizer(
						document.selectXpath(xpath.get("venditoreTerzoBuyBox")).text());
				venditore = strT.nextToken();
			}
			if (venditore == null || venditore.equalsIgnoreCase("")) {
				venditore = document.selectXpath(xpath.get("venditoreBuyBoxVuoto")).html();
			}
		} else {
			try {
				venditore = document.selectXpath(xpath.get("vendutoDaNoBuyBox1").replace("*id*", String.valueOf(i)))
						.text();
			} catch (NoSuchElementException e) {
				venditore = document.selectXpath(xpath.get("vendutoDaNoBuyBox2").replace("*id*", String.valueOf(i)))
						.text();
			}
			// amazon non in prima posizione
			if (venditore == null || venditore.equalsIgnoreCase("")) {
				venditore = document.selectXpath(xpath.get("venditoreAmazonNoBuybox")).text();
			}
		}

		return venditore;
	}

	private String getSpeditoDa(int i) {
		String speditoDa = null;
		if (i == 0) {
			speditoDa = document.selectXpath(xpath.get("speditoDaBuyBox")).text();
		} else {
			speditoDa = document.selectXpath(xpath.get("speditoDaNoBuyBox").replace("*id*", String.valueOf(i))).text();
		}
		if (speditoDa == null || speditoDa.equalsIgnoreCase("")) {
			speditoDa = document.selectXpath(xpath.get("speditoDaBuyBoxVuoto")).html();
		}
		return speditoDa;
	}

	private Double getPrezzo(int i) {
		String prezzoIntero = null;
		String frazione = null;
		if (i == 0) {
			prezzoIntero = document.selectXpath(xpath.get("prezzoInteroBuyBox")).text();
			if (prezzoIntero.equalsIgnoreCase("")) {
				prezzoIntero = document.selectXpath(xpath.get("prezzoInteroBuyBox2")).text();
			}
			frazione = document.selectXpath(xpath.get("frazioneBuyBox")).text();
			if (frazione.equalsIgnoreCase("")) {
				frazione = document.selectXpath(xpath.get("frazioneBuyBox2")).text();
			}
		} else {
			prezzoIntero = document.selectXpath(xpath.get("prezzoInteroNoBuyBox").replace("*id*", String.valueOf(i)))
					.text();
			frazione = document.selectXpath(xpath.get("frazioneNoBuyBox").replace("*id*", String.valueOf(i))).text();
		}
		if (prezzoIntero.length() == 0) {
			String p = document.selectXpath(xpath.get("prezzoPaginaPrincipale")).text();
			p = p.substring(0, p.length() - 1);
			p = p.replace(",", ".");
			return Double.parseDouble(p);
		}
		Double pr = Double.parseDouble(prezzoIntero.substring(0, prezzoIntero.length() - 1));
		Double fr = Double.parseDouble(frazione);
		fr = fr / 100;
		pr = pr + fr;
		return pr;
	}

	private int getNumVenditori() {
		String cont = document.selectXpath(xpath.get("numVenditori")).text();
		StringTokenizer st = new StringTokenizer(cont, " ");
		return Integer.parseInt(st.nextToken());
	}
}
