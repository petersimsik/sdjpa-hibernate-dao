package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookDao {
    Book getById(Long id);
    Book findBookByTitle(String title);
    Book findBookByTitleCriteria(String title);
    Book findBookByTitleNative(String title);
    Book saveNewBook(Book book);
    Book updateBook(Book book);
    void deleteBookById(Long id);
    Book findByISBN(String isbn);
    List<Book> findAll();
    List<Book> findAll(Pageable pageable);
    List<Book> findAllBooksSortByTitle(Pageable pageable);
}
