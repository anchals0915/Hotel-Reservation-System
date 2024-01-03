package com.hotelReservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Hotel {
	private final Connection con;

	public Hotel(Connection con) {
		this.con = con;
	}

	public void reserveRoom(Scanner sc) {
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

	public void viewReservations() {
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

	public void getRoomNumber(Scanner sc) {
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

	public boolean reservationExists(int reservationID) {
		String query = "select reservation_id from reservations where reservation_id = " + reservationID;
		try (Statement st = con.createStatement()) {
			ResultSet rs = st.executeQuery(query);
			return rs.next();
		} catch (SQLException s) {
			System.out.print(s.getMessage());
			return false;
		}
	}

	public void updateReservation(Scanner sc) {
		System.out.print("Enter reservation ID : ");
		int reservationId = sc.nextInt();

		if (!reservationExists(reservationId)) {
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

	public void deleteReservation(Scanner sc) {
		System.out.print("Enter reservation ID : ");
		int reservationId = sc.nextInt();

		if (!reservationExists(reservationId)) {
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

}
