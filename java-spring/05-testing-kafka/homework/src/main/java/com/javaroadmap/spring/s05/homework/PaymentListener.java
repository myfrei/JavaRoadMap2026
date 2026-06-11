package com.javaroadmap.spring.s05.homework;

/**
 * Задание 3. Сделай класс бином-консьюмером:
 * - подпишись на топик "payments" (groupId "hw-payments");
 * - в PaymentStore сохраняй ТОЛЬКО события со status "OK";
 * - обработка должна быть идемпотентной (store.accept уже такой).
 */
public class PaymentListener {

    private final PaymentStore store;

    public PaymentListener(PaymentStore store) {
        this.store = store;
    }

    public void onPayment(PaymentEvent event) {
        throw new UnsupportedOperationException("TODO: задание 3");
    }
}
