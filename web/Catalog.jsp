<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Catalog</title>

        <!-- CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styleCatalog.css">

        <!-- Fonts + Icons -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>

    <body>

        <header>
            <%@include file="template/header.jsp" %>
        </header>

        <main class="content">

            <!-- HERO -->
            <section class="heros">
                <div class="heros-content">
                    <h1>Discover Your Next Book</h1>
                    <p>Explore our collection and start your journey 📚</p>
                    <a href="#search" class="cta-buttonn">Browse Books</a>
                </div>
            </section>

            <!-- SEARCH -->
            <section id="search" class="search-section">
                <div class="search-container">

                    <!-- Search box -->
                    <div class="search-box">
                        <form action="catalog" method="post" style="display:flex;width:100%">
                            <input type="text" name="keyword"
                                   class="search-input"
                                   placeholder="Search..."
                                   value="${param.keyword}">
                            <button type="submit" class="cta-buttonn">Search</button>
                        </form>
                    </div>

                    <!-- Filter -->
                    <div class="search-filters">
                        <form id="filterForm" action="catalog" method="post">
                            <input type="hidden" name="category" id="categoryInput" value="${param.category}">

                            <button type="button" class="filter-btn ${empty param.category || param.category=='All' ? 'active' : ''}" onclick="submitFilter('All')">All</button>
                            <button type="button" class="filter-btn ${param.category=='Literature' ? 'active' : ''}" onclick="submitFilter('Literature')">Literature</button>
                            <button type="button" class="filter-btn ${param.category=='Science Fiction' ? 'active' : ''}" onclick="submitFilter('Science Fiction')">Sci-Fi</button>
                            <button type="button" class="filter-btn ${param.category=='Technology' ? 'active' : ''}" onclick="submitFilter('Technology')">Tech</button>
                            <button type="button" class="filter-btn ${param.category=='Education' ? 'active' : ''}" onclick="submitFilter('Education')">Education</button>
                        </form>
                    </div>

                </div>
            </section>

            <!-- BOOK LIST -->
            <section class="books-session">
                <h2>Search Results</h2>

                <div class="book_grid">

                    <!-- Empty -->
                    <c:if test="${empty requestScope.books}">
                        <p style="color:red;text-align:center;">
                            No books found 😢
                        </p>
                    </c:if>

                    <!-- List -->
                    <c:forEach var="book" items="${requestScope.books}">
                        <div class="book-card">

                            <!-- IMAGE (FIX CHÍNH Ở ĐÂY) -->
                            <img 
                                src="${pageContext.request.contextPath}/${book.imgUrl}" 
                                alt="${book.title}" 
                                class="book-img"
                                onerror="this.src='${pageContext.request.contextPath}/img/bookdefault.jpg'"
                                />

                            <h3 class="book-title">${book.title}</h3>
                            <p class="book-author">${book.author}</p>

                            <!-- DETAIL -->
                            <form action="viewdetail" method="get">
                                <input type="hidden" name="bookId" value="${book.bookId}">
                                <button type="submit" class="view-detail-btn">
                                    View Detail
                                </button>
                            </form>

                        </div>
                    </c:forEach>

                </div>
            </section>

        </main>

        <footer>
            <%@include file="template/footer.jsp" %>
        </footer>

        <!-- JS -->
        <script>
            function submitFilter(category) {
                document.getElementById("categoryInput").value = category;
                document.getElementById("filterForm").submit();
            }
        </script>

    </body>
</html>