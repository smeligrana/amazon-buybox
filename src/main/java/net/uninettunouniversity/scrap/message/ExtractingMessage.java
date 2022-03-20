package net.uninettunouniversity.scrap.message;

import net.uninettunouniversity.scrap.dto.Esperimento;

public class ExtractingMessage {
	private Esperimento esperimento;
	private String dataEstrazione;

	public ExtractingMessage() {
		super();
	}

	public ExtractingMessage(Esperimento esperimento, String dataEstrazione) {
		super();
		this.esperimento = esperimento;
		this.dataEstrazione = dataEstrazione;
	}

	public Esperimento getEsperimento() {
		return esperimento;
	}

	public void setEsperimento(Esperimento esperimento) {
		this.esperimento = esperimento;
	}

	public String getDataEstrazione() {
		return dataEstrazione;
	}

	public void setDataEstrazione(String dataEstrazione) {
		this.dataEstrazione = dataEstrazione;
	}
	
	

}
