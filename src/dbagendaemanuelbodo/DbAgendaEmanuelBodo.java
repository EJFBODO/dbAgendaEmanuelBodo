package dbagendaemanuelbodo;

import java.sql.*;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class DbAgendaEmanuelBodo {

    Connection con;

    private DbAgendaEmanuelBodo() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_agenda?serverTimezone=UTC", "root", "thismeanswar");

        } catch (Exception e) {
            System.err.println("Error " + e);
        }
    }

    private static int menu() {

        int seleccion;
        Scanner input = new Scanner(System.in);

        System.out.println(".::AGENDA::.");
        System.out.println(".::MENU::.");
        System.out.println("1 - Mostrar contactos");
        System.out.println("2 - Buscar contacto");
        System.out.println("3 - Agregar contacto");
        System.out.println("4 - Editar contacto");
        System.out.println("5 - Eliminar contacto");
        System.out.println("6 - Salir");

        System.out.println("Ingrese el numero : ");
        seleccion = input.nextInt();
        return seleccion;
    }

    public static void main(String[] args) {

        int seleccionUsuario = 0;

        do {
            seleccionUsuario = menu();

            switch (seleccionUsuario) {
                case 1:
                    mostrar();
                    break;
                case 2:
                    buscar();
                    break;
                case 3:
                    agregar();
                    break;
                case 4:
                    editar();
                    break;
                case 5:
                    eliminar();
                    break;

            }
        } while (seleccionUsuario != 6);

    }

    private static void mostrar() {
        DbAgendaEmanuelBodo mt = new DbAgendaEmanuelBodo();
        Statement st;
        ResultSet rs;

        try {

            st = mt.con.createStatement();
            rs = st.executeQuery("SELECT * FROM contactos");
            System.out.printf("%15s %-15s %-20s %15s %30s %n", "NOMBRE", "APELLIDO", "FECHA DE NACIMIENTO", "EMAIL", "TELEFONO");

            while (rs.next()) {
                System.out.printf("%15s %-15s %-20s %25s %20s %n", rs.getString("nombre"), rs.getString("apellido"), rs.getDate("fechaNacimiento"), rs.getString("email"), rs.getString("telefono"));

            }

            st = mt.con.createStatement();
            String Query = "SELECT * from contactos WHERE MONTH(fechaNacimiento)=MONTH(NOW()) AND DAY(fechaNacimiento)=DAY(NOW());";
            rs = st.executeQuery(Query);

            while (rs.next()) {
                System.out.println(" Es el cumpleaños de " + rs.getString("nombre") + " " + rs.getString("apellido"));

            }

            mt.con.close();

        } catch (SQLException e) {
            System.err.println("ERROR AL OBTENER LOS DATOS");
        }

    }

    private static void buscar() {
        DbAgendaEmanuelBodo mt = new DbAgendaEmanuelBodo();
        Statement st;
        ResultSet rs;

        try {

            System.out.print("Ingrese contacto a buscar por nombre,apellido o email: ");
            Scanner input = new Scanner(System.in);
            String busqueda = input.next();

            st = mt.con.createStatement();
            rs = st.executeQuery("SELECT * FROM contactos WHERE nombre like'" + busqueda + "' OR apellido like'" + busqueda + "' OR email Like '" + busqueda + "%'");
            while (rs.next()) {
                System.out.printf("%-15s %-15s %5s %n", "NOMBRE", "APELLIDO", "EMAIL");
                System.out.printf("%-15s %-15s %15s %n", rs.getString("nombre"), rs.getString("apellido"), rs.getString("email"));
            }

            mt.con.close();
        } catch (SQLException e) {
            System.err.println("Error");
            System.err.println(e.getMessage());
        }

    }

    private static void agregar() {
        DbAgendaEmanuelBodo mt = new DbAgendaEmanuelBodo();

        try {
            System.out.println("Ingresar separadamente nombre,apellido,fecha de nacimiento sin barra(YYYY/MM/DD),email y telefono:");

            Scanner input = new Scanner(System.in);

            String query = "insert into contactos(nombre,apellido,fechaNacimiento,email,telefono)" + "values (?,?,?,?,?)";
            PreparedStatement ps = mt.con.prepareStatement(query);
            String nombre = input.next();
            ps.setString(1, nombre);
            String apellido = input.next();
            ps.setString(2, apellido);
            int fechaNacimiento = input.nextInt();
            ps.setInt(3, fechaNacimiento);
            String email = input.next();
            ps.setString(4, email);
            String telefono = input.next();
            ps.setString(5, telefono);

            int resultCount = ps.executeUpdate();
            System.out.println("número de contactos agregados->" + resultCount);

            mt.con.close();

        } catch (SQLException e) {
            System.err.println("Error");
            System.err.println(e.getMessage());
        }

    }

    private static void editar() {
        DbAgendaEmanuelBodo mt = new DbAgendaEmanuelBodo();
        Statement st;
        ResultSet rs;
        try {

            System.out.print("Ingrese apellido del contacto a editar: ");
            Scanner input = new Scanner(System.in);
            String busqueda = input.next();

            st = mt.con.createStatement();
            rs = st.executeQuery("SELECT * FROM contactos WHERE apellido like'" + busqueda + "%'");
            while (rs.next()) {
                System.out.printf("%-15s %-15s %5s %n", "NOMBRE", "APELLIDO", "EMAIL");
                System.out.printf("%-15s %-15s %15s %n", rs.getString("nombre"), rs.getString("apellido"), rs.getString("email"));
            }

            System.out.println("Ingresar separadamente nombre,apellido,fecha de nacimiento sin barra(YYYY/MM/DD),email y telefono con los nuevos datos:");

            String query = "UPDATE contactos SET nombre=?,apellido=?,fechaNacimiento=?,email=?,telefono=? WHERE nombre='" + busqueda + "' OR apellido='" + busqueda + "' OR email Like '" + busqueda + "%'";

            PreparedStatement ps = mt.con.prepareStatement(query);
            String nombre = input.next();
            ps.setString(1, nombre);
            String apellido = input.next();
            ps.setString(2, apellido);
            int fechaNacimiento = input.nextInt();
            ps.setInt(3, fechaNacimiento);
            String email = input.next();
            ps.setString(4, email);
            String telefono = input.next();
            ps.setString(5, telefono);

            int resultRowCount = ps.executeUpdate();

            if (resultRowCount > 0) {
                System.out.println("número de contactos actualizados -> " + resultRowCount);
            } else {
                System.out.println("No se modifico ningun registro");
            }

            mt.con.close();

        } catch (SQLException e) {
            System.err.println("Error");
            System.err.println(e.getMessage());
        }

    }

    private static void eliminar() {
        DbAgendaEmanuelBodo mt = new DbAgendaEmanuelBodo();
        Statement st;
        ResultSet rs;
        try {
            System.out.print("Ingrese apellido del contacto a buscar para eliminar: ");
            Scanner input = new Scanner(System.in);
            String busqueda = input.next();

            st = mt.con.createStatement();
            rs = st.executeQuery("SELECT * FROM contactos WHERE apellido like'" + busqueda + "%'");
            while (rs.next()) {
                System.out.printf("%-15s %-15s %5s %n", "NOMBRE", "APELLIDO", "EMAIL");
                System.out.printf("%-15s %-15s %15s %n", rs.getString("nombre"), rs.getString("apellido"), rs.getString("email"));

                System.out.println("Ingresar apellido del contacto a eliminar");

                String query = "DELETE FROM contactos WHERE apellido=?";

                PreparedStatement ps = mt.con.prepareStatement(query);
                String apellido = input.next();
                ps.setString(1, apellido);

                int resultCount = ps.executeUpdate();
                System.out.println("número de contactos eliminados->" + resultCount);

            }

            mt.con.close();

        } catch (SQLException e) {
            System.out.println(e);
        }

    }

}
