package it.northwind.gruppo2.dao;

import it.northwind.gruppo2.models.Product;
import it.northwind.gruppo2.models.Category;
import it.northwind.gruppo2.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ProductDAO {

    public List<Product> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("""
                            select p
                            from Product p
                            left join fetch p.category
                            order by p.productId
                            """, Product.class)
                    .getResultList();
        }
    }

    public Optional<Product> findById(int productId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Product product = session
                    .createQuery("""
                            select p
                            from Product p
                            left join fetch p.category
                            where p.productId = :productId
                            """, Product.class)
                    .setParameter("productId", productId)
                    .uniqueResult();
            return Optional.ofNullable(product);
        }
    }

    public Product save(Product product) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            validate(product);
            product.setCategory(resolveCategory(session, product.getCategory()));
            session.persist(product);
            transaction.commit();
            return product;
        } catch (RuntimeException ex) {
            rollback(transaction);
            throw ex;
        }
    }

    public Optional<Product> update(int productId, Product updatedProduct) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Product product = session.get(Product.class, productId);

            if (product == null) {
                transaction.rollback();
                return Optional.empty();
            }

            validate(updatedProduct);
            updatedProduct.setCategory(resolveCategory(session, updatedProduct.getCategory()));
            copyEditableFields(updatedProduct, product);
            transaction.commit();
            return Optional.of(product);
        } catch (RuntimeException ex) {
            rollback(transaction);
            throw ex;
        }
    }

    public boolean delete(int productId) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Product product = session.get(Product.class, productId);

            if (product == null) {
                transaction.rollback();
                return false;
            }

            session.remove(product);
            transaction.commit();
            return true;
        } catch (RuntimeException ex) {
            rollback(transaction);
            throw ex;
        }
    }

    private void copyEditableFields(Product source, Product destination) {
        destination.setProductName(source.getProductName());
        destination.setSupplierId(source.getSupplierId());
        destination.setCategory(source.getCategory());
        destination.setQuantityPerUnit(source.getQuantityPerUnit());
        destination.setUnitPrice(source.getUnitPrice());
        destination.setUnitsInStock(source.getUnitsInStock());
        destination.setUnitsOnOrder(source.getUnitsOnOrder());
        destination.setReorderLevel(source.getReorderLevel());
        destination.setDiscontinued(source.getDiscontinued());
    }

    private Category resolveCategory(Session session, Category category) {
        if (category == null || category.getCategoryId() <= 0) {
            return null;
        }

        Category managedCategory = session.get(Category.class, category.getCategoryId());
        if (managedCategory == null) {
            throw new IllegalArgumentException("Categoria non trovata: " + category.getCategoryId());
        }

        return managedCategory;
    }

    private void validate(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Il prodotto non puo' essere null.");
        }

        if (product.getProductName() == null || product.getProductName().isBlank()) {
            throw new IllegalArgumentException("Il nome del prodotto e' obbligatorio.");
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}
