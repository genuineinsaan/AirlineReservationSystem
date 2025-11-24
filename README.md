# ✈️ Airline Reservation System V2.0

A comprehensive desktop application for managing flight bookings, schedules, and user profiles. Built from scratch using **Java Swing (GUI)** and **MySQL**.

![Project Thumbnail](https://via.placeholder.com/800x400?text=Airline+System+V2.0+Dashboard) 
*(Replace the link above with a screenshot of your actual dashboard)*

## 🌟 Features

### 👤 User Module
* **Secure Registration:** Auto-login capability with mandatory profile completion (DOB, Address, etc.).
* **Smart Flight Search:** Filter flights by Source, Destination, and **Day of Week** using a calendar picker.
* **Visual Seat Map:** Interactive grid to select multiple seats. Booked seats appear red and disabled.
* **Booking Management:** View booking history grouped by transaction and cancel bookings.
* **My Account:** Update personal details and change passwords.

### 👨‍✈️ Admin Module
* **Flight Scheduling:** Add complex flight schedules (e.g., operates on Mon, Wed, Fri).
* **Validation:** Ensures arrival time is always after departure time.
* **Dashboard:** View all active flight schedules and a master list of all user bookings.

## 🛠️ Tech Stack

* **Language:** Java (Core + Swing)
* **Database:** MySQL (Relational DB with 4 NF Tables)
* **Architecture:** MVC / DAO Design Pattern
* **External Libraries:**
    * `mysql-connector-j`: Database connectivity.
    * `jcalendar`: Date picker component.

## 🗄️ Database Schema

The system uses a relational database `airline_system_v2` with the following structure:
* **Users:** Stores login credentials and extended profile info.
* **Flights:** Stores flight details and weekly operation schedules.
* **Transactions:** Groups multiple seat purchases into one payment record.
* **Booked_Seats:** Links individual seats to specific dates and transactions.

## 🚀 How to Run

### Prerequisites
1.  Java Development Kit (JDK) 8 or higher.
2.  MySQL Server installed and running.

### Setup Steps
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/AirlineReservationSystem.git](https://github.com/YOUR_USERNAME/AirlineReservationSystem.git)
    ```
2.  **Database Setup:**
    * Open your MySQL client.
    * Run the script provided in `database_setup.sql` (you should save your SQL script and upload it too).
    * Update `src/db/DBConnection.java` with your MySQL username and password.
3.  **Compile and Run:**
    Navigate to the project root and run:
    ```bash
    # Compile
    javac -d bin -sourcepath src -cp "lib/*" src/gui/LoginView.java
    
    # Run
    java -cp "bin;lib/*" gui.LoginView
    ```
    *(Note: On Mac/Linux, use `:` instead of `;` in the classpath)*

## 📸 Screenshots

*(Upload screenshots of your Login screen, User Dashboard, and Seat Map here)*

## 👤 Author

**[Shashank Srivastava]**
* [LinkedIn](https://www.linkedin.com/in/yourprofile)
* [GitHub](https://github.com/yourusername)

---
*This project was developed as a capstone project demonstrating full-stack desktop development capabilities.*
