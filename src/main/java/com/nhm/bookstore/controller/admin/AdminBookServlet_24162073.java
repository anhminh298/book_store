package com.nhm.bookstore.controller.admin;

import com.nhm.bookstore.model.Book_24162073;
import com.nhm.bookstore.model.Author_24162073;
import com.nhm.bookstore.service.BookService_24162073;
import com.nhm.bookstore.service.AuthorService_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@WebServlet("/admin/books")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class AdminBookServlet_24162073 extends HttpServlet {

    private String getValue(Part part) throws IOException {
        if (part == null) return null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), "UTF-8"));
        StringBuilder value = new StringBuilder();
        char[] buffer = new char[1024];
        int length;
        while ((length = reader.read(buffer)) != -1) {
            value.append(buffer, 0, length);
        }
        return value.toString();
    }

    private String getSubmittedFileName(Part part) {
        if (part == null) return null;
        String header = part.getHeader("content-disposition");
        if (header == null) return null;
        for (String cd : header.split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                if (fileName.isEmpty()) return null;
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        BookService_24162073 bookService = new BookService_24162073();
        AuthorService_24162073 authorService = new AuthorService_24162073();

        switch (action) {
            case "list":
                int page = 1;
                int pageSize = 5;
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try {
                        page = Integer.parseInt(pageParam);
                    } catch (NumberFormatException e) {
                        page = 1;
                    }
                }
                
                List<Book_24162073> books = bookService.getAll(page, pageSize);
                for (Book_24162073 book : books) {
                    book.setAuthors(authorService.getByBookId(book.getBookid()));
                }
                int totalCount = bookService.countAll();
                int totalPages = (int) Math.ceil((double) totalCount / pageSize);

                request.setAttribute("books", books);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.getRequestDispatcher("/views/admin/book-list.jsp").forward(request, response);
                break;

            case "create":
                List<Author_24162073> authors = authorService.getAll();
                request.setAttribute("authors", authors);
                request.getRequestDispatcher("/views/admin/book-form.jsp").forward(request, response);
                break;

            case "edit":
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.isEmpty()) {
                    int id = Integer.parseInt(idParam);
                    Book_24162073 book = bookService.getById(id);
                    List<Author_24162073> bookAuthors = authorService.getByBookId(id);
                    List<Author_24162073> allAuthors = authorService.getAll();
                    
                    request.setAttribute("book", book);
                    request.setAttribute("bookAuthors", bookAuthors);
                    request.setAttribute("authors", allAuthors);
                    request.getRequestDispatcher("/views/admin/book-edit.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/books");
                }
                break;

            case "delete":
                String delIdParam = request.getParameter("id");
                if (delIdParam != null && !delIdParam.isEmpty()) {
                    int id = Integer.parseInt(delIdParam);
                    boolean success = bookService.delete(id);
                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/admin/books?success=true");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/books?error=true");
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/books");
                }
                break;
                
            default:
                response.sendRedirect(request.getContextPath() + "/admin/books");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/books");
            return;
        }

        BookService_24162073 bookService = new BookService_24162073();

        switch (action) {
            case "create":
                try {
                    int isbn = Integer.parseInt(getValue(request.getPart("isbn")));
                    String title = getValue(request.getPart("title"));
                    String publisher = getValue(request.getPart("publisher"));
                    BigDecimal price = new BigDecimal(getValue(request.getPart("price")));
                    String description = getValue(request.getPart("description"));
                    String publishDateStr = getValue(request.getPart("publish_date"));
                    Date publishDate = null;
                    if (publishDateStr != null && !publishDateStr.isEmpty()) {
                        publishDate = Date.valueOf(publishDateStr);
                    }
                    int quantity = Integer.parseInt(getValue(request.getPart("quantity")));
                    int authorId = Integer.parseInt(getValue(request.getPart("author_id")));

                    Part filePart = request.getPart("coverImage");
                    String fileName = getSubmittedFileName(filePart);
                    String coverImage = null;

                    if (fileName != null && !fileName.isEmpty()) {
                        coverImage = System.currentTimeMillis() + "_" + fileName;
                        String uploadPath = getServletContext().getRealPath("/uploads/");
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        filePart.write(uploadPath + File.separator + coverImage);
                    }

                    Book_24162073 book = new Book_24162073();
                    book.setIsbn(isbn);
                    book.setTitle(title);
                    book.setPublisher(publisher);
                    book.setPrice(price);
                    book.setDescription(description);
                    book.setPublishDate(publishDate);
                    book.setQuantity(quantity);
                    book.setCoverImage(coverImage);

                    boolean success = bookService.create(book, authorId);
                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/admin/books?success=true");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/books?error=true");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/books?error=true");
                }
                break;

            case "edit":
                try {
                    String idParam = request.getParameter("id");
                    if (idParam == null || idParam.isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/books");
                        return;
                    }
                    int id = Integer.parseInt(idParam);
                    
                    int isbn = Integer.parseInt(getValue(request.getPart("isbn")));
                    String title = getValue(request.getPart("title"));
                    String publisher = getValue(request.getPart("publisher"));
                    BigDecimal price = new BigDecimal(getValue(request.getPart("price")));
                    String description = getValue(request.getPart("description"));
                    String publishDateStr = getValue(request.getPart("publish_date"));
                    Date publishDate = null;
                    if (publishDateStr != null && !publishDateStr.isEmpty()) {
                        publishDate = Date.valueOf(publishDateStr);
                    }
                    int quantity = Integer.parseInt(getValue(request.getPart("quantity")));
                    int authorId = Integer.parseInt(getValue(request.getPart("author_id")));

                    String oldCoverImage = getValue(request.getPart("oldCoverImage"));
                    Part filePart = request.getPart("coverImage");
                    String fileName = getSubmittedFileName(filePart);
                    String coverImage = oldCoverImage;

                    if (fileName != null && !fileName.isEmpty()) {
                        coverImage = System.currentTimeMillis() + "_" + fileName;
                        String uploadPath = getServletContext().getRealPath("/uploads/");
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        filePart.write(uploadPath + File.separator + coverImage);
                    }

                    Book_24162073 book = new Book_24162073();
                    book.setBookid(id);
                    book.setIsbn(isbn);
                    book.setTitle(title);
                    book.setPublisher(publisher);
                    book.setPrice(price);
                    book.setDescription(description);
                    book.setPublishDate(publishDate);
                    book.setQuantity(quantity);
                    book.setCoverImage(coverImage);

                    boolean success = bookService.update(book, authorId);
                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/admin/books?success=true");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/books?error=true");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/books?error=true");
                }
                break;
                
            default:
                response.sendRedirect(request.getContextPath() + "/admin/books");
                break;
        }
    }
}
