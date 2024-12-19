package library.SonTest;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.Route;
import library.entity.CurrentUser;
import library.views.MainView;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "book-chart", layout = MainView.class)
public class BookChartView extends Div {

    private final DatabaseService databaseService;

    @Autowired
    public BookChartView(DatabaseService databaseService) {
        this.databaseService = databaseService;
        Integer userId = CurrentUser.getId();

        // Create main layout
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setHeightFull();
        mainLayout.setPadding(true);
        mainLayout.setSpacing(true);

        // Create two vertical layouts for 2x2 grid
        VerticalLayout leftColumn = new VerticalLayout();
        VerticalLayout rightColumn = new VerticalLayout();

        // Add charts to columns
        createUserBooksChart(userId, leftColumn);
        createRentedBooksChart(userId, leftColumn);
        createNotificationsChart(userId, rightColumn);
        createFeedbacksChart(userId, rightColumn);

        mainLayout.add(leftColumn, rightColumn);
        add(mainLayout);
    }

    private void createUserBooksChart(Integer userId, VerticalLayout container) {
        int rentedBooks = databaseService.countDistinctRentedBooks(userId);
        int returnedBooks = databaseService.countDistinctReturnedBooks(userId);
        int wishlistBooks = databaseService.countWishlistBooks(userId);

        Div chartContainer = new Div();
        chartContainer.setId("bookChart");
        chartContainer.getStyle()
                .set("width", "45vw")
                .set("height", "45vh");
        container.add(chartContainer);

        getElement().getNode().runWhenAttached(ui -> {
            Page page = ui.getPage();
            page.executeJs("const script = document.createElement('script');"
                    + "script.src = 'https://cdn.jsdelivr.net/npm/chart.js';"
                    + "script.onload = function() {"
                    + "    const canvas = document.createElement('canvas');"
                    + "    canvas.id = 'bookChartCanvas';"
                    + "    document.getElementById('bookChart').appendChild(canvas);"
                    + "    const ctx = canvas.getContext('2d');"
                    + "    new Chart(ctx, {"
                    + "        type: 'pie',"
                    + "        data: {"
                    + "            labels: ['Currently Borrowed (' + " + rentedBooks + " + ')', 'Returned (' + " + returnedBooks + " + ')', 'Wishlist (' + " + wishlistBooks + " + ')'],"
                    + "            datasets: [{"
                    + "                data: [" + rentedBooks + ", " + returnedBooks + ", " + wishlistBooks + "],"
                    + "                backgroundColor: ['#4CAF50', '#2196F3', '#FFC107'],"
                    + "                hoverOffset: 4"
                    + "            }]"
                    + "        },"
                    + "        options: {"
                    + "            responsive: true,"
                    + "            plugins: {"
                    + "                title: {"
                    + "                    display: true,"
                    + "                    text: 'Book Status Distribution'"
                    + "                },"
                    + "                legend: {"
                    + "                    position: 'top'"
                    + "                }"
                    + "            }"
                    + "        }"
                    + "    });"
                    + "};"
                    + "document.head.appendChild(script);");
        });
    }

    private void createRentedBooksChart(Integer userId, VerticalLayout container) {
        int ontimeRentedBooks = databaseService.countOntimeRentedBooks(userId);
        int overdueRentedBooks = databaseService.countOverdueRentedBooks(userId);

        Div chartContainer = new Div();
        chartContainer.setId("rentedBooksChart");
        chartContainer.getStyle()
                .set("width", "45vw")
                .set("height", "45vh");
        container.add(chartContainer);

        getElement().getNode().runWhenAttached(ui -> {
            Page page = ui.getPage();
            page.executeJs("const script = document.createElement('script');"
                    + "script.src = 'https://cdn.jsdelivr.net/npm/chart.js';"
                    + "script.onload = function() {"
                    + "    const canvas = document.createElement('canvas');"
                    + "    canvas.id = 'rentedBooksChartCanvas';"
                    + "    document.getElementById('rentedBooksChart').appendChild(canvas);"
                    + "    const ctx = canvas.getContext('2d');"
                    + "    new Chart(ctx, {"
                    + "        type: 'bar',"
                    + "        data: {"
                    + "            labels: ['On Time (' + " + ontimeRentedBooks + " + ')', 'Overdue (' + " + overdueRentedBooks + " + ')'],"
                    + "            datasets: [{"
                    + "                label: 'Currently Borrowed Books',"
                    + "                data: [" + ontimeRentedBooks + ", " + overdueRentedBooks + "],"
                    + "                backgroundColor: ['#4CAF50', '#FF5733'],"
                    + "                borderColor: ['#4CAF50', '#FF5733'],"
                    + "                borderWidth: 1"
                    + "            }]"
                    + "        },"
                    + "        options: {"
                    + "            responsive: true,"
                    + "            plugins: {"
                    + "                title: {"
                    + "                    display: true,"
                    + "                    text: 'Borrowed Books Status'"
                    + "                },"
                    + "                legend: { position: 'top' }"
                    + "            },"
                    + "            scales: {"
                    + "                y: { beginAtZero: true }"
                    + "            }"
                    + "        }"
                    + "    });"
                    + "};"
                    + "document.head.appendChild(script);");
        });
    }

