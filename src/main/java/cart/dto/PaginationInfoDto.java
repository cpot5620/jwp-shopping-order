package cart.dto;

public class PaginationInfoDto {

    private final int total;
    private final int perPage;
    private final int currentPage;
    private final int lastPage;

    public PaginationInfoDto(final int total, final int perPage, final int currentPage, final int lastPage) {
        this.total = total;
        this.perPage = perPage;
        this.currentPage = currentPage;
        this.lastPage = lastPage;
    }

    public int getTotal() {
        return total;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLastPage() {
        return lastPage;
    }
}
