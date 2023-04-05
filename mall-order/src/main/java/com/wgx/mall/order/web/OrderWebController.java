package com.wgx.mall.order.web;

import com.wgx.mall.order.service.OrderService;
import com.wgx.mall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author wgx
 * @since 2023/4/5 14:52
 */
@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("order", orderConfirmVo);
        return "confirm";
    }
}
