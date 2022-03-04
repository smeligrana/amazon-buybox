package net.uninettunouniversity.scrap.dto;

public class Esperimento {

	private int idEsperimento;
	private String nome;
	private String url;
	private String id_sheet_extracted_data;
	private String id_folder_experiment;
	private String id_folder_html;
	private String id_folder_img;

	public int getIdEsperimento() {
		return idEsperimento;
	}

	public void setIdEsperimento(int idEsperimento) {
		this.idEsperimento = idEsperimento;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId_sheet_extracted_data() {
		return id_sheet_extracted_data;
	}

	public void setId_sheet_extracted_data(String id_sheet_extracted_data) {
		this.id_sheet_extracted_data = id_sheet_extracted_data;
	}

	public String getId_folder_experiment() {
		return id_folder_experiment;
	}

	public void setId_folder_experiment(String id_folder_experiment) {
		this.id_folder_experiment = id_folder_experiment;
	}

	public String getId_folder_html() {
		return id_folder_html;
	}

	public void setId_folder_html(String id_folder_html) {
		this.id_folder_html = id_folder_html;
	}

	public String getId_folder_img() {
		return id_folder_img;
	}

	public void setId_folder_img(String id_folder_img) {
		this.id_folder_img = id_folder_img;
	}

	@Override
	public String toString() {
		return idEsperimento + ";" + nome + ";" + url + ";" + id_sheet_extracted_data + ";" + id_folder_experiment + ";"
				+ id_folder_html + ";" + id_folder_img;
	}
}
