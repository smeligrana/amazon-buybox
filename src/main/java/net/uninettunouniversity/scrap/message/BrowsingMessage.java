package net.uninettunouniversity.scrap.message;

public class BrowsingMessage {
	private String url;
	private String folderid;

	public BrowsingMessage() {
	}

	public BrowsingMessage(String url, String folderid) {
		super();
		this.url = url;
		this.folderid = folderid;
	}

	public String getUrl() {
		return url;
	}

	public String getFolderid() {
		return folderid;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setFolderid(String folderid) {
		this.folderid = folderid;
	}

	@Override
	public String toString() {
		return "BrowsingMessage [url=" + url + ", folderid=" + folderid + "]";
	}

}
