package net.uninettunouniversity.scrap.message;

public class ExtractingMessage {
	private String htmlId;
	private String sheetId;

	public ExtractingMessage() {
		super();
	}

	public ExtractingMessage(String htmlId, String sheetId) {
		super();
		this.htmlId = htmlId;
		this.sheetId = sheetId;
	}

	public String getHtmlId() {
		return htmlId;
	}

	public void setHtmlId(String htmlId) {
		this.htmlId = htmlId;
	}

	public String getSheetId() {
		return sheetId;
	}

	public void setSheetId(String sheetId) {
		this.sheetId = sheetId;
	}

	@Override
	public String toString() {
		return "ExtractingMessage [htmlId=" + htmlId + ", sheetId=" + sheetId + "]";
	}
	
}
