<%-- 
    Document   : homepage
    Created on : Mar 1, 2025, 1:07:41 AM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Library Manegement - Home</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="preconnect" href="https://fonts.gstatic.com"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/style.css">
    </head>
    <body>
        <header>
            <nav class="navbar">
                <a href="home" style="cursor: pointer; text-decoration: none">
                    <div class="logo" >
                        <i class="fas fa-book-open"></i>
                        <span>Library.Thien</span>
                    </div>
                </a>
                <ul class="nav-links">
                    <li><a href="home">Home</a></li>
                    <li><a href="catalog">Catalog</a></li>
                    <li><a href="#">Contact</a></li>
                    <li class="auth-links">
                        <c:if test="${empty sessionScope.user}">
                            <a href="login" class="login-btn">Login</a>
                            <a href="register" class="register-btn">Register</a>
                        </c:if>
                        <c:if test="${ not empty sessionScope.user}">
                            <div class="profile-menu">
                                <div class="profile-icon">
                                    <img src="${sessionScope.user.imgUrl}" alt="Avatar" style="width: 30px; border-radius: 50%;"/>
                                    <span style="color: black">${sessionScope.user.fullname}</span>
                                </div>
                                <div class="profile-dropdown">
                                    <a href="profile">My Profile</a>
                                    <a href="mybooks">My Books</a>
                                    <a href="favorite">My Favorite</a>
                                    <a href="logout" class="logout-btn">Logout</a>
                                </div>
                            </div>
                        </c:if>
                    </li>
                </ul>
            </nav>
        </header>

        <main>
            <section class="hero">
                <div class="hero-content">
                    <h1>Library Management</h1>
                    <p>Where books are more than just paperweights!</p>
                    <a href="catalog" class="cta-button">Get Started</a>
                </div>
            </section>

            <section class="features">
                <h2>Your Library Tools</h2>
                <div class="feature-grid">
                    <div class="feature-card">
                        <i class="fas fa-search"></i>
                        <h3>Search Books</h3>
                        <p>Find your next read by title or author</p>

                        <a href="catalog" class="feature-link">Go to Catalog</a>

                    </div>
                    <div class="feature-card">
                        <i class="fas fa-book-reader"></i>
                        <h3>Borrow Books</h3>
                        <p>Borrow up to 5 books at a time</p>
                        <a href="catalog" class="feature-link">Start Borrowing</a>
                    </div>
                    <div class="feature-card">
                        <i class="fas fa-history"></i>
                        <h3>Borrow History</h3>
                        <p>Track your borrowed books</p>
                        <a href="mybooks" class="feature-link">View History</a>
                    </div>
                    <div class="feature-card">
                        <i class="fas fa-star"></i>
                        <h3>Rate & Review</h3>
                        <p>Share your thoughts on books</p>
                        <a href="catalog" class="feature-link">Rate Now</a>
                    </div>
                    <div class="feature-card">
                        <i class="fas fa-heart"></i>
                        <h3>Favorites</h3>
                        <p>Save your favorite books</p>
                        <a href="favorite" class="feature-link">View Favorites</a>
                    </div>
                </div>
            </section>

            <section class="features">
                <h2>Highlights</h2>
                <div style="max-width: 1100px; margin: 0 auto;">
                    <h3 style="margin: 12px 0;">Featured (Top Rated)</h3>
                    <div class="feature-grid">
                        <c:forEach var="b" items="${featuredBooks}">
                            <div class="feature-card">
                                <img src="${b.imgUrl}" alt="${b.title}" style="width:100%; height: 160px; object-fit:cover; border-radius:10px; margin-bottom:10px;">
                                <h3 style="margin-top:0">${b.title}</h3>
                                <p style="margin: 6px 0;">${b.author}</p>
                                <a href="viewdetail?bookId=${b.bookId}" class="feature-link">View details</a>
                            </div>
                        </c:forEach>
                    </div>

                    <h3 style="margin: 22px 0 12px;">New Books</h3>
                    <div class="feature-grid">
                        <c:forEach var="b" items="${newBooks}">
                            <div class="feature-card">
                                <img src="${b.imgUrl}" alt="${b.title}" style="width:100%; height: 160px; object-fit:cover; border-radius:10px; margin-bottom:10px;">
                                <h3 style="margin-top:0">${b.title}</h3>
                                <p style="margin: 6px 0;">${b.author}</p>
                                <a href="viewdetail?bookId=${b.bookId}" class="feature-link">View details</a>
                            </div>
                        </c:forEach>
                    </div>

                    <h3 style="margin: 22px 0 12px;">Best Selling (Most Borrowed)</h3>
                    <div class="feature-grid">
                        <c:forEach var="b" items="${bestSellingBooks}">
                            <div class="feature-card">
                                <img src="${b.imgUrl}" alt="${b.title}" style="width:100%; height: 160px; object-fit:cover; border-radius:10px; margin-bottom:10px;">
                                <h3 style="margin-top:0">${b.title}</h3>
                                <p style="margin: 6px 0;">${b.author}</p>
                                <a href="viewdetail?bookId=${b.bookId}" class="feature-link">View details</a>
                            </div>
                        </c:forEach>
                    </div>

                    <h3 style="margin: 22px 0 12px;">Most Favorited</h3>
                    <div class="feature-grid">
                        <c:forEach var="b" items="${mostFavoritedBooks}">
                            <div class="feature-card">
                                <img src="${b.imgUrl}" alt="${b.title}" style="width:100%; height: 160px; object-fit:cover; border-radius:10px; margin-bottom:10px;">
                                <h3 style="margin-top:0">${b.title}</h3>
                                <p style="margin: 6px 0;">${b.author}</p>
                                <a href="viewdetail?bookId=${b.bookId}" class="feature-link">View details</a>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </section>

            <section class="features" style="padding-top: 24px">
                <h2>Explore Books</h2>
                <div style="max-width: 1100px; margin: 0 auto;">
                    <form action="home" method="get" style="display:flex; gap:10px; flex-wrap:wrap; align-items:end; margin-bottom: 16px;">
                        <div style="flex:1; min-width: 220px;">
                            <label style="display:block; font-size:12px; color:#666; margin-bottom:6px;">Search</label>
                            <input name="q" value="${q}" placeholder="Title / author / publisher"
                                   style="width:100%; padding:10px 12px; border:1px solid #ddd; border-radius:8px;">
                        </div>
                        <div style="min-width: 170px;">
                            <label style="display:block; font-size:12px; color:#666; margin-bottom:6px;">Category</label>
                            <select name="categoryId" style="width:100%; padding:10px 12px; border:1px solid #ddd; border-radius:8px;">
                                <option value="">All</option>
                                <c:forEach var="c" items="${categories}">
                                    <option value="${c.categoryId}" ${categoryId==c.categoryId?'selected':''}>${c.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div style="min-width: 170px;">
                            <label style="display:block; font-size:12px; color:#666; margin-bottom:6px;">Sort</label>
                            <select name="sort" style="width:100%; padding:10px 12px; border:1px solid #ddd; border-radius:8px;">
                                <option value="newest" ${sort=='newest'?'selected':''}>Newest</option>
                                <option value="title_asc" ${sort=='title_asc'?'selected':''}>Title A → Z</option>
                                <option value="title_desc" ${sort=='title_desc'?'selected':''}>Title Z → A</option>
                                <option value="author_asc" ${sort=='author_asc'?'selected':''}>Author A → Z</option>
                                <option value="author_desc" ${sort=='author_desc'?'selected':''}>Author Z → A</option>
                            </select>
                        </div>
                        <button type="submit" style="padding:10px 14px; border:none; border-radius:8px; background:#3498db; color:#fff; cursor:pointer;">
                            <i class="fas fa-search"></i> Search
                        </button>
                    </form>

                    <c:if test="${not empty books}">
                        <div class="feature-grid">
                            <c:forEach var="b" items="${books}">
                                <div class="feature-card">
                                    <img src="${b.imgUrl}" alt="${b.title}" style="width:100%; height: 180px; object-fit:cover; border-radius:10px; margin-bottom:10px;">
                                    <h3 style="margin-top:0">${b.title}</h3>
                                    <p style="margin: 6px 0;"><strong>Author:</strong> ${b.author}</p>
                                    <p style="margin: 6px 0;"><strong>Publisher:</strong> ${b.publisher}</p>
                                    <a href="viewdetail?bookId=${b.bookId}" class="feature-link">View details</a>
                                </div>
                            </c:forEach>
                        </div>

                        <div style="display:flex; justify-content:center; gap:10px; margin-top: 16px; flex-wrap:wrap;">
                            <c:if test="${page > 1}">
                                <a class="feature-link" href="home?page=${page-1}&q=${q}&categoryId=${categoryId}&sort=${sort}" style="padding:8px 12px; border:1px solid #ddd; border-radius:8px;">
                                    Previous
                                </a>
                            </c:if>
                            <span style="padding:8px 12px; color:#666;">
                                Page ${page} / ${totalPages} (${total} items)
                            </span>
                            <c:if test="${page < totalPages}">
                                <a class="feature-link" href="home?page=${page+1}&q=${q}&categoryId=${categoryId}&sort=${sort}" style="padding:8px 12px; border:1px solid #ddd; border-radius:8px;">
                                    Next
                                </a>
                            </c:if>
                        </div>
                    </c:if>

                    <c:if test="${empty books}">
                        <p style="color:#888; text-align:center; padding: 12px 0;">No books found.</p>
                    </c:if>
                </div>
            </section>
        </main>

        <footer>
            <div class="footer-content">
                <div class="footer-section">
                    <h3>About Us</h3>
                    <p>Library Madness is your digital gateway to knowledge and entertainment.</p>
                </div>
                <div class="footer-section">
                    <h3>Quick Links</h3>
                    <ul>
                        <li><a href="catalog">Browse Books</a></li>
                        <li><a href="login">Login</a></li>
                        <li><a href="#">Contact</a></li>
                    </ul>
                </div>
                <div class="footer-section">
                    <h3>Connect</h3>
                    <div class="social-links">
                        <a href="#"><i class="fab fa-facebook"></i></a>
                        <a href="#"><i class="fab fa-twitter"></i></a>
                        <a href="#"><i class="fab fa-instagram"></i></a>
                    </div>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; 2025 Library-Thien. All rights reserved.</p>
            </div>
        </footer>
    </body>
</html>

