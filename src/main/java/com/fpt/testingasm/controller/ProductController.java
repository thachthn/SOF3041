package com.fpt.testingasm.controller;

import com.fpt.testingasm.model.Product;
import com.fpt.testingasm.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    private boolean notLoggedIn(HttpSession session) {
        return session.getAttribute("currentUser") == null;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        if (notLoggedIn(session)) {
            return "redirect:/login";
        }
        model.addAttribute("products", productService.findAll());
        model.addAttribute("currentUser", session.getAttribute("currentUser"));
        return "products/list";
    }

    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        if (notLoggedIn(session)) {
            return "redirect:/login";
        }
        model.addAttribute("product", new Product());
        return "products/form";
    }

    @PostMapping
    public String create(HttpSession session,
                          @RequestParam String name,
                          @RequestParam(required = false) String description,
                          @RequestParam Double price,
                          @RequestParam Integer quantity) {
        if (notLoggedIn(session)) {
            return "redirect:/login";
        }
        productService.create(new Product(name, description, price, quantity));
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(HttpSession session, @PathVariable Long id, Model model) {
        if (notLoggedIn(session)) {
            return "redirect:/login";
        }
        Product product = productService.findById(id)
                .orElseThrow(() -> new ProductService.ProductNotFoundException(id));
        model.addAttribute("product", product);
        return "products/form";
    }

    @PostMapping("/{id}")
    public String update(HttpSession session,
                          @PathVariable Long id,
                          @RequestParam String name,
                          @RequestParam(required = false) String description,
                          @RequestParam Double price,
                          @RequestParam Integer quantity) {
        if (notLoggedIn(session)) {
            return "redirect:/login";
        }
        productService.update(id, new Product(name, description, price, quantity));
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(HttpSession session, @PathVariable Long id) {
        if (notLoggedIn(session)) {
            return "redirect:/login";
        }
        productService.delete(id);
        return "redirect:/products";
    }
}
