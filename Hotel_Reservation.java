package Hotel_Res;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Hotel_Reservation {
		private static final String dburl = "jdbc:mysql://localhost:3306/hotel_db";
		private static final String user = "root";
		private static final String password = "Raghul@29";
		public static void main(String[] args) throws ClassNotFoundException,SQLException {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch(ClassNotFoundException e) {
				System.out.println(e.getMessage());
			}
			try {
				Connection con = DriverManager.getConnection(dburl, user, password);
				while (true) {
					System.out.println(); // new line consume
					System.out.println("     \t\t WELCOME TO        ");
					System.out.println("\t --- TAJ GROUP OF HOTELS ---");
					Scanner scanner = new Scanner(System.in);
					System.out.println("1. RESERVE A ROOM ");
					System.out.println("2. VIEW ROOM RESERVATIONS ");
					System.out.println("3. GET A ROOM NUMBER ");
					System.out.println("4. UPDATE RESERVATIONS ");
					System.out.println("5. DELETE RESERVATIONS ");
					System.out.println("0. EXIT ");
					System.out.println(" CHOOSE AN OPTION : ");
					int choice = scanner.nextInt();
					switch (choice) {
					case 1:
						reserveRoom(con, scanner);
						break;
					case 2:
						viewReservations(con);
						break;
					case 3:
						getRoomNumber(con, scanner);
						break;
					case 4:
						updateReservation(con, scanner);
						break;
					case 5:
						deleteReservation(con, scanner);
						break;
					case 0:
						exit();
						scanner.close();
						return;
					default:
						System.out.println("Invalid choice .... try again !!!");
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		// method for reservation of an hotel
		private static void reserveRoom(Connection con, Scanner scanner) {
			try {
				System.out.println("Enter Guest Name : ");
				String guest = scanner.next();
				scanner.nextLine();
				System.out.println("Enter Room Number : ");
				int room = scanner.nextInt();
				
				  // STEP 1: Check if room is already booked
		        String checkQuery = "SELECT ROOMNO FROM RESERVATIONS WHERE ROOMNO = ?";
		        PreparedStatement checkStmt = con.prepareStatement(checkQuery);
		        checkStmt.setInt(1, room);

		        ResultSet rs = checkStmt.executeQuery();
		        if (rs.next()) {
		            System.out.println("---- ROOM ALREADY BOOKED ----");
		            return; // Stop the method
		        }
		        
				System.out.println("Enter Contact Number : ");
				String contact = scanner.next();
				
			  
				String query = "INSERT INTO RESERVATIONS(GUESTNAME,ROOMNO,CONTACT) VALUES('"+guest+"',"+room+",'"+contact+"')";
				Statement stmt = con.createStatement();
				int count = stmt.executeUpdate(query);
				if (count > 0) {
					System.out.println("----RESERVATION DONE SUCCESSFULLY---------");
				} else {
					System.out.println("-----RESERVATION FAILS--------TRY AGAIN----------");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// method for view reservations
		private static void viewReservations(Connection con) {
			String query = "SELECT ID,GUESTNAME,ROOMNO,CONTACT,DATE FROM RESERVATIONS";
			//String query = "SELECT * FROM RESERVATIONS";
			try {
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				System.out.println("-----Current Reservations-----------");
				System.out.println("+-----------+------------------+-------------+---------------+---------------------+");
				System.out.println("|   R_ID    |      GUEST NAME  |   ROOM NO   |    CONTACT    |          DATE       |");
				System.out.println("+-----------+------------------+-------------+---------------+---------------------+");

				while (rs.next()) {
					int id = rs.getInt("ID");
					String guest = rs.getString("GUESTNAME");
					int room = rs.getInt("ROOMNO");
					String contact = rs.getString("CONTACT");
					String date = rs.getTimestamp("DATE").toString();

					System.out.printf("| %-10d | %-14s | %-12d | %13s | %-19s |\n ", id,guest,room,contact,date);
				}
				System.out.println("+-----------+-----------------+-------------+----------------+--------------------+");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// method for getting room numbers
		private static void getRoomNumber(Connection con, Scanner scanner) {
			try {
				System.out.println("Enter Reservation id : ");
				int id = scanner.nextInt();
				System.out.println("Enter Guest Name : ");
				String guest = scanner.next();
				// scanner.nextLine();
				String query = "SELECT ROOMNO FROM RESERVATIONS WHERE ID = "+id+" AND GUESTNAME="+"'"+guest+"'";

				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					int room = rs.getInt("ROOMNO");
					System.out.println("ROOM NO FOR GIVEN RESERVATION ID "+id+" & Guest Name :"+guest+" ROOM NO is "+room);
				} else {
					System.out.println("--RESERVATION NOT FOUND FOR GIVEN ID : " + id + " AND GUEST NAME : " + guest);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// method for update the reservations
		private static void updateReservation(Connection con, Scanner scanner) {
			try {
				System.out.println("Enter Reservation ID to Update : ");
				int id = scanner.nextInt();
				scanner.nextLine(); // consume the newline charecter
				if (!reservationExists(con, id)) {
					System.out.println("---   RESERVATION NOT FOUND FOR GIVEN ID  ------");
					return;
				}
				System.out.println("Enter new Guest Name : ");
				String newGuest = scanner.nextLine();
				System.out.println("Enter new room number : ");
				int newRoom = scanner.nextInt();
				System.out.println("Enter new Contact number : ");
				String newContact = scanner.next();

				String query = "UPDATE RESERVATIONS SET GUESTNAME='"+newGuest+"',ROOMNO="+newRoom+ ","
						+ "CONTACT ='"+newContact+"' WHERE ID ="+id;
				Statement stmt = con.createStatement();
				int count = stmt.executeUpdate(query);
				if (count > 0) {
					System.out.println("----   RESERVATION UPDATE SUCCESSFULLY  ------- ");
				} else {
					System.out.println("---- RESERVATION UPDATE FAILED  -------");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// method for delete reservation
		private static void deleteReservation(Connection con, Scanner scanner) {
			try {
				System.out.println("Enter Reservation ID to Delete Reservation : ");
				int id = scanner.nextInt();
				if (!reservationExists(con, id)) {
					System.out.println("----   RESERVATION NOT FOUND FOR GIVEN ID  ----");
					return;
				}
				String query = "DELETE FROM RESERVATIONS WHERE ID = " + id;
				Statement stmt = con.createStatement();
				int count = stmt.executeUpdate(query);
				if (count > 0) {
					System.out.println("--- RESERVATION DELETED SUCCESSFULLY ---");
				} else {
					System.out.println("--- RESERVATION DELETION FAILED ---");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// method for checking weather reservation exist or not
		private static boolean reservationExists(Connection con, int id) {
			try {
				String query = "SELECT ID FROM RESERVATIONS WHERE ID = " + id;
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				return rs.next(); // if there's a result the reservation exist
			} catch (Exception e) {
				e.printStackTrace();
				return false; // handle the database errors as needed
			}
		}

		// method for exiting the Reservation
		public static void exit() throws InterruptedException {
			System.out.println("--- EXITING THE SYSTEM ---");
			int i = 5;
			while (i != 0) {
				System.out.print(",");
				Thread.sleep(1000);
				i--;
			}
			System.out.println();
			System.out.println("--- THANK YOU FOR USING HOTEL RESERVATION SYSTEM !!! ---");
		}
	}
