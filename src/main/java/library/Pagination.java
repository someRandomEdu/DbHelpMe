package library;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import library.entity.Book;

import java.util.List;

public class Pagination<T> {
    private final Grid<T> grid;
    private List<T> items;  // This holds all the items to paginate
    private final int pageSize;
    private int currentPage = 0;

    public Pagination(Grid<T> grid, List<T> items, int pageSize) {
        this.grid = grid;
        this.items = items;
        this.pageSize = pageSize;
        updateGrid();
    }

    public HorizontalLayout getLayout() {
        Button previousButton = new Button("Previous", e -> {
            if (currentPage > 0) {
                currentPage--;
                updateGrid();
            }
        });

        Button nextButton = new Button("Next", e -> {
            if ((currentPage + 1) * pageSize < items.size()) {
                currentPage++;
                updateGrid();
            }
        });

        HorizontalLayout layout = new HorizontalLayout(previousButton, nextButton);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        return layout;
    }

    public void update(String searchFilter) {
        currentPage = 0;
        updateGrid(searchFilter);
    }

    private void updateGrid() {
        updateGrid("");
    }

    private void updateGrid(String searchFilter) {
        List<T> filteredItems = items.stream()
                .filter(item -> matchesSearch(item, searchFilter))
                .toList();
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, filteredItems.size());
        grid.setItems(filteredItems.subList(start, end));
    }

    private boolean matchesSearch(T item, String searchFilter) {
        // Implement filter logic here (for example, matching a field of the item with the searchFilter)
        return true;  // For now, it just returns true for every item (no actual filtering)
    }

    // This method will allow you to reset the items and reinitialize the grid
    public void setItems(List<T> items) {
        this.items = items;
        this.currentPage = 0;  // Reset to the first page
        updateGrid();  // Update the grid with the new items
    }

    // This method can now be modified to use 'items' instead of 'fullList'
    public List<T> getCurrentPageItems() {
        int fromIndex = Math.min(currentPage * pageSize, items.size());
        int toIndex = Math.min(fromIndex + pageSize, items.size());
        return items.subList(fromIndex, toIndex);
    }
}
