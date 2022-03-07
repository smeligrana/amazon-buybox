package net.uninettunouniversity.scrap.message;

import net.uninettunouniversity.scrap.dto.Esperimento;

public class BrowsingMessage {
	private Esperimento esperimento;

	public BrowsingMessage() {
	}

	public BrowsingMessage(Esperimento esperimento) {
		super();
		this.esperimento = esperimento;
	}

	public Esperimento getEsperimento() {
		return esperimento;
	}

	public void setEsperimento(Esperimento esperimento) {
		this.esperimento = esperimento;
	}

}
