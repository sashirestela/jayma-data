package uma.jayma.data.sample;

import uma.jayma.data.dao.annotation.Identifier;

public class Direccion {

	@Identifier
	protected Long id = null;

	protected String nombreVia = null;

	protected String numero = null;

	protected Long rowVersion = null;

	public Direccion() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombreVia() {
		return nombreVia;
	}

	public void setNombreVia(String nombreVia) {
		this.nombreVia = nombreVia;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Long getRowVersion() {
		return rowVersion;
	}

	public void setRowVersion(Long rowVersion) {
		this.rowVersion = rowVersion;
	}
}
