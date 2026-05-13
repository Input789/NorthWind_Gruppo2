package it.northwind.gruppo2.models.Servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.northwind.gruppo2.models.Category;
import it.northwind.gruppo2.models.Product;
import jakarta.persistence.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/products")
public class ProductServlet extends HttpServlet {
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    private EntityManagerFactory emf;
    
    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("default");
    }
    
    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String idParam = request.getParameter("id");
        
        try {
            EntityManager em = emf.createEntityManager();
            
            if (idParam != null) {
                // GET singolo prodotto per ID
                int id = Integer.parseInt(idParam);
                Product product = em.find(Product.class, id);
                
                if (product != null) {
                    response.getWriter().write(gson.toJson(product));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write(gson.toJson(new ErrorResponse("Prodotto non trovato")));
                }
            } else {
                // GET lista di tutti i prodotti
                List<Product> products = em.createQuery(
                        "SELECT p FROM Product p", Product.class)
                        .getResultList();
                response.getWriter().write(gson.toJson(products));
            }
            
            em.close();
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new ErrorResponse("ID non valido")));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new ErrorResponse("Errore: " + e.getMessage())));
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Leggi il JSON dal body della richiesta
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            Product product = gson.fromJson(sb.toString(), Product.class);
            
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            
            // Se è stata fornita una categoria, la carica dal DB
            if (product.getCategory() != null && product.getCategory().getCategoryId() != 0) {
                Category category = em.find(Category.class, product.getCategory().getCategoryId());
                product.setCategory(category);
            }
            
            tx.begin();
            em.persist(product);
            tx.commit();
            
            em.close();
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Prodotto creato con successo", product)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new ErrorResponse("Errore: " + e.getMessage())));
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String idParam = request.getParameter("id");
        
        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new ErrorResponse("ID non fornito")));
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            
            // Leggi il JSON dal body della richiesta
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            Product productData = gson.fromJson(sb.toString(), Product.class);
            
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            
            Product product = em.find(Product.class, id);
            
            if (product == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(new ErrorResponse("Prodotto non trovato")));
                em.close();
                return;
            }
            
            tx.begin();
            product.setProductName(productData.getProductName());
            product.setSupplierId(productData.getSupplierId());
            
            // Gestisci la categoria
            if (productData.getCategory() != null && productData.getCategory().getCategoryId() != 0) {
                Category category = em.find(Category.class, productData.getCategory().getCategoryId());
                product.setCategory(category);
            }
            
            product.setQuantityPerUnit(productData.getQuantityPerUnit());
            product.setUnitPrice(productData.getUnitPrice());
            product.setUnitsInStock(productData.getUnitsInStock());
            product.setUnitsOnOrder(productData.getUnitsOnOrder());
            product.setReorderLevel(productData.getReorderLevel());
            product.setDiscontinued(productData.getDiscontinued());
            
            em.merge(product);
            tx.commit();
            
            em.close();
            
            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Prodotto aggiornato con successo", product)));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new ErrorResponse("ID non valido")));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new ErrorResponse("Errore: " + e.getMessage())));
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String idParam = request.getParameter("id");
        
        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new ErrorResponse("ID non fornito")));
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            
            Product product = em.find(Product.class, id);
            
            if (product == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(new ErrorResponse("Prodotto non trovato")));
                em.close();
                return;
            }
            
            tx.begin();
            em.remove(product);
            tx.commit();
            
            em.close();
            
            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Prodotto eliminato con successo", null)));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new ErrorResponse("ID non valido")));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new ErrorResponse("Errore: " + e.getMessage())));
        }
    }
    
    // Classi helper per le risposte JSON
    static class SuccessResponse {
        String message;
        Object data;
        
        SuccessResponse(String message, Object data) {
            this.message = message;
            this.data = data;
        }
    }
    
    static class ErrorResponse {
        String error;
        
        ErrorResponse(String error) {
            this.error = error;
        }
    }
}
