package net.uninettunouniversity.scrap.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

import net.uninettunouniversity.scrap.dto.Esperimento;

public class URLRetriever {

	private SheetsOperation shOp;

	public URLRetriever() throws GeneralSecurityException, IOException {
		shOp = new SheetsOperation();
	}

	public LinkedList<Esperimento> leggiFileConfig(String idFoglioConfig) throws IOException, GeneralSecurityException {
		// lettura file configurazione
		ValueRange result = shOp.getValues(idFoglioConfig, "Foglio1!A:G");

		LinkedList<Esperimento> prodotti = new LinkedList<>();

		List<List<Object>> values = result.getValues();
		if (values != null) {
			for (int i = 1; i < values.size(); i++) {
				Esperimento e = new Esperimento();
				List<Object> l = values.get(i);
				e.setIdEsperimento(Integer.parseInt(l.get(0).toString()));
				e.setNome(l.get(1).toString());
				e.setUrl(l.get(2).toString());
				e.setId_sheet_extracted_data(l.get(3).toString());
				e.setId_folder_experiment(l.get(4).toString());
				e.setId_folder_html(l.get(5).toString());
				e.setId_folder_img(l.get(6).toString());
				prodotti.add(e);
			}
		}
		return prodotti;
	}

}
