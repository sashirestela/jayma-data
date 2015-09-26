package uma.jayma.data.sample;

import uma.jayma.data.dao.annotation.Identifier;
import uma.jayma.data.dao.annotation.ManyToOne;

public class Empleado {
	
	@Identifier
	protected Long id = null;
	
	protected String nombre = null;
	
	protected String apellido = null;
	
	protected Long rowVersion = null;
	
	@ManyToOne(selfJoinColumn="idOficina")
	protected Oficina oficina = null;

	public Empleado() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public Long getRowVersion() {
		return rowVersion;
	}

	public void setRowVersion(Long rowVersion) {
		this.rowVersion = rowVersion;
	}

	public Oficina getOficina() {
		return oficina;
	}

	public void setOficina(Oficina oficina) {
		this.oficina = oficina;
	}
}
