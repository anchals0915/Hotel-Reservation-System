package com.hotelReservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

		Scanner sc = new Scanner(System.in);

		try (Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
			Hotel hotel = new Hotel(con);

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
				case 1 -> hotel.reserveRoom(sc);
				case 2 -> hotel.viewReservations();
				case 3 -> hotel.getRoomNumber(sc);
				case 4 -> hotel.updateReservation(sc);
				case 5 -> hotel.deleteReservation(sc);
				case 6 -> {
					try {
						exit();
					} catch (InterruptedException e) {
						System.out.print(e.getMessage());
					}
					sc.close(); // closing the resource
					System.exit(0);
				}
				default -> System.out.println("Invalid Choice. Try again.");
				}

			} while (true);
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		} finally {
			sc.close(); // closing the resource
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
	}

}
