package it.northwind.gruppo2.dao;

import it.northwind.gruppo2.models.Category;
import it.northwind.gruppo2.utils.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

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
}
