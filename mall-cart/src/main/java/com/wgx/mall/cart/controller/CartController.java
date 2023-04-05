package com.wgx.mall.cart.controller;

import com.wgx.common.constants.AuthConstant;
import com.wgx.common.to.MemberTo;
import com.wgx.common.to.UserInfoTo;
import com.wgx.common.utils.R;
import com.wgx.mall.cart.interceptor.CartInterceptor;
import com.wgx.mall.cart.service.CartService;
import com.wgx.mall.cart.vo.CartItem;
import com.wgx.mall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author wgx
 * @since 2023/4/1 15:54
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> currentUserCartItems() {
        return cartService.currentUserCartItems();
    }


    @GetMapping({"/", "/cartList.html"})
    public String cartList(Model model) {
        CartVo cartVo = cartService.getCart();
        model.addAttribute("cart", cartVo);
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(Long skuId, Integer num) {
        cartService.addToCart(skuId, num);
        return "redirect:http://cart.mall.com/addToCartSuccess.html?skuId=" + skuId;
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId, Model model) {
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }

    @GetMapping("/deleteCartItem/{skuId}")
    public String deleteCartItem(@PathVariable("skuId") Long skuId) {
        cartService.deleteCartItem(skuId);
        return "redirect:http://cart.mall.com";
    }

}
