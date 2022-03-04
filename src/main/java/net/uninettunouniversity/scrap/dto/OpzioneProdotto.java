package net.uninettunouniversity.scrap.dto;

public class OpzioneProdotto {
	private String nomeProdotto;
	private double prezzo;
	private String speditoDa = " ";
	private String vendutoDa = " ";
	private int numValutazioni;
	private int valPositive;
	private String condizione = " ";
	private String spedizioneConsegna;
	private String condizioneUsato = " ";
	private int qtaMinVenduta = 1;
	private String tipoSpedizione;
	private double prezzoSpedizione;
	private int giorniConsegnaMin;
	private int giorniConsegnaMax;
	private int giorniConsegnaVelMin;
	private int giorniConsegnaVelMax;
	private double differenzaPrezzo;
	private double differenzaPrezzoSpedizione;
	private double differenzaPrezzoTotale;
	private int buyBox;
	private int visibilityOrder;
	private double stelle;
	private double rapportoPiuBasso;
	private boolean fba;//spedito da Amazon
	private boolean VendAmazon;
	//differenza tra il tempo consegna (num. giorni) minore della rilevazione e il tempo consegna minore dell'opzione
	//pi� � piccolo e meglio �
	private int deltaConsegna;
	//differenza tra il massimo numero di valutazioni e quello corrente
	//pi� � piccolo e meglio �
	private int deltaNumVal;
	//differenza tra la percentuale massima di valutazioni positive e quella corrente
	private int deltaValPos;
	

	public double getPrezzoProdottoVenduto() {
		return this.prezzo * this.qtaMinVenduta;
	}

	public double getPrezzoTotale() {
		return Math.floor(Double.sum(this.getPrezzoProdottoVenduto(), this.prezzoSpedizione) * 100) / 100;
	}

	public String getNomeProdotto() {
		return nomeProdotto;
	}

	public void setNomeProdotto(String nomeProdotto) {
		this.nomeProdotto = nomeProdotto;
	}

	public double getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(double prezzo) {
		this.prezzo = prezzo;
	}

	public String getSpeditoDa() {
		return speditoDa;
	}

	public void setSpeditoDa(String speditoDa) {
		this.speditoDa = speditoDa;
		if(speditoDa.equalsIgnoreCase("amazon")) {
			this.fba = true;
		}else {
			this.fba = false;
		}
	}

	public String getVendutoDa() {
		return vendutoDa;
	}

	public void setVendutoDa(String venditore) {
		this.vendutoDa = venditore;
		if(venditore.equalsIgnoreCase("amazon")) {
			this.VendAmazon = true;
		}else {
			this.VendAmazon = false;
		}
	}

	public int getNumValutazioni() {
		return numValutazioni;
	}

	public void setNumValutazioni(int numValutazioni) {
		this.numValutazioni = numValutazioni;
	}

	public int getValPositive() {
		return valPositive;
	}

	public void setValPositive(int valPositive) {
		this.valPositive = valPositive;
	}

	public String getCondizione() {
		return condizione;
	}

	public void setCondizione(String condizione) {
		this.condizione = condizione;
	}

	@Override
	public String toString() {
		return this.getBuyBox() + ";" + this.getVisibilityOrder() + ";" + this.getCondizione() + ";"
				+ this.getVendutoDa() + ";" + this.getSpeditoDa() + ";" + this.getNumValutazioni() + ";"
				+ this.getValPositive() + ";" + this.getPrezzo() + ";" + this.getQtaMinVenduta() + ";"
				+ this.getPrezzoProdottoVenduto() + ";" + this.getTipoSpedizione() + ";" + this.getPrezzoSpedizione()
				+ ";" + this.getPrezzoTotale() + ";" + this.getDifferenzaPrezzo() + ";"
				+ this.getDifferenzaPrezzoSpedizione() + ";" + this.getDifferenzaPrezzoTotale() + ";"
				+ this.getGiorniConsegnaMin() + ";" + this.getGiorniConsegnaMax() + ";" + this.getGiorniSpedizione()
				+ ";" + this.getGiorniConsegnaVelMin() + ";" + this.getGiorniConsegnaVelMax() + ";"
				+ this.getGiorniSpedizioneVeloce() + ";" + this.getSpedizioneConsegna() + ";"
				+ this.getCondizioneUsato() + ";";
	}

	public String getSpedizioneConsegna() {
		return spedizioneConsegna;
	}

	public void setSpedizioneConsegna(String spedizione) {
		this.spedizioneConsegna = spedizione;
	}

