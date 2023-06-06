package cart.ui;

import cart.application.MemberService;
import cart.application.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    private final ProductService productService;
    private final MemberService memberService;

    public PageController(final ProductService productService, final MemberService memberService) {
        this.productService = productService;
        this.memberService = memberService;
    }

    @GetMapping("/admin")
    public String admin(final Model model) {
        model.addAttribute("products", productService.findAllProducts());
        return "admin";
    }

    @GetMapping("/settings")
    public String members(final Model model) {
        model.addAttribute("members", memberService.findAllMembers());
        return "settings";
    }
}
