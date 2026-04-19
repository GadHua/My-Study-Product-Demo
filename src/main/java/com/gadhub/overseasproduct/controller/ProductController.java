package com.gadhub.overseasproduct.controller;

import com.gadhub.overseasproduct.dto.ProductDto;
import com.gadhub.overseasproduct.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/backend/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public String addProduct(ProductDto productDto) {
        productService.addProduct(productDto);
        return "redirect:/api/v1/backend/product/list";
    }

}
