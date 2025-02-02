package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@Component
public class BookDaoImpl implements BookDao {

    private final EntityManagerFactory emf;

    public BookDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Book getById(Long id) {
        Book book = getEntityManager().find(Book.class, id);
        getEntityManager().close();
        return book;
    }

    @Override
    public Book findBookByTitle(String title) {
        EntityManager em = getEntityManager();
        TypedQuery<Book> query = em.createNamedQuery("book_find_by_title", Book.class);
        query.setParameter("title", title);
        Book book = query.getSingleResult();
        em.close();
        return book;
    }

    @Override
    public Book findBookByTitleCriteria(String title) {
        EntityManager em = getEntityManager();
        try{
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
            Root<Book> root = criteriaQuery.from(Book.class);
            ParameterExpression<String> titleParam = criteriaBuilder.parameter(String.class);
            Predicate titlePredicate = criteriaBuilder.equal(root.get("title"), titleParam);
            criteriaQuery.select(root).where(titlePredicate);
            TypedQuery<Book> typedQuery = em.createQuery(criteriaQuery);
            typedQuery.setParameter(titleParam, title);
            return typedQuery.getSingleResult();
        } finally {
             em.close();
        }
    }

    @Override
    public Book findBookByTitleNative(String title) {
        EntityManager em = getEntityManager();
        try{
          Query query = em.createNativeQuery("SELECT * FROM book WHERE title = :title", Book.class);
          query.setParameter("title", title);
          return (Book) query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Book saveNewBook(Book book) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.flush();
        em.getTransaction().commit();
        em.close();
        return book;
    }

    @Override
    public Book updateBook(Book book) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.merge(book);
        em.flush();
        em.getTransaction().commit();
        em.close();
        return null;
    }

    @Override
    public void deleteBookById(Long id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        Book book = em.find(Book.class, id);
        em.remove(book);

        em.flush();
        em.clear();
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Book findByISBN(String isbn) {
        EntityManager em = getEntityManager();
        try{
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
            query.setParameter("isbn", isbn);
            Book book = query.getSingleResult();
            return book;
        }finally{
            em.close();
        }

    }

    @Override
    public List<Book> findAll() {
        EntityManager em = getEntityManager();
        try{
            TypedQuery<Book> query = em.createNamedQuery("book_find_all", Book.class);
            return query.getResultList();
        }
        finally {
            em.close();
        }
    }

    @Override
    public List<Book> findAll(Pageable pageable) {
        EntityManager em = getEntityManager();
        try{
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b", Book.class);
            query.setFirstResult(Math.toIntExact(pageable.getOffset()));
            query.setMaxResults(pageable.getPageSize());
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Book> findAllBooksSortByTitle(Pageable pageable) {
        EntityManager em = getEntityManager();
        try{
            String jpql = "SELECT b FROM Book b ORDER BY b.title " + (pageable.getSort().getOrderFor("title") == null ? "DESC" : pageable.getSort().getOrderFor("title").getDirection().name());
            TypedQuery<Book> query = em.createQuery(jpql, Book.class);
            query.setFirstResult(Math.toIntExact(pageable.getOffset()));
            query.setMaxResults(pageable.getPageSize());
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    private EntityManager getEntityManager(){
        return emf.createEntityManager();
    }
}
