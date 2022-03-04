package net.uninettunouniversity.scrap.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import net.uninettunouniversity.scrap.dto.OpzioneProdotto;

public class SheetsOperation extends GoogleOperationAbstract {

	private NetHttpTransport HTTP_TRANSPORT;
	private Sheets service;

	public SheetsOperation() throws GeneralSecurityException, IOException {
		HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getHttpRequestInitializer())
				.setApplicationName(APPLICATION_NAME).build();
	}

	public AppendValuesResponse appendValues(String spreadsheetId, String range, String valueInputOption,
			List<List<Object>> _values) throws IOException, GeneralSecurityException {

		List<List<Object>> values = Arrays.asList(Arrays.asList());
		values = _values;
		ValueRange body = new ValueRange().setValues(values);
		AppendValuesResponse result = service.spreadsheets().values().append(spreadsheetId, range, body)
				.setValueInputOption(valueInputOption).execute();

		return result;
	}

	public UpdateValuesResponse updateValues(String spreadsheetId, String valueInputOption, List<List<Object>> _values,
			String range) throws IOException, GeneralSecurityException {

		List<List<Object>> values = Arrays.asList(Arrays.asList());
		values = _values;
		ValueRange body = new ValueRange().setValues(values);
		UpdateValuesResponse result = service.spreadsheets().values().update(spreadsheetId, range, body)
				.setValueInputOption(valueInputOption).execute();
		return result;
	}

	public ValueRange getValues(String spreadsheetId, String range) throws IOException, GeneralSecurityException {

		ValueRange result = service.spreadsheets().values().get(spreadsheetId, range).execute();

		return result;
	}

	public UpdateValuesResponse updateElement(String spreadsheetId, String valueInputOption, List<List<Object>> _values,
			String range) throws IOException, GeneralSecurityException {

		List<List<Object>> values = Arrays.asList(Arrays.asList());
		values = _values;
		ValueRange body = new ValueRange().setValues(values);
		UpdateValuesResponse result = service.spreadsheets().values().update(spreadsheetId, range, body)
				.setValueInputOption(valueInputOption).execute();
		return result;
	}

	public AppendValuesResponse salvaSuFoglioGoogle(LinkedList<OpzioneProdotto> elencoOpzioni, String data,
			String spreadsheetId, String range) throws IOException, GeneralSecurityException {

		List<List<Object>> values = new ArrayList<>();

		for (OpzioneProdotto o : elencoOpzioni) {
			values.add(Arrays.asList(data, o.getBuyBox(), o.getVisibilityOrder(), o.getCondizione(), o.getVendutoDa(),
					o.getSpeditoDa(), o.getNumValutazioni(), o.getValPositive(), o.getPrezzo(), o.getQtaMinVenduta(),
					o.getPrezzoProdottoVenduto(), o.getTipoSpedizione(), o.getPrezzoSpedizione(), o.getPrezzoTotale(),
					o.getDifferenzaPrezzo(), o.getDifferenzaPrezzoSpedizione(), o.getDifferenzaPrezzoTotale(),
					o.getGiorniConsegnaMin(), o.getGiorniConsegnaMax(), o.getGiorniSpedizione(),
					o.getGiorniConsegnaVelMin(), o.getGiorniConsegnaVelMax(), o.getGiorniSpedizioneVeloce(),
					o.getSpedizioneConsegna(), o.getCondizioneUsato(), o.getStelle(), o.getDeltaConsegna(),
					o.getDeltaNumVal(), o.getDeltaValPos(), o.getRapportoPiuBasso(), o.isFba(), o.isVendAmazon()));
		}
		return appendValues(spreadsheetId, range, "RAW", values);
	}

}
