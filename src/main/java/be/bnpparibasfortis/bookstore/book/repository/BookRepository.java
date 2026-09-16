package be.bnpparibasfortis.bookstore.book.repository;

import be.bnpparibasfortis.bookstore.book.repository.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, UUID> {
}
