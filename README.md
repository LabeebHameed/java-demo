# 🎓 EduTrack — College Student Management System

A sleek, modern, and lightweight **College Student Management System** built with **Java (Swing + FlatLaf)** and **MySQL (JDBC)**.

Designed specifically as a clean educational codebase to teach junior students how to build professional-looking desktop applications in Java with database persistence and zero bloat.

---

## ✨ Features & UI Highlights

- **Modern Design**: Clean card-based layout with rounded corners, modern indigo theme, and custom status badge pills (`Active`, `Graduated`, `On Leave`).
- **Live KPI Dashboard Cards**: Instant summary metrics showing **Total Students**, **Active Enrolled**, and **Average GPA**.
- **Real-Time Instant Search**: Dynamic filtering across Roll No, Student Name, Email, and Department as you type.
- **Full CRUD Workflow**:
  - **Create**: Add new students with instant form validation.
  - **Read**: View structured table data with custom cell renderers.
  - **Update**: Click any row to automatically load details into the form and edit.
  - **Delete**: Remove students with confirmation dialog.
- **Non-blocking Status Toast**: Real-time feedback bar at the bottom for operations and error notifications.
- **Teaching-Ready Codebase**: Only **3 core Java files** with clear comments explaining Swing layouts, event listeners, and JDBC queries.

---

## 📁 Minimalist Project Structure

```
java-demo/
├── pom.xml                               # Maven config (FlatLaf + MySQL Connector)
├── schema.sql                            # SQL schema & demo seed data
├── README.md                             # Setup & learning guide
└── src/
    └── main/
        └── java/
            └── com/college/
                ├── Student.java          # Model (POJO) with fields, getters & setters
                ├── DatabaseHelper.java   # JDBC connection, auto-table creation & CRUD queries
                └── StudentApp.java       # Modern UI, event handling & table data binding
```

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java JDK 17+** (or Java 11+)
- **Maven**
- **MySQL Server** (Localhost on port `3306`)

---

### 2. Configure Database Credentials

Open [`DatabaseHelper.java`](file:///Users/labeeb/Documents/CODE/Projects/java-demo/src/main/java/com/college/DatabaseHelper.java) and verify your MySQL password:

```java
private static final String DB_HOST = "localhost";
private static final String DB_PORT = "3306";
private static final String DB_NAME = "college_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "password"; // <-- Set your MySQL root password here
```

*(Note: The app will automatically create the `college_db` database, create the `students` table, and seed initial demo records on the very first run!)*

You can also run the SQL script manually if you prefer:
```bash
mysql -u root -p < schema.sql
```

---

### 3. Run the Application

#### Option A: Using Maven Terminal Command
```bash
mvn compile exec:java
```

#### Option B: In Any Java IDE (IntelliJ IDEA / VS Code / Eclipse)
1. Open the `java-demo` folder in your IDE.
2. Let Maven import the dependencies.
3. Open `src/main/java/com/college/StudentApp.java` and click **Run**.

---

## 📚 Key Concepts for Junior Students

| Concept | File | Educational Takeaway |
| :--- | :--- | :--- |
| **POJO (Data Model)** | `Student.java` | Clean object encapsulation with private fields and standard getters/setters. |
| **JDBC & Security** | `DatabaseHelper.java` | Using `PreparedStatement` with `?` placeholders to prevent SQL Injection attacks. |
| **Look & Feel (UI/UX)** | `StudentApp.java` | Modern FlatLaf theme setup (`FlatLightLaf`) and component arc rounding. |
| **Event Handling** | `StudentApp.java` | `ActionListener`, `KeyAdapter` for instant search, and `MouseAdapter` for table clicks. |
| **Table Data Binding** | `StudentApp.java` | Working with `DefaultTableModel` and custom `TableCellRenderer` for status badges. |

---

## 📝 License
Educational open-source project. Feel free to customize and extend!
