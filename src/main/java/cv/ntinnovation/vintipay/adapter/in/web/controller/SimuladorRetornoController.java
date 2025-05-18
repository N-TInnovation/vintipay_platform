package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.SispCheckoutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/simulador/")
@RequiredArgsConstructor
public class SimuladorRetornoController {
/* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */
    /**
     * This endpoint is a simulation of the SISP 3DS Middleware callback.
     * It will be called by the SISP 3DS Middleware when a FingerPrint is created.
     * The FingerPrint, TimeStamp and FingerPrintVersion are sent as parameters.
     * @param fingerPrint The FingerPrint created by the 3DS Middleware
     * @param timeStamp The TimeStamp when the FingerPrint was created
     * @param fingerPrintVersion The version of the FingerPrint
     * @return The html page of the SISP 3DS Middleware
     */
/* <<<<<<<<<<  47910709-7c0c-412b-8c17-b0b9d7844fa6  >>>>>>>>>>> */
    @PostMapping(value = "sisp/3ds_middleware_php/public/3ds_init.php", consumes = "application/x-www-form-urlencoded")
    public String index(
            @RequestParam(value = "FingerPrint") String fingerPrint,
            @RequestParam(value = "TimeStamp") String timeStamp,
            @RequestParam(value = "FingerPrintVersion") String fingerPrintVersion,
            @ModelAttribute SispCheckoutRequest sispCheckoutRequest,
            Model model
            ) {
        if(!sispCheckoutRequest.getFingerprint().equals(fingerPrint)) {
            throw new RuntimeException("FingerPrint does not match");
        }
        if(!sispCheckoutRequest.getTimeStamp().equals(timeStamp)) {
            throw new RuntimeException("TimeStamp does not match");
        }
        if (!sispCheckoutRequest.getFingerprintversion().equals(fingerPrintVersion)) {
            throw new RuntimeException("FingerPrintVersion does not match");
        }

        model.addAttribute("request", sispCheckoutRequest);

        return "simulador/sisp/3ds_middleware_php";
    }

}
