USE [master]
GO
IF DB_ID('LibraryDB') IS NOT NULL
BEGIN
    ALTER DATABASE LibraryDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE LibraryDB;
END;
GO
CREATE DATABASE LibraryDB;
GO

USE LibraryDB;
GO

-- Tạo bảng User
CREATE TABLE Users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    fullname VARCHAR(100) NOT NULL,
    role VARCHAR(10) CHECK (role IN ('admin', 'user')) DEFAULT 'user',
    status VARCHAR(10) CHECK (status IN ('active', 'banned')) DEFAULT 'active',
    create_at DATETIME DEFAULT GETDATE()
);

-- Tạo bảng Category
CREATE TABLE Category (
    category_id INT IDENTITY(1,1) PRIMARY KEY,
    category_name VARCHAR(100) UNIQUE NOT NULL
);

-- Tạo bảng Book
CREATE TABLE Book (
    book_id INT IDENTITY(1,1) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    publisher VARCHAR(100) NOT NULL,
    category_id INT,
    quantity INT NOT NULL,
    available INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),

    FOREIGN KEY (category_id) REFERENCES Category(category_id) ON DELETE CASCADE
);

-- Tạo bảng Borrow_Record
CREATE TABLE Borrow_Record (
    record_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT,
    book_id INT,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    status VARCHAR(10) CHECK (status IN ('borrowed', 'returned', 'overdue')) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES Book(book_id) ON DELETE CASCADE
);

-- Tạo bảng Review
CREATE TABLE Review (
    review_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT,
    book_id INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES Book(book_id) ON DELETE CASCADE
);

-- Tạo bảng Favorite_Book
CREATE TABLE Favorite_Book (
    favorite_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT,
    book_id INT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES Book(book_id) ON DELETE CASCADE
);


-- Tạo dữ liệu 
-- Thêm dữ liệu vào bảng Users
INSERT INTO Users (username, password, email, fullname, role, status) VALUES
('admin1', 'pass123', 'admin1@example.com', 'Admin One', 'admin', 'active'),
('user1', 'pass123', 'user1@example.com', 'User One', 'user', 'active'),
('user2', 'pass456', 'user2@example.com', 'User Two', 'user', 'active'),
('user3', 'pass789', 'user3@example.com', 'User Three', 'user', 'banned');

-- Thêm dữ liệu vào bảng Category
INSERT INTO Category (category_name) VALUES
('Science Fiction'),
('History'),
('Technology'),
('Literature');

-- Thêm dữ liệu vào bảng Book
INSERT INTO Book (title, author, publisher, category_id, quantity, available) VALUES
('Dune', 'Frank Herbert', 'Ace Books', 1, 10, 8),
('Sapiens', 'Yuval Noah Harari', 'Harper', 2, 5, 4),
('Clean Code', 'Robert C. Martin', 'Prentice Hall', 3, 7, 7),
('The Great Gatsby', 'F. Scott Fitzgerald', 'Scribner', 4, 6, 5);

-- Thêm dữ liệu vào bảng Borrow_Record
INSERT INTO Borrow_Record (user_id, book_id, borrow_date, due_date, return_date, status) VALUES
(2, 1, '2024-02-10', '2024-02-20', NULL, 'borrowed'),
(3, 2, '2024-02-12', '2024-02-22', '2024-02-21', 'returned'),
(2, 3, '2024-02-15', '2024-02-25', NULL, 'overdue'),
(4, 4, '2024-02-16', '2024-02-26', NULL, 'borrowed');

-- Thêm dữ liệu vào bảng Review
INSERT INTO Review (user_id, book_id, rating, comment) VALUES
(2, 1, 5, 'Great sci-fi novel!'),
(3, 2, 4, 'Very insightful book about history.'),
(2, 3, 5, 'A must-read for programmers!'),
(4, 4, 3, 'Classic but a bit overrated.');

-- Thêm dữ liệu vào bảng Favorite_Book
INSERT INTO Favorite_Book (user_id, book_id) VALUES
(2, 1),
(2, 3),
(3, 2),
(4, 4);