    private void createNotificationsChart(Integer userId, VerticalLayout container) {
        int unreadNotifications = databaseService.countUnreadNotifications(userId);
        int readNotifications = databaseService.countReadNotifications(userId);

        Div chartContainer = new Div();
        chartContainer.setId("notificationsChart");
        chartContainer.getStyle()
                .set("width", "45vw")
                .set("height", "45vh");
        container.add(chartContainer);

        getElement().getNode().runWhenAttached(ui -> {
            Page page = ui.getPage();
            page.executeJs("const script = document.createElement('script');"
                    + "script.src = 'https://cdn.jsdelivr.net/npm/chart.js';"
                    + "script.onload = function() {"
                    + "    const canvas = document.createElement('canvas');"
                    + "    canvas.id = 'notificationsChartCanvas';"
                    + "    document.getElementById('notificationsChart').appendChild(canvas);"
                    + "    const ctx = canvas.getContext('2d');"
                    + "    new Chart(ctx, {"
                    + "        type: 'pie',"
                    + "        data: {"
                    + "            labels: ['Unread (' + " + unreadNotifications + " + ')', 'Read (' + " + readNotifications + " + ')'],"
                    + "            datasets: [{"
                    + "                data: [" + unreadNotifications + ", " + readNotifications + "],"
                    + "                backgroundColor: ['#FFC107', '#4CAF50'],"
                    + "                hoverOffset: 4"
                    + "            }]"
                    + "        },"
                    + "        options: {"
                    + "            responsive: true,"
                    + "            plugins: {"
                    + "                title: {"
                    + "                    display: true,"
                    + "                    text: 'Notifications Status'"
                    + "                },"
                    + "                legend: { position: 'top' }"
                    + "            }"
                    + "        }"
                    + "    });"
                    + "};"
                    + "document.head.appendChild(script);");
        });
    }

    private void createFeedbacksChart(Integer userId, VerticalLayout container) {
        int totalFeedbacks = databaseService.countTotalFeedbacks(userId);
        int pendingFeedbacks = databaseService.countPendingFeedbacks(userId);
        int handledFeedbacks = databaseService.countHandledFeedbacks(userId);

        Div chartContainer = new Div();
        chartContainer.setId("feedbacksChart");
        chartContainer.getStyle()
                .set("width", "45vw")
                .set("height", "45vh");
        container.add(chartContainer);

        getElement().getNode().runWhenAttached(ui -> {
            Page page = ui.getPage();
            page.executeJs("const script = document.createElement('script');"
                    + "script.src = 'https://cdn.jsdelivr.net/npm/chart.js';"
                    + "script.onload = function() {"
                    + "    const canvas = document.createElement('canvas');"
                    + "    canvas.id = 'feedbacksChartCanvas';"
                    + "    document.getElementById('feedbacksChart').appendChild(canvas);"
                    + "    const ctx = canvas.getContext('2d');"
                    + "    new Chart(ctx, {"
                    + "        type: 'bar',"
                    + "        data: {"
                    + "            labels: ['Pending Feedbacks', 'Handled Feedbacks'],"  // Labels for each feedback type
                    + "            datasets: [{"
                    + "                label: 'Feedback Count',"
                    + "                data: [" + pendingFeedbacks + ", " + handledFeedbacks + "],"
                    + "                backgroundColor: ['#FF9800', '#4CAF50'],"
                    + "                borderColor: ['#FF9800', '#4CAF50'],"
                    + "                borderWidth: 1"
                    + "            }]"
                    + "        }," // Data for the chart
                    + "        options: {"
                    + "            responsive: true,"
                    + "            plugins: {"
                    + "                title: {"
                    + "                    display: true,"
                    + "                    text: 'Feedback Summary for User (' + " + totalFeedbacks + " + ' Total Feedbacks)'"
                    + "                },"
                    + "                legend: { position: 'top' }"
                    + "            },"
                    + "            scales: {"
                    + "                y: { beginAtZero: true }"
                    + "            }"
                    + "        }"
                    + "    });"
                    + "};"
                    + "document.head.appendChild(script);");
        });
    }
}