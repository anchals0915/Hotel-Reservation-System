package com.hotelReservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

	private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "";

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException ce) {
			System.out.println(ce.getMessage());
		}

		try {
			Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			Scanner sc = new Scanner(System.in);

			System.out.println("\033[1;36m*********************************");
			System.out.println("*  \033[1;33mWelcome to Hotel Management  \033[1;36m*");
			System.out.println("*********************************\033[0m");

			do {
				System.out.println("\033[1;32m1. Reserve a room");
				System.out.println("2. View Reservation ");
				System.out.println("3. Get Room Number");
				System.out.println("4. Update Reservations ");
				System.out.println("5. Delete Reservations");
				System.out.println("6. Exit\033[0m");
				System.out.print("Enter an option ");
				int choice = sc.nextInt();

				switch (choice) {
				case 1 -> reserveRoom(con, sc);
				case 2 -> viewReservations(con);
				case 3 -> getRoomNumber(con, sc);
				case 4 -> updateReservation(con, sc);
				case 5 -> deleteReservation(con, sc);
				case 6 -> {
					exit();
					sc.close(); // closing the resource
				}
				default -> System.out.println("Invalid Choice. Try again.");
				}

			} while (true);
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		} catch (InterruptedException e) {
			System.out.print(e.getMessage());
		}

	}

	private static void reserveRoom(Connection con, Scanner sc) {
		System.out.print("Enter guest name : ");
		String guestName = sc.next();
		sc.nextLine();
		System.out.print("Enter room number : ");
		int roomNumber = sc.nextInt();
		System.out.print("Enter contact number : ");
		String contactNumber = sc.next();

		String sqlQuery = "INSERT INTO reservations(guest_name, room_number, contact_number) VALUES (?, ?, ?)";

		try {
			PreparedStatement st = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, guestName);
			st.setInt(2, roomNumber);
			st.setString(3, contactNumber);

			int affectedRows = st.executeUpdate();

			if (affectedRows > 0) {
				System.out.println("Reservation successful!");
			} else {
				System.out.println("Reservation failed!");
			}
			st.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void viewReservations(Connection con) {
		String query = "SELECT * FROM reservations ";
		try (Statement st = con.createStatement()) {
			ResultSet rs = st.executeQuery(query);

			System.out.println("Current Reservations ");
			System.out.printf("+----------------+------------+-------------+----------------+---------------------+%n");
			System.out.printf("| Reservation ID | Guest Name | Room Number | Contact Number | Reservation Date    |%n");
			System.out.printf("+----------------+------------+-------------+----------------+---------------------+%n");

			while (rs.next()) {

				System.out.printf("| %14d | %10s | %11d | %14s | %19s |%n", rs.getInt("reservation_id"),
						rs.getString("guest_name"), rs.getInt("room_number"), rs.getString("contact_number"),
						rs.getTimestamp("reservation_date"));
				System.out.printf(
						"+----------------+------------+-------------+----------------+---------------------+%n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void getRoomNumber(Connection con, Scanner sc) {
		System.out.print("Enter reservation ID : ");
		int reservationId = sc.nextInt();
		System.out.print("Enter guest name : ");
		String guestName = sc.next();

		String query = "SELECT room_number FROM reservations WHERE reservation_id = ? AND guest_name = ?";

		try (PreparedStatement st = con.prepareStatement(query)) {
			st.setInt(1, reservationId);
			st.setString(2, guestName);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				int roomNumber = rs.getInt("room_number");
				System.out.printf("+-----------------------+----------------------+------------+%n");
				System.out.printf("| Reservation ID        | Guest                | Room Number |%n");
				System.out.printf("+-----------------------+----------------------+------------+%n");
				System.out.printf("| %-21d | %-20s | %-11d |%n", reservationId, guestName, roomNumber);
				System.out.printf("+-----------------------+----------------------+------------+%n");
			} else {
				System.out.print("Reservation not found for the given ID and guest name.");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private static boolean reservationExists(Connection con, int reservationID) {
		String query = "select reservation_id from reservations where reservation_id = " + reservationID;
		try (Statement st = con.createStatement()) {
			ResultSet rs = st.executeQuery(query);
			return rs.next();
		} catch (SQLException s) {
			System.out.print(s.getMessage());
			return false;
		}
	}

	private static void updateReservation(Connection con, Scanner sc) {
		System.out.print("Enter reservation ID : ");
		int reservationId = sc.nextInt();

		if (!reservationExists(con, reservationId)) {
			System.out.print("Reservation not found for the given ID.");
			return;
		}

		System.out.print("Enter new guest name : ");
		String newGuestName = sc.next();
		sc.nextLine();
		System.out.print("Enter new room number : ");
		int newRoomNumber = sc.nextInt();
		System.out.print("Enter new contact Number :");
		String newContactNumber = sc.next();

		String query = "UPDATE reservations SET guest_name = ?, room_number = ?, contact_number = ? WHERE reservation_id = ?";

		try (PreparedStatement st = con.prepareStatement(query)) {
			st.setString(1, newGuestName);
			st.setInt(2, newRoomNumber);
			st.setString(3, newContactNumber);
			st.setInt(4, reservationId);

			int affectedRows = st.executeUpdate();

			if (affectedRows > 0) {
				System.out.println("Reservation update successful!");
			} else {
				System.out.println("Reservation update failed!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void deleteReservation(Connection con, Scanner sc) {
		System.out.print("Enter reservation ID : ");
		int reservationId = sc.nextInt();

		if (!reservationExists(con, reservationId)) {
			System.out.print("Reservation not found for the given ID.");
			return;
		}

		String query = "DELETE FROM reservations WHERE reservation_id = ?";

		try (PreparedStatement st = con.prepareStatement(query)) {
			st.setInt(1, reservationId);

			int affectedRows = st.executeUpdate();

			if (affectedRows > 0) {
				System.out.println("Reservation deleted successful!");
			} else {
				System.out.println("Reservation deleted failed!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void exit() throws InterruptedException {
		System.out.print("Existing System");
		int i = 5;
		while (i != 0) {
			System.out.print(".");
			Thread.sleep(450);
			i--;
		}
		System.out.println();
		System.out.print("Thank you for using Hotel Management System!!");
		System.exit(0);
	}

}
