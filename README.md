# Library Management System - Project ITA301

A web-based Library Management System built with Java Servlet/JSP and SQL Server. This project is developed for the ITA301 course and features Role-Based Access Control (RBAC), book borrowing/returning workflows, and an administrative dashboard with statistical charts.

## 🚀 Tech Stack

* **Backend:** Java 17, Jakarta EE (Servlet, JSP, JSTL)
* **Database:** Microsoft SQL Server (JDBC)
* **Frontend:** HTML5, CSS3, JavaScript (Chart.js for statistics)
* **Server:** Apache Tomcat 10.1
* **IDE/Build Tool:** Apache NetBeans / Ant

## ✨ Key Features

### 👤 User Roles
* **Register/Login:** Secure authentication system.
* **Catalog Browsing:** Search books by keyword or filter by category.
* **Book Details & Reviews:** View book information and leave star ratings/comments.
* **Borrowing System:** Borrow up to 5 books, track currently borrowed books, and return them.
* **Favorites:** Add or remove books from a personal favorites list.
* **Profile Management:** Update personal information, avatar, and change password.

### 🛡️ Admin Roles
* **Admin Dashboard:** Overview of library operations.
* **User Management:** View, edit, and delete user accounts.
* **Book Management:** CRUD operations for books, including image file uploads.
* **Borrow/Return Management:** Track user borrow records and process returns.
* **Statistics:** Visual charts (Chart.js) displaying total users, total books, borrowing trends, and overdue statuses.

## ⚙️ Installation & Setup

### 1. Prerequisites
* JDK 17+
* Apache Tomcat 10.1+
* Microsoft SQL Server
* Apache NetBeans IDE (recommended)

### 2. Database Setup
1. Open SQL Server Management Studio (SSMS).
2. Execute the provided `LibraryDB.sql` file (located in the project directory) to create the database, tables, triggers, and mock data.

### 3. Project Configuration
1. Clone the repository:
   ```bash
   git clone [https://github.com/tdtfptuk19-cmd/Library_draft.git](https://github.com/tdtfptuk19-cmd/Library_draft.git)
