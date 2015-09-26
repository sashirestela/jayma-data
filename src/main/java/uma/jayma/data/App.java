package uma.jayma.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import uma.jayma.data.sample.Empleado;
import uma.jayma.data.sample.EmpleadoDao;
import uma.jayma.data.sample.EmpleadoDaoImpl;
import uma.jayma.data.sample.Oficina;
import uma.jayma.data.sample.OficinaDao;
import uma.jayma.data.sample.OficinaDaoImpl;
import uma.jayma.data.util.DataUtil;

/**
 * Hello world!
 *
 */
public class App 
{
    @SuppressWarnings("unchecked")
	public static void main( String[] args )
    {
		Connection conn = null;
		
    	try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sigmaflota", "sigmaflota", "sigmaflota");
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
        
        try {
			conn.setAutoCommit(false);
			
			OficinaDao ofiDao = new OficinaDaoImpl();
			ofiDao.setConnection(conn);
			
			EmpleadoDao empDao = new EmpleadoDaoImpl();
			empDao.setConnection(conn);
			
			Oficina ofi1 = new Oficina();
			ofi1.setNombre("Alfa");
			ofi1.setRowVersion(0L);
			
			Oficina ofi2 = new Oficina();
			ofi2.setNombre("Beta");
			ofi2.setRowVersion(0L);
			
			ofi1.setId(ofiDao.create(ofi1));
			ofi2.setId(ofiDao.create(ofi2));
			
			List<Oficina> ofiList = ofiDao.fetchAll();
			System.out.println("Oficinas");
			for (Oficina ofi : ofiList) {
				System.out.println(DataUtil.toString(ofi));
			}
			
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
			
			List<Empleado> empList = empDao.fetchAll();
			System.out.println("Empleados");
	        for (Empleado emp : empList) {
	            System.out.println(DataUtil.toString(emp));
			}
	        
	        ofiDao.saveLink(ofi1, "empleados", emp1);
	        ofiDao.saveLink(ofi1, "empleados", emp3);
	        
	        empDao.saveLink(emp2, "oficina", ofi2);
	        empDao.saveLink(emp4, "oficina", ofi2);
			
	        ofi1.setEmpleados((List<Empleado>)ofiDao.fetchLink(ofi1, "empleados"));
	        System.out.println("\nEmpleados de la Oficina: "+DataUtil.toString(ofi1));
	        for (Empleado emp : ofi1.getEmpleados()) {
				System.out.println(DataUtil.toString(emp));
			}
	        
	        ofi2.setEmpleados((List<Empleado>)ofiDao.fetchLink(ofi2, "empleados"));
	        System.out.println("\nEmpleados de la Oficina: "+DataUtil.toString(ofi2));
	        for (Empleado emp : ofi2.getEmpleados()) {
				System.out.println(DataUtil.toString(emp));
			}
	        
	        emp4.setOficina((Oficina)empDao.fetchLink(emp4, "oficina"));
	        System.out.println("\nOficina del Empleado: "+DataUtil.toString(emp4));
	        System.out.println(DataUtil.toString(emp4.getOficina()));
	        
	        ofiDao.deleteLink(ofi1, "empleados", emp3);
	        empDao.deleteLink(emp4, "oficina", null);
			
	        ofi1.setEmpleados((List<Empleado>)ofiDao.fetchLink(ofi1, "empleados"));
	        System.out.println("\nEmpleados de la Oficina: "+DataUtil.toString(ofi1));
	        for (Empleado emp : ofi1.getEmpleados()) {
				System.out.println(DataUtil.toString(emp));
			}
	        
	        ofi2.setEmpleados((List<Empleado>)ofiDao.fetchLink(ofi2, "empleados"));
	        System.out.println("\nEmpleados de la Oficina: "+DataUtil.toString(ofi2));
	        for (Empleado emp : ofi2.getEmpleados()) {
				System.out.println(DataUtil.toString(emp));
			}
	        
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
