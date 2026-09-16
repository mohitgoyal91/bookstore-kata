package be.bnpparibasfortis.bookstore.book.repository;

import be.bnpparibasfortis.bookstore.book.repository.entity.BookEntity;
import be.bnpparibasfortis.bookstore.book.service.models.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, UUID> {
    List<BookEntity> findByQuantityGreaterThan(int quantity);
    List<BookEntity> findByQuantityEquals(int quantity);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BookEntity b WHERE b.id = :id")
    Optional<BookEntity> findByIdForUpdate(UUID id);
}
