<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html>
    <head>
        <meta charset="UTF-8">
        <title>Statistic</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

        <style>
            body {
                font-family: Arial;
                background: #f4f6f9;
                margin: 0;
            }

            .container {
                margin: 6rem auto;
                width: 90%;
                max-width: 1200px;
            }

            .stats-container {
                display: flex;
                gap: 20px;
            }

            .stats-box {
                flex: 1;
                background: white;
                padding: 20px;
                border-radius: 10px;
                text-align: center;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }

            .stats-box h3 {
                margin: 0;
                color: #666;
            }

            .stats-box p {
                font-size: 24px;
                color: #007bff;
                margin-top: 10px;
            }

            .chart-container {
                display: flex;
                gap: 20px;
                margin-top: 20px;
            }

            .chart-box {
                flex: 1;
                background: white;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }

            canvas {
                height: 300px !important;
            }
        </style>
    </head>

    <body>

        <header>
            <%@include file="template/headerAdmin.jsp" %>
        </header>

        <div class="container">

            <!-- 🔹 STATS -->
            <div class="stats-container">
                <div class="stats-box">
                    <h3>Total Books</h3>
                    <p id="totalBooks">0</p>
                </div>
                <div class="stats-box">
                    <h3>Total Users</h3>
                    <p id="totalUsers">0</p>
                </div>
                <div class="stats-box">
                    <h3>Total Borrows</h3>
                    <p id="totalBorrows">0</p>
                </div>
                <div class="stats-box">
                    <h3>Total Overdue</h3>
                    <p id="totalOverdue">0</p>
                </div>
            </div>

            <!-- 🔹 CHART -->
            <div class="chart-container">

                <!-- Line -->
                <div class="chart-box">
                    <h3>Borrow vs Overdue</h3>
                    <canvas id="lineChart"></canvas>
                </div>

                <!-- Pie -->
                <div class="chart-box">
                    <h3>Category Distribution</h3>
                    <canvas id="pieChart"></canvas>
                </div>

            </div>
        </div>

        <footer>
            <%@include file="template/footer.jsp" %>
        </footer>

        <script>
    fetch('<%= request.getContextPath()%>/chartServlet')
            .then(res => res.json())
            .then(data => {

                // 🔹 FIX KEY
                document.getElementById('totalBooks').textContent = data.totalBooks || 0;
                document.getElementById('totalUsers').textContent = data.totalUsers || 0;
                document.getElementById('totalBorrows').textContent = data.totalBorrows || 0;
                document.getElementById('totalOverdue').textContent = data.totalOverdue || 0;

                console.log(data);

                // 🔹 LINE CHART
                new Chart(document.getElementById('lineChart'), {
                    type: 'line',
                    data: {
                        labels: data.months || [],
                        datasets: [
                            {
                                label: 'Borrow',
                                data: data.bookStats || [],
                                borderWidth: 2,
                                fill: false
                            },
                            {
                                label: 'Overdue',
                                data: data.overdueStats || [],
                                borderDash: [5, 5],
                                borderWidth: 2,
                                fill: false
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });

                // 🔹 PIE CHART
                new Chart(document.getElementById('pieChart'), {
                    type: 'doughnut',
                    data: {
                        labels: data.categoryLabels || [],
                        datasets: [{
                                data: data.categoryData || []
                            }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                position: 'right'
                            }
                        }
                    }
                });

            })
            .catch(err => console.error(err));
        </script>

    </body>
</html>