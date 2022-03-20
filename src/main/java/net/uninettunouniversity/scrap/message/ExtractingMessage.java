package net.uninettunouniversity.scrap.message;

import net.uninettunouniversity.scrap.dto.Esperimento;

public class ExtractingMessage {
	private Esperimento esperimento;

	public ExtractingMessage() {
		super();
	}

	public ExtractingMessage(Esperimento esperimento) {
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
