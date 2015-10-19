package uma.jayma.data.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import uma.jayma.data.sample.domain.Direccion;
import uma.jayma.data.sample.domain.DireccionDao;
import uma.jayma.data.sample.domain.DireccionDaoImpl;
import uma.jayma.data.sample.domain.Empleado;
import uma.jayma.data.sample.domain.EmpleadoDao;
import uma.jayma.data.sample.domain.EmpleadoDaoImpl;
import uma.jayma.data.sample.domain.Oficina;
import uma.jayma.data.sample.domain.OficinaDao;
import uma.jayma.data.sample.domain.OficinaDaoImpl;
import uma.jayma.data.sample.domain.Proyecto;
import uma.jayma.data.sample.domain.ProyectoDao;
import uma.jayma.data.sample.domain.ProyectoDaoImpl;
import uma.jayma.data.util.Util;

public class App 
{
	public static void main( String[] args )
	{
		Connection conn = null;
		
		final String dbDrv = "com.mysql.jdbc.Driver";
		final String dbUrl = "jdbc:mysql://sql3.freemysqlhosting.net:3306/sql391462";
		final String dbUsr = "sql391462";
		final String dbPwd = "cL4!mQ1*";
		
//		final String dbDrv = "com.mysql.jdbc.Driver";
//		final String dbUrl = "jdbc:mysql://localhost:3306/jayma";
//		final String dbUsr = "root";
//		final String dbPwd = "root";

		try {
			Class.forName(dbDrv);
			conn = DriverManager.getConnection(dbUrl, dbUsr, dbPwd);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			conn.setAutoCommit(false);

			// Creación de DAOs

			OficinaDao ofiDao = new OficinaDaoImpl();
			ofiDao.setConnection(conn);
			ProyectoDao proDao = new ProyectoDaoImpl();
			proDao.setConnection(conn);
			EmpleadoDao empDao = new EmpleadoDaoImpl();
			empDao.setConnection(conn);
			DireccionDao dirDao = new DireccionDaoImpl();
			dirDao.setConnection(conn);

			// Seteo de Oficinas

			Oficina ofi1 = new Oficina();
			ofi1.setNombre("Desarrollo");
			ofi1.setRowVersion(0L);

			Oficina ofi2 = new Oficina();
			ofi2.setNombre("Analisis");
			ofi2.setRowVersion(0L);

			ofi1.setId(ofiDao.create(ofi1));
			ofi2.setId(ofiDao.create(ofi2));

			List<Oficina> ofiList = ofiDao.selectAll();
			System.out.println("\nOficinas");
			for (Oficina ofi : ofiList) {
				System.out.println(Util.toString(ofi));
			}

			// Seteo de Proyectos

			Proyecto pro1 = new Proyecto();
			pro1.setNombre("Alfa");
			pro1.setRowVersion(0L);

			Proyecto pro2 = new Proyecto();
			pro2.setNombre("Beta");
			pro2.setRowVersion(0L);

			Proyecto pro3 = new Proyecto();
			pro3.setNombre("Omega");
			pro3.setRowVersion(0L);

			pro1.setId(proDao.create(pro1));
			pro2.setId(proDao.create(pro2));
			pro3.setId(proDao.create(pro3));

			List<Proyecto> proList = proDao.selectAll();
			System.out.println("\nProyectos");
			for (Proyecto pro : proList) {
				System.out.println(Util.toString(pro));
			}			

			// Seteo de Empleados

			Empleado emp1 = new Empleado();
			emp1.setNombre("Sashir");
			emp1.setApellido("Estela");
			emp1.setRowVersion(0L);

			Empleado emp2 = new Empleado();
			emp2.setNombre("Sara");
			emp2.setApellido("Chota");
			emp2.setRowVersion(0L);

			Empleado emp3 = new Empleado();
			emp3.setNombre("Fiorella");
			emp3.setApellido("Estela");
			emp3.setRowVersion(0L);

			Empleado emp4 = new Empleado();
			emp4.setNombre("Facundo");
			emp4.setApellido("Estela");
			emp4.setRowVersion(0L);

			emp1.setId(empDao.create(emp1));
			emp2.setId(empDao.create(emp2));
			emp3.setId(empDao.create(emp3));
			emp4.setId(empDao.create(emp4));

			List<Empleado> empList = empDao.selectAll();
			System.out.println("\nEmpleados");
			for (Empleado emp : empList) {
				System.out.println(Util.toString(emp));
			}

			// Seteo de Direcciones

			Direccion dir1 = new Direccion();
			dir1.setNombreVia("Calle Amazonas");
			dir1.setNumero("129");
			dir1.setRowVersion(0L);

			Direccion dir2 = new Direccion();
			dir2.setNombreVia("Calle Loreto");
			dir2.setNumero("207");
			dir2.setRowVersion(0L);

			Direccion dir3 = new Direccion();
			dir3.setNombreVia("Calle Madre de Dios");
			dir3.setNumero("316");
			dir3.setRowVersion(0L);

			Direccion dir4 = new Direccion();
			dir4.setNombreVia("Calle San Martin");
			dir4.setNumero("414");
			dir4.setRowVersion(0L);

			dir1.setId(dirDao.create(dir1));
			dir2.setId(dirDao.create(dir2));
			dir3.setId(dirDao.create(dir3));
			dir4.setId(dirDao.create(dir4));

			List<Direccion> dirList = dirDao.selectAll();
			System.out.println("\nDirecciones");
			for (Direccion dir : dirList) {
				System.out.println(Util.toString(dir));
			}

			// Seteo de Asociaciones

			ofiDao.createAssoc(ofi1, "empleados", emp1);
			ofiDao.createAssoc(ofi1, "empleados", emp3);

			empDao.createAssoc(emp2, "oficina", ofi2);
			empDao.createAssoc(emp4, "oficina", ofi2);

			proDao.createAssoc(pro1, "empleados", emp1);
			proDao.createAssoc(pro1, "empleados", emp2);
			proDao.createAssoc(pro1, "empleados", emp3);
			proDao.createAssoc(pro2, "empleados", emp2);
			proDao.createAssoc(pro2, "empleados", emp3);
			proDao.createAssoc(pro2, "empleados", emp4);
			proDao.createAssoc(pro3, "empleados", emp1);
			proDao.createAssoc(pro3, "empleados", emp2);
			proDao.createAssoc(pro3, "empleados", emp3);
			proDao.createAssoc(pro3, "empleados", emp4);

			empDao.createAssoc(emp1, "direccion", dir1);
			empDao.createAssoc(emp2, "direccion", dir2);
			empDao.createAssoc(emp3, "direccion", dir3);
			empDao.createAssoc(emp4, "direccion", dir4);

			// Display de Asociaciones

			ofi1.setEmpleados(ofiDao.selectAssocMany(ofi1, "empleados", Empleado.class));
			System.out.println("\nEmpleados de la Oficina: "+Util.toString(ofi1));
			for (Empleado emp : ofi1.getEmpleados()) {
				emp.setDireccion(empDao.selectAssocOne(emp, "direccion", Direccion.class));
				System.out.println(Util.toString(emp));
				System.out.println("\tDireccion: "+Util.toString(emp.getDireccion()));
			}

			ofi2.setEmpleados(ofiDao.selectAssocMany(ofi2, "empleados", Empleado.class));
			System.out.println("\nEmpleados de la Oficina: "+Util.toString(ofi2));
			for (Empleado emp : ofi2.getEmpleados()) {
				emp.setDireccion(empDao.selectAssocOne(emp, "direccion", Direccion.class));
				System.out.println(Util.toString(emp));
				System.out.println("\tDireccion: "+Util.toString(emp.getDireccion()));
			}

			pro1.setEmpleados(proDao.selectAssocMany(pro1, "empleados", Empleado.class));
			System.out.println("\nEmpleados del Proyecto: "+Util.toString(pro1));
			for (Empleado emp : pro1.getEmpleados()) {
				System.out.println(Util.toString(emp));
			}

			emp3.setProyectos(empDao.selectAssocMany(emp3, "proyectos", Proyecto.class));
			System.out.println("\nProyectos del Empleado: "+Util.toString(emp3));
			for (Proyecto pro : emp3.getProyectos()) {
				System.out.println(Util.toString(pro));
			}

			// Eliminación de Datos

			ofiDao.delete(ofi1.getId());
			ofiDao.delete(ofi2.getId());

			proDao.delete(pro1.getId());
			proDao.delete(pro2.getId());
			proDao.delete(pro3.getId());

			empDao.delete(emp1.getId());
			empDao.delete(emp2.getId());
			empDao.delete(emp3.getId());
			empDao.delete(emp4.getId());

			dirDao.delete(dir1.getId());
			dirDao.delete(dir2.getId());
			dirDao.delete(dir3.getId());
			dirDao.delete(dir4.getId());

			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