alter table Users add img_url VARCHAR(255) default '../img/userdefault.jpg';
alter table Book add img_url VARCHAR(255) default '../img/bookdefault.jpg';
update Users
set img_url = '../img/userdefault.jpg' where user_id = 1;
update Users
set img_url = '../img/userdefault.jpg' where user_id = 2;
update Users
set img_url = '../img/userdefault.jpg' where user_id = 3;
update Users
set img_url = '../img/userdefault.jpg' where user_id = 4;

update Book
set img_url = '../img/bookdefault.jpg' where book_id = 1;
update Book
set img_url = '../img/bookdefault.jpg' where book_id = 2;
update Book
set img_url = '../img/bookdefault.jpg' where book_id = 3;
update Book
set img_url = '../img/bookdefault.jpg' where book_id = 4;


CREATE TABLE Permission(
	permission_id INT IDENTITY(1,1) PRIMARY KEY,
	role VARCHAR(100),
	canAccess VARCHAR(100)
);

INSERT INTO Permission(role, canAccess) VALUES
('admin', '/adminLogin'),
('admin', '/Statistic.jsp'),
('admin', '/account?action=accountmanagement'),
('admin', '/admin?action=bookmanagement'),
('admin', '/borrowmanagement?action=borrowmanagement'),
('admin', '/statistic'),
('user', '/home'),
('user', '/catalog'),
('user', '/viewdetail'),
('user', '/mybooks'),
('user', '/favorite'),
('user', '/borrow')



-- them du lieu cho trang
INSERT INTO Category (category_name) VALUES
('Education');
INSERT INTO Book (title, author, publisher, category_id, quantity, available) VALUES
('Educated', 'Tara Westover', 'Random House', 5, 10, 8),
('How Children Succeed', 'Paul Tough', 'Houghton Mifflin Harcourt', 5, 8, 6),
('Mindset: The New Psychology of Success', 'Carol S. Dweck', 'Random House', 5, 12, 10),
('The Smartest Kids in the World', 'Amanda Ripley', 'Simon & Schuster', 5, 7, 5),
('Make It Stick: The Science of Successful Learning', 'Peter C. Brown', 'Harvard University Press', 5, 6, 4),

('Hyperion', 'Dan Simmons', 'Bantam Spectra', 1, 12, 10),
('Foundation', 'Isaac Asimov', 'Gnome Press', 1, 15, 12),
('Neuromancer', 'William Gibson', 'Ace Books', 1, 8, 6),
('Brave New World', 'Aldous Huxley', 'Chatto & Windus', 1, 10, 7),
('The Left Hand of Darkness', 'Ursula K. Le Guin', 'Ace Books', 1, 9, 8),

('Guns, Germs, and Steel', 'Jared Diamond', 'W. W. Norton & Company', 2, 10, 9),
('The Silk Roads', 'Peter Frankopan', 'Bloomsbury', 2, 8, 6),
('A Peoples History of the United States', 'Howard Zinn', 'Harper', 2, 7, 5),
('Sapiens', 'Yuval Noah Harari', 'Harper', 2, 6, 3),
('The Wright Brothers', 'David McCullough', 'Simon & Schuster', 2, 5, 4),

('To Kill a Mockingbird', 'Harper Lee', 'J. B. Lippincott & Co.', 4, 10, 8),
('Pride and Prejudice', 'Jane Austen', 'T. Egerton', 4, 12, 10),
('Moby-Dick', 'Herman Melville', 'Harper & Brothers', 4, 7, 5),

('The Pragmatic Programmer', 'Andrew Hunt & David Thomas', 'Addison-Wesley', 3, 10, 9),
('Introduction to Algorithms', 'Thomas H. Cormen', 'MIT Press', 3, 12, 10),
('Design Patterns', 'Erich Gamma et al.', 'Addison-Wesley', 3, 8, 7);

