package uma.jayma.data.sample.domain;

import java.util.List;

import uma.jayma.data.annot.Identifier;
import uma.jayma.data.annot.ManyMany;

public class Proyecto {

	@Identifier
	protected Long id = null;

	protected String nombre = null;

	protected Long rowVersion = null;

	@ManyMany(joinEntity="Asignacion", isClass=false)
	protected List<Empleado> empleados = null;

	public Proyecto() {
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

	public Long getRowVersion() {
		return rowVersion;
	}

	public void setRowVersion(Long rowVersion) {
		this.rowVersion = rowVersion;
	}

	public List<Empleado> getEmpleados() {
		return empleados;
	}

	public void setEmpleados(List<Empleado> empleados) {
		this.empleados = empleados;
	}
}
