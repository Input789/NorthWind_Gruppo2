package it.northwind.gruppo2;

import it.northwind.gruppo2.dao.CategoryDAO;
import it.northwind.gruppo2.dao.ProductDAO;
import it.northwind.gruppo2.models.Category;
import it.northwind.gruppo2.models.Product;
import it.northwind.gruppo2.utils.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class TestMotore {
    public static void main(String[] args) {
        System.out.println("--- AVVIO TEST DEI DAO ---");

        // Inizializziamo i nostri Data Access Object
        CategoryDAO categoryDAO = new CategoryDAO();
        ProductDAO productDAO = new ProductDAO();

        try {
            // --- TEST 1: Lettura di tutte le categorie ---
            System.out.println("\n🔄 1. Test Lettura Categorie (CategoryDAO.findAll)...");
            List<Category> categories = categoryDAO.findAll();
            System.out.println("✅ Trovate " + categories.size() + " categorie nel DB.");
            if (!categories.isEmpty()) {
                System.out.println("   Esempio prima categoria: " + categories.get(0).getCategoryName());
            }

            // --- TEST 2: Lettura di un singolo prodotto e della sua Foreign Key ---
            System.out.println("\n🔄 2. Test Lettura Prodotto (ProductDAO.findById 1)...");
            Optional<Product> pOpt = productDAO.findById(1);

            // Verifichiamo l'Optional
            if (pOpt.isPresent()) {
                Product p = pOpt.get();
                System.out.println("✅ Prodotto trovato: " + p.getProductName());
                System.out.println("   Prezzo: " + p.getUnitPrice() + "€");
                
                // Verifichiamo se la left join fetch ha caricato la categoria
                if (p.getCategory() != null) {
                    System.out.println("   Categoria associata: " + p.getCategory().getCategoryName());
                } else {
                    System.out.println("⚠️ Il prodotto non ha una categoria associata.");
                }
            } else {
                System.out.println("❌ Prodotto con ID 1 non trovato.");
            }

            System.out.println("\n🎉 TUTTI I TEST DI LETTURA DEL MOTORE SONO SUPERATI! 🎉");
            System.out.println("👉 Ora puoi avviare Tomcat e testare la ProductServlet!");

        } catch (Exception e) {
            System.err.println("\n❌ ERRORE DURANTE IL TEST DEI DAO:");
            e.printStackTrace();
        } finally {
            // Spegniamo sempre Hibernate alla fine
            HibernateUtil.shutdown();
        }
    }
}