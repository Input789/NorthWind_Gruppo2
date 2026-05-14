package it.northwind.gruppo2.models.Servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.northwind.gruppo2.models.Category;
import it.northwind.gruppo2.utils.HibernateUtil;
import org.hibernate.SessionFactory;
import jakarta.persistence.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/categories")
public class CategoryServlet extends HttpServlet {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private SessionFactory sessionFactory;

    @Override
    public void init() throws ServletException {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void destroy() {
        // HibernateUtil gestisce la chiusura globale
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("id");

        try {
            EntityManager em = sessionFactory.openSession();

            if (idParam != null) {
                // GET singola categoria per ID
                int id = Integer.parseInt(idParam);
                Category category = em.find(Category.class, id);

                if (category != null) {
                    response.getWriter().write(gson.toJson(category));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write(gson.toJson(new ErrorResponse("Categoria non trovata")));
                }
            } else {
                // GET lista di tutte le categorie
                List<Category> categories = em.createQuery(
                        "SELECT c FROM Category c", Category.class)
                        .getResultList();
                response.getWriter().write(gson.toJson(categories));
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

            Category category = gson.fromJson(sb.toString(), Category.class);

            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            tx.begin();
            em.persist(category);
            tx.commit();

            em.close();

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Categoria creata con successo", category)));
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

            Category categoryData = gson.fromJson(sb.toString(), Category.class);

            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            Category category = em.find(Category.class, id);

            if (category == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(new ErrorResponse("Categoria non trovata")));
                em.close();
                return;
            }

            tx.begin();
            category.setCategoryName(categoryData.getCategoryName());
            category.setDescription(categoryData.getDescription());
            category.setPicture(categoryData.getPicture());
            em.merge(category);
            tx.commit();

            em.close();

            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Categoria aggiornata con successo", category)));
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

            Category category = em.find(Category.class, id);

            if (category == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(new ErrorResponse("Categoria non trovata")));
                em.close();
                return;
            }

            tx.begin();
            em.remove(category);
            tx.commit();

            em.close();

            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Categoria eliminata con successo", null)));
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
