package it.northwind.gruppo2.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.northwind.gruppo2.dao.CategoryDAO;
import it.northwind.gruppo2.models.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/api/categories")
public class CategoryServlet extends HttpServlet {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareJsonResponse(response);
        String idParam = request.getParameter("id");

        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                Category category = categoryDAO.findById(id);

                if (category != null) {
                    response.getWriter().write(gson.toJson(toCategoryResponse(category)));
                } else {
                    writeError(response, HttpServletResponse.SC_NOT_FOUND, "Categoria non trovata");
                }
            } else {
                List<Category> categories = categoryDAO.findAll();
                response.getWriter().write(gson.toJson(toCategoryResponses(categories)));
            }
        } catch (NumberFormatException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID non valido");
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareJsonResponse(response);

        try {
            Category category = gson.fromJson(readBody(request), Category.class);
            Category savedCategory = categoryDAO.save(category);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Categoria creata con successo", toCategoryResponse(savedCategory))));
        } catch (IllegalArgumentException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareJsonResponse(response);
        String idParam = request.getParameter("id");

        if (idParam == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID non fornito");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Category categoryData = gson.fromJson(readBody(request), Category.class);
            Optional<Category> updatedCategory = categoryDAO.update(id, categoryData);

            if (updatedCategory.isEmpty()) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Categoria non trovata");
                return;
            }

            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Categoria aggiornata con successo", toCategoryResponse(updatedCategory.get()))));
        } catch (NumberFormatException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID non valido");
        } catch (IllegalArgumentException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareJsonResponse(response);
        String idParam = request.getParameter("id");

        if (idParam == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID non fornito");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            boolean deleted = categoryDAO.delete(id);

            if (!deleted) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Categoria non trovata");
                return;
            }

            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Categoria eliminata con successo", null)));
        } catch (NumberFormatException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "ID non valido");
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore: " + e.getMessage());
        }
    }

    private void prepareJsonResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(gson.toJson(new ErrorResponse(message)));
    }

    private List<CategoryResponse> toCategoryResponses(List<Category> categories) {
        List<CategoryResponse> responses = new ArrayList<>();
        for (Category category : categories) {
            responses.add(toCategoryResponse(category));
        }
        return responses;
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getDescription()
        );
    }

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

    static class CategoryResponse {
        int categoryId;
        String categoryName;
        String description;

        CategoryResponse(int categoryId, String categoryName, String description) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.description = description;
        }
    }
}
