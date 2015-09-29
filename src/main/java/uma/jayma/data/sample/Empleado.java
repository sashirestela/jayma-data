package uma.jayma.data.sample;

import java.util.List;

import uma.jayma.data.dao.annotation.Identifier;
import uma.jayma.data.dao.annotation.ManyToMany;
import uma.jayma.data.dao.annotation.ManyToOne;
import uma.jayma.data.dao.annotation.OneToOne;

public class Empleado {
	
	@Identifier
	protected Long id = null;
	
	protected String nombre = null;
	
	protected String apellido = null;
	
	protected Long rowVersion = null;
	
	@ManyToOne(selfJoinColumn="idOficina")
	protected Oficina oficina = null;
	
	@OneToOne(joinColumn="idEmpleado", selfDriven=false)
	protected Direccion direccion = null;
	
	@ManyToMany(joinEntity="Asignacion", isClass=false)
	protected List<Proyecto> proyectos = null;

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

	public Direccion getDireccion() {
		return direccion;
	}

	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
	}

	public List<Proyecto> getProyectos() {
		return proyectos;
	}

	public void setProyectos(List<Proyecto> proyectos) {
		this.proyectos = proyectos;
	}
}
