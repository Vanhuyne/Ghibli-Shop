package com.mvc.ecommerce.controller;


import com.mvc.ecommerce.entity.Account;
import com.mvc.ecommerce.entity.Category;
import com.mvc.ecommerce.entity.Order;
import com.mvc.ecommerce.entity.Product;
import com.mvc.ecommerce.service.*;
import com.mvc.ecommerce.utils.OrderExcelExporter;
import com.mvc.ecommerce.utils.UserExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AccountService accountService;
    private final HttpSession httpSession;
    private final ProductService productService;
    private final OrderService orderService;
    private final CartService cartService;
    private final CategoryService categoryService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size) {

    Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");

    if (loggedInUser == null || !loggedInUser.getAdmin()) {
        return "redirect:/login";
    }

    model.addAttribute("loggedInUser", loggedInUser);
    model.addAttribute("activeSection", "users");
    fetchUserData("users", page, size, model);
    model.addAttribute("sizeCart", cartService.getSizeCart(loggedInUser.getUsername()));

    return "admin/dashboard";
}

    @GetMapping("/dashboard/{activeSection}")
    public String adminAction(@PathVariable String activeSection, @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size, Model model) {
        model.addAttribute("activeSection", activeSection);

        // Fetch data for the respective sections using switch case
        fetchUserData(activeSection, page, size, model);

        addLoggedInUserInfoToModel(httpSession, model, cartService);
        return "admin/dashboard";
    }

    private void fetchUserData(String activeSection, int page, int size, Model model) {
        switch (activeSection) {
            case "users":
                Page<Account> usersPage = accountService.getUsersByPage(PageRequest.of(page, size));

                model.addAttribute("users", usersPage.getContent());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", usersPage.getTotalPages());
                model.addAttribute("user", new Account());
                break;

            case "products":
                Page<Product> productsPage = productService.getAllProductsByPage(PageRequest.of(page, size));
                List<Category> listCategories = categoryService.getAllCategories();
                model.addAttribute("categories", listCategories);
                model.addAttribute("products", productsPage.getContent());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", productsPage.getTotalPages());
                break;

            case "orders":
                List<Order> orders = orderService.getAllOrders();

                // Sort the orders based on status
                Collections.sort(orders, Comparator.comparing(Order::getStatus));
                model.addAttribute("orders", orders);
                break;

            case "categories":
                List<Category> categories = categoryService.getAllCategories();
                model.addAttribute("categories", categories);
                break;
            default:
                break;
        }
    }


    @GetMapping("/add-new-user")
    public String list(Model model) {
        model.addAttribute("user", new Account());
        addLoggedInUserInfoToModel(httpSession, model, cartService);
        return "admin/account/add_new_user";
    }

    @PostMapping("/add-new-user")
    public String addUser(@ModelAttribute("user") @Validated Account account,
                          BindingResult result, Model model,
                          @RequestParam("profilePicture") MultipartFile profilePicture,
                          @RequestParam(value = "admin", required = false) String admin) {
        if (result.hasErrors()) {
            addLoggedInUserInfoToModel(httpSession, model, cartService);
            return "admin/account/add_new_user"; // Redirect to the same page to keep the modal open
        }

        try {

            // Convert the radio button value to boolean
            account.setAdmin(Boolean.parseBoolean(admin));

            Account account1 = accountService.registerUser(account);

            // check if profilePicture
            if (!profilePicture.isEmpty()) {
                accountService.updateProfilePicture(account1, profilePicture);
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "admin/account/add_new_user";

        }
        // Redirect to the user section after adding the user
        return "redirect:/admin/dashboard/users";
    }

    @GetMapping("/delete-user")
    public String deleteUser(@RequestParam("username") String username) {
        accountService.deleteUser(username);
        return "redirect:/admin/dashboard/users";
    }

    @GetMapping("/edit-user/{username}")
    public String showUpdateForm(@PathVariable("username") String username, Model model) {
        Account user = accountService.getUserByUsername(username);
        if (user == null) return "redirect:/admin/dashboard/users";

        model.addAttribute("user", user);
        addLoggedInUserInfoToModel(httpSession, model, cartService);
        return "admin/account/update_user";
    }

    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute("user") @Validated Account account,
                             BindingResult result, Model model,
                             @RequestParam("profilePicture") MultipartFile profilePicture,
                             @RequestParam(value = "admin", required = false) String admin) {
        if (result.hasErrors()) {
            addLoggedInUserInfoToModel(httpSession, model, cartService);
            return "admin/account/update_user"; // Redirect to the same page to keep the modal open
        }

        try {

            // Convert the radio button value to boolean
            account.setAdmin(Boolean.parseBoolean(admin));

            // Call the service to update the user
            accountService.updateProfileInfo(account);

            // check if profilePicture
            if (!profilePicture.isEmpty()) {
                accountService.updateProfilePicture(account, profilePicture);
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "admin/account/update_user";

        }
        // Redirect to the user section after adding the user
        return "redirect:/admin/dashboard/users";
    }

    public void addLoggedInUserInfoToModel(HttpSession httpSession, Model model, CartService cartService) {
        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("sizeCart", cartService.getSizeCart(loggedInUser.getUsername()));
    }

    @PostMapping("/add-new-product")
    public String addProduct(@RequestParam String name,
                             @RequestParam MultipartFile image,
                             @RequestParam double price,
                             @RequestParam(required = false, defaultValue = "false") boolean available,
                             @RequestParam Long categoryId,
                             Model model) {
        try {
            // Save the product image and get its URL
            String imageUrl = productService.saveProductImage(image);

            // Save the product details to the database
            productService.addProduct(name, imageUrl, price, available, categoryId);

            // Redirect to a success page or the product list page
            return "redirect:/admin/dashboard/products";
        } catch (Exception e) {
            // Handle other exceptions
            model.addAttribute("error", "Error adding product: " + e.getMessage());
            return "errorPage"; // Redirect to an error page
        }
    }

    @GetMapping("/delete-product")
    public String deleteProduct(@RequestParam("id") Long productId) {
        productService.deleteProduct(productId);
        return "redirect:/admin/dashboard/products";
    }

    @GetMapping("/update-product/{id}")
    public String showUpdateProduct(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        List<Category> categories = categoryService.getAllCategories(); // Assuming you have a method to get all categories
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categories);
        }
        return "admin/products/update_product";
    }

    @PostMapping("/update-product")
    public String updateProduct(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam(required = false) MultipartFile image,
                                @RequestParam double price,
                                @RequestParam(required = false, defaultValue = "false") boolean available,
                                @RequestParam Long categoryId,
                                Model model) {
        try {
            // Get the product by id
            Product product = productService.getProductById(id);

            // Update the product details
            product.setName(name);
            product.setPrice(price);
            product.setAvailable(available);

            // Save the product image and get its URL, only if a new image is provided
            if (image != null && !image.isEmpty()) {
                String imageUrl = productService.saveProductImage(image);
                product.setImage(imageUrl);
            }

            // Assuming you have a method to fetch Category by ID
            Category category = categoryService.getCategoryById(categoryId);
            product.setCategory(category);

            // Save the updated product to the database
            productService.saveProduct(product);

            // Redirect to the product list page
            return "redirect:/admin/dashboard/products";
        } catch (Exception e) {
            // Handle other exceptions
            model.addAttribute("error", "Error updating product: " + e.getMessage());
            return "errorPage"; // Redirect to an error page
        }
    }

    @PostMapping("/add-new-category")
    public String addCategory(@RequestParam String name, Model model) {
        try {
            Category category = new Category();
            category.setName(name);
            categoryService.saveCategory(category);
            return "redirect:/admin/dashboard/categories";
        } catch (Exception e) {
            model.addAttribute("error", "Error adding category: " + e.getMessage());
            return "errorPage";
        }
    }

    @GetMapping("/delete-category")
    public String deleteCategory(@RequestParam Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            return "redirect:/admin/dashboard/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting category: " + e.getMessage());
        }
        return "redirect:/admin/dashboard/categories";
    }

    @PostMapping("/update-category")
    public String updateCategory(@RequestParam Long id, @RequestParam String name, Model model) {
        try {
            Category category = categoryService.getCategoryById(id);
            if (category != null) {
                category.setName(name);
                categoryService.saveCategory(category);
                return "redirect:/admin/dashboard/categories";
            } else {
                model.addAttribute("error", "Category not found");
                return "errorPage";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error updating category: " + e.getMessage());
            return "errorPage";
        }
    }



    @GetMapping("/export-user/excel")
    public void exportUserToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Account> listUsers = accountService.getAllUsers();

        UserExcelExporter excelExporter = new UserExcelExporter(listUsers);

        excelExporter.export(response);
    }

    @GetMapping("/export-order-confirmed/excel")
    public void exportOrderConfirmedToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Order_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Order> orderList = orderService.ListOrderConfirmed("Confirmed");

        OrderExcelExporter excelExporter = new OrderExcelExporter(orderList);

        excelExporter.export(response);
    }

    @GetMapping("/user-chart")
    public String showChart(Model model) {
        List<Account> accounts = accountService.getAllUsers(); // Assuming you have a repository to fetch accounts

        long adminCount = accounts.stream().filter(Account::getAdmin).count();
        long userCount = accounts.stream().filter(account -> !account.getAdmin()).count();

        long activatedCount = accounts.stream().filter(Account::getActivated).count();
        long nonActivatedCount = accounts.size() - activatedCount;

        model.addAttribute("adminCount", adminCount);
        model.addAttribute("userCount", userCount);

        model.addAttribute("activatedCount", activatedCount);
        model.addAttribute("nonActivatedCount", nonActivatedCount);


        return "admin/account/chart";

    }
}
