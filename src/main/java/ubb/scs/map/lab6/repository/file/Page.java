package ubb.scs.map.lab6.repository.file;

public class Page <E>{
    private Iterable<E> elements;
    private int totalElements;

    public Page(Iterable<E> elements, int totalElements) {
        this.elements = elements;
        this.totalElements = totalElements;
    }
    public Iterable<E> getElementsOnPage() {
        return elements;
    }
    public int getTotalElementsCount() {
        return totalElements;
    }
}
