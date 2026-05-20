package br.com.infnet.payment.controller;

import br.com.infnet.payment.domain.enums.PaymentStatus;
import br.com.infnet.payment.dto.PaymentRequest;
import br.com.infnet.payment.dto.PaymentResponse;
import br.com.infnet.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(@Valid @RequestBody PaymentRequest request) {
        return paymentService.create(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse getById(@PathVariable UUID id) {
        return paymentService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentResponse> getAll() {
        return paymentService.getAll();
    }

    //PATCH /payments/{id}/status?status=NOVO_STATUS
    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse updateStatus(
            @PathVariable UUID id,
            @RequestParam PaymentStatus status) {
        return paymentService.updateStatus(id, status);
    }
}