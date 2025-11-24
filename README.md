# ✈️ Airline Reservation System V2.0

A comprehensive desktop application for managing flight bookings, schedules, and user profiles. Built from scratch using **Java Swing (GUI)** and **MySQL**.

![airline](https://github.com/user-attachments/assets/ee71329d-b283-46c4-b542-3cc046782bfc)
 


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

<img width="558" height="438" alt="Screenshot 2025-11-24 081416" src="https://github.com/user-attachments/assets/06873fae-553a-4f11-893e-0cfc4bb7ae9f" />
<img width="563" height="630" alt="Screenshot 2025-11-24 081423" src="https://github.com/user-attachments/assets/777a7a4a-4dd3-4153-9e91-11d14f489901" />
<img width="1235" height="806" alt="Screenshot 2025-11-24 081503" src="https://github.com/user-attachments/assets/84be189c-3048-4f2e-9ffa-f62d4e8925e3" />
<img width="1244" height="810" alt="Screenshot 2025-11-24 081513" src="https://github.com/user-attachments/assets/004c1bc6-ae8e-4552-bf3c-6e6f2a3b9851" />
<img width="641" height="899" alt="Screenshot 2025-11-24 081540" src="https://github.com/user-attachments/assets/9efe266c-3cc2-4e09-a3e1-67387be605e7" />
<img width="641" height="830" alt="Screenshot 2025-11-24 081547" src="https://github.com/user-attachments/assets/aa9337a4-6a7b-45fd-add5-71e37479e934" />
<img width="1193" height="755" alt="Screenshot 2025-11-24 081718" src="https://github.com/user-attachments/assets/4681ae1f-eb99-4f14-bca3-892a9d7cc467" />
<img width="572" height="669" alt="Screenshot 2025-11-24 081723" src="https://github.com/user-attachments/assets/2239bc04-f271-44da-a4c2-4c2a5c8d5dac" />
<img width="1202" height="761" alt="Screenshot 2025-11-24 081731" src="https://github.com/user-attachments/assets/c6806713-509a-46d0-8635-3d3d6a762a50" />


## 👤 Author

**[Shashank Srivastava]**
* [LinkedIn](https://www.linkedin.com/in/shashank-srivastava-58269b281/)
* [GitHub](https://github.com/genuineinsaan)

---
*This project was developed as a capstone project demonstrating full-stack desktop development capabilities.*
