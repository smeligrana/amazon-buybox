package net.uninettunouniversity.scrap.extract;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

import net.uninettunouniversity.scrap.google.SheetsOperation;

public class XPathRetriever {
	
	private SheetsOperation shOp;
	
	public XPathRetriever() throws GeneralSecurityException, IOException {
		shOp = new SheetsOperation();
	}
	
	public HashMap<String, String> letturaXpath() throws IOException, GeneralSecurityException {
		// carichiamo gli xpath dal foglio di Google
		HashMap<String, String> xpath = new HashMap<>();

		// lettura file xpath
		ValueRange xPath = shOp.getValues("1smTCR0gb5q0Rh0pjc_G1kwXwtYN9pO_11t4gyxlWk-g", "Foglio1!A:B");
		List<List<Object>> values2 = xPath.getValues();
		if (values2 != null) {
			for (int i = 1; i < values2.size(); i++) {
				List<Object> l = values2.get(i);
				xpath.put(l.get(0).toString(), l.get(1).toString());
			}
		}
		return xpath;
	}

}
