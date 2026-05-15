package it.northwind.gruppo2.dao;

import it.northwind.gruppo2.models.Category;
import it.northwind.gruppo2.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class CategoryDAO {

    public List<Category> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from Category order by categoryName", Category.class)
                    .getResultList();
        }
    }

    public Category findById(int categoryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Category.class, categoryId);
        }
    }

    public Category save(Category category) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            validate(category);
            session.persist(category);
            transaction.commit();
            return category;
        } catch (RuntimeException ex) {
            rollback(transaction);
            throw ex;
        }
    }

    public Optional<Category> update(int categoryId, Category updatedCategory) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Category category = session.get(Category.class, categoryId);

            if (category == null) {
                transaction.rollback();
                return Optional.empty();
            }

            validate(updatedCategory);
            category.setCategoryName(updatedCategory.getCategoryName());
            category.setDescription(updatedCategory.getDescription());
            transaction.commit();
            return Optional.of(category);
        } catch (RuntimeException ex) {
            rollback(transaction);
            throw ex;
        }
    }

    public boolean delete(int categoryId) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Category category = session.get(Category.class, categoryId);

            if (category == null) {
                transaction.rollback();
                return false;
            }

            session.remove(category);
            transaction.commit();
            return true;
        } catch (RuntimeException ex) {
            rollback(transaction);
            throw ex;
        }
    }

    private void validate(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("La categoria non puo' essere null.");
        }

        if (category.getCategoryName() == null || category.getCategoryName().isBlank()) {
            throw new IllegalArgumentException("Il nome della categoria e' obbligatorio.");
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}