INSERT INTO Borrow_Record (user_id, book_id, borrow_date, due_date, return_date, status) VALUES
-- Overdue (Mượn nhưng chưa trả, đã quá hạn)
(2, 1, '2024-10-05', '2024-10-20', NULL, 'overdue'), 
(3, 2, '2024-11-10', '2024-11-25', NULL, 'overdue'), 
(2, 3, '2024-12-01', '2024-12-16', NULL, 'overdue'), 
(3, 4, '2025-01-15', '2025-01-30', NULL, 'overdue'), 
(2, 2, '2025-02-10', '2025-02-25', NULL, 'overdue'), 

-- Borrowed (Mượn nhưng chưa trả, chưa quá hạn)
(3, 6, '2025-03-01', '2025-03-20', NULL, 'borrowed'), 
(2, 7, '2025-02-28', '2025-03-18', NULL, 'borrowed'), 
(3, 8, '2025-02-25', '2025-03-15', NULL, 'borrowed'), 

-- Returned (Đã trả sách)
(2, 9, '2024-09-20', '2024-10-05', '2024-10-04', 'returned'), 
(3, 10, '2024-10-15', '2024-10-30', '2024-10-29', 'returned'), 
(2, 1, '2024-11-05', '2024-11-20', '2024-11-19', 'returned'), 
(3, 2, '2024-12-10', '2024-12-25', '2024-12-24', 'returned'), 
(2, 3, '2025-01-05', '2025-01-20', '2025-01-19', 'returned'), 
(3, 4, '2025-02-01', '2025-02-16', '2025-02-15', 'returned'), 
(2, 3, '2025-02-20', '2025-03-06', '2025-03-05', 'returned');

INSERT INTO Review (user_id, book_id, rating, comment) VALUES
(2, 1, 5, 'A fascinating read! The author does a great job keeping the reader engaged.'),
(3, 2, 4, 'Very insightful, but some parts felt a bit slow.'),
(2, 3, 3, 'Not bad, but I expected more depth on certain topics.'),
(3, 4, 5, 'Absolutely loved it! One of the best books I have read this year.'),
(2, 2, 4, 'Great book with useful information, though a bit technical.'),
(3, 6, 5, 'A must-read for anyone interested in the subject!'),
(2, 7, 3, 'Some good ideas, but I found it hard to stay engaged.'),
(3, 8, 4, 'Well-written and informative, but a bit repetitive.'),
(2, 9, 5, 'An outstanding book with deep insights and excellent writing.'),
(3, 10, 4, 'Really enjoyed it! The characters were well developed.'),
(2, 11, 5, 'An inspiring story that left a strong impression on me.'),
(3, 12, 3, 'I struggled to finish it, but it had a few good parts.'),
(2, 13, 4, 'Great storytelling, but the pacing was a bit uneven.'),
(3, 14, 5, 'One of the best books I have read in a long time!'),
(2, 15, 4, 'A thought-provoking book that challenges conventional thinking.');

CREATE TABLE Fine (
    fine_id INT IDENTITY(1,1) PRIMARY KEY,
    record_id INT NOT NULL,  -- liên kết với Borrow_Record
    fine_amount DECIMAL(10,2) NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) CHECK (status IN ('unpaid', 'paid')) DEFAULT 'unpaid',
    created_at DATETIME DEFAULT GETDATE(),

    FOREIGN KEY (record_id) REFERENCES Borrow_Record(record_id) ON DELETE CASCADE
);

Go
CREATE TRIGGER trg_CreateFine_WhenOverdue
ON Borrow_Record
AFTER INSERT, UPDATE
AS
BEGIN
    INSERT INTO Fine (record_id, fine_amount, reason)
    SELECT 
        i.record_id,
        DATEDIFF(DAY, i.due_date, GETDATE()) * 5000,
        'Overdue ' + CAST(DATEDIFF(DAY, i.due_date, GETDATE()) AS VARCHAR) + ' days'
    FROM inserted i
    WHERE 
        i.status = 'overdue'
        AND DATEDIFF(DAY, i.due_date, GETDATE()) > 0
        AND NOT EXISTS (
            SELECT 1 FROM Fine f WHERE f.record_id = i.record_id
        );
END;