	public String getCondizioneUsato() {
		return condizioneUsato;
	}

	public void setCondizioneUsato(String condizioneUsato) {
		this.condizioneUsato = condizioneUsato;
	}

	public int getQtaMinVenduta() {
		return qtaMinVenduta;
	}

	public void setQtaMinVenduta(int qtaMinVenduta) {
		this.qtaMinVenduta = qtaMinVenduta;
	}

	public String getTipoSpedizione() {
		return tipoSpedizione;
	}

	public void setTipoSpedizione(String tipoSpedizione) {
		this.tipoSpedizione = tipoSpedizione;
	}

	public double getPrezzoSpedizione() {
		return prezzoSpedizione;
	}

	public void setPrezzoSpedizione(double prezzoSpedizione) {
		this.prezzoSpedizione = prezzoSpedizione;
	}

	public int getGiorniConsegnaMin() {
		return giorniConsegnaMin;
	}

	public void setGiorniConsegnaMin(int giorniConsegnaMin) {
		this.giorniConsegnaMin = giorniConsegnaMin;
	}

	public int getGiorniConsegnaMax() {
		return giorniConsegnaMax;
	}

	public void setGiorniConsegnaMax(int giorniConsegnaMax) {
		this.giorniConsegnaMax = giorniConsegnaMax;
	}

	public int getGiorniConsegnaVelMin() {
		return giorniConsegnaVelMin;
	}

	public void setGiorniConsegnaVelMin(int giorniConsegnaVelMin) {
		this.giorniConsegnaVelMin = giorniConsegnaVelMin;
	}

	public int getGiorniConsegnaVelMax() {
		return giorniConsegnaVelMax;
	}

	public void setGiorniConsegnaVelMax(int giorniConsegnaVelMax) {
		this.giorniConsegnaVelMax = giorniConsegnaVelMax;
	}

	public double getDifferenzaPrezzo() {
		return Math.floor(differenzaPrezzo * 100) / 100;
	}

	public void setDifferenzaPrezzo(double differenzaPrezzo) {
		this.differenzaPrezzo = differenzaPrezzo;
	}

	public double getDifferenzaPrezzoSpedizione() {
		return Math.floor(differenzaPrezzoSpedizione * 100) / 100;
	}

	public void setDifferenzaPrezzoSpedizione(double differenzaPrezzoSpedizione) {
		this.differenzaPrezzoSpedizione = differenzaPrezzoSpedizione;
	}

	public double getDifferenzaPrezzoTotale() {
		return Math.floor(differenzaPrezzoTotale * 100) / 100;
	}

	public void setDifferenzaPrezzoTotale(double differenzaPrezzoTotale) {
		this.differenzaPrezzoTotale = differenzaPrezzoTotale;
	}

	public int getGiorniSpedizioneVeloce() {
		return this.giorniConsegnaVelMax - this.giorniConsegnaVelMin;
	}

	public int getGiorniSpedizione() {
		return this.giorniConsegnaMax - this.giorniConsegnaMin;
	}

	public int getBuyBox() {
		return buyBox;
	}

	public void setBuyBox(int buyBox) {
		this.buyBox = buyBox;
	}

	public int getVisibilityOrder() {
		return visibilityOrder;
	}

	public void setVisibilityOrder(int visibilityOrder) {
		this.visibilityOrder = visibilityOrder;
	}

	public double getStelle() {
		return stelle;
	}

	public void setStelle(double stelle) {
		this.stelle = stelle;
	}

	public int getDeltaConsegna() {
		return deltaConsegna;
	}

	public void setDeltaConsegna(int deltaConsegna) {
		this.deltaConsegna = deltaConsegna;
	}

	public int getDeltaNumVal() {
		return deltaNumVal;
	}

	public void setDeltaNumVal(int deltaNumVal) {
		this.deltaNumVal = deltaNumVal;
	}

	public int getDeltaValPos() {
		return deltaValPos;
	}

	public void setDeltaValPos(int deltaValPos) {
		this.deltaValPos = deltaValPos;
	}

	public double getRapportoPiuBasso() {
		return Math.floor(rapportoPiuBasso * 100) / 100;
	}

	public void setRapportoPiuBasso(double rapportoPiuBasso) {
		this.rapportoPiuBasso = rapportoPiuBasso;
	}

	public boolean isFba() {
		return fba;
	}

	public boolean isVendAmazon() {
		return VendAmazon;
	}

}
