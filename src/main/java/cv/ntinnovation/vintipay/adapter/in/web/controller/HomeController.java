package cv.ntinnovation.vintipay.adapter.in.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para a página inicial
 */
@Controller
public class HomeController {

    /**
     * Exibe a página inicial (landing page)
     * @return Template da landing page
     */
    @GetMapping("/")
    public String home() {
        return "home/index";
    }
}
