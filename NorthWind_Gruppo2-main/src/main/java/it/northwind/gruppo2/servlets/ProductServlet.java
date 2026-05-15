package it.northwind.gruppo2.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.northwind.gruppo2.dao.ProductDAO;
import it.northwind.gruppo2.models.Product;
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

@WebServlet("/api/products")
public class ProductServlet extends HttpServlet {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareJsonResponse(response);
        String idParam = request.getParameter("id");

        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                Optional<Product> product = productDAO.findById(id);

                if (product.isPresent()) {
                    response.getWriter().write(gson.toJson(toProductResponse(product.get())));
                } else {
                    writeError(response, HttpServletResponse.SC_NOT_FOUND, "Prodotto non trovato");
                }
            } else {
                List<Product> products = productDAO.findAll();
                response.getWriter().write(gson.toJson(toProductResponses(products)));
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
            Product product = gson.fromJson(readBody(request), Product.class);
            Product savedProduct = productDAO.save(product);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Prodotto creato con successo", toProductResponse(savedProduct))));
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
            Product productData = gson.fromJson(readBody(request), Product.class);
            Optional<Product> updatedProduct = productDAO.update(id, productData);

            if (updatedProduct.isEmpty()) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Prodotto non trovato");
                return;
            }

            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Prodotto aggiornato con successo", toProductResponse(updatedProduct.get()))));
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
            boolean deleted = productDAO.delete(id);

            if (!deleted) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Prodotto non trovato");
                return;
            }

            response.getWriter().write(gson.toJson(
                    new SuccessResponse("Prodotto eliminato con successo", null)));
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

    private List<ProductResponse> toProductResponses(List<Product> products) {
        List<ProductResponse> responses = new ArrayList<>();
        for (Product product : products) {
            responses.add(toProductResponse(product));
        }
        return responses;
    }

    private ProductResponse toProductResponse(Product product) {
        CategorySummary category = null;
        if (product.getCategory() != null) {
            category = new CategorySummary(
                    product.getCategory().getCategoryId(),
                    product.getCategory().getCategoryName()
            );
        }

        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getSupplierId(),
                category,
                product.getQuantityPerUnit(),
                product.getUnitPrice(),
                product.getUnitsInStock(),
                product.getUnitsOnOrder(),
                product.getReorderLevel(),
                product.getDiscontinued()
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

    static class ProductResponse {
        int productId;
        String productName;
        Integer supplierId;
        CategorySummary category;
        String quantityPerUnit;
        Double unitPrice;
        Integer unitsInStock;
        Integer unitsOnOrder;
        Integer reorderLevel;
        String discontinued;

        ProductResponse(int productId, String productName, Integer supplierId, CategorySummary category,
                        String quantityPerUnit, Double unitPrice, Integer unitsInStock,
                        Integer unitsOnOrder, Integer reorderLevel, String discontinued) {
            this.productId = productId;
            this.productName = productName;
            this.supplierId = supplierId;
            this.category = category;
            this.quantityPerUnit = quantityPerUnit;
            this.unitPrice = unitPrice;
            this.unitsInStock = unitsInStock;
            this.unitsOnOrder = unitsOnOrder;
            this.reorderLevel = reorderLevel;
            this.discontinued = discontinued;
        }
    }

    static class CategorySummary {
        int categoryId;
        String categoryName;

        CategorySummary(int categoryId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }
    }
}
