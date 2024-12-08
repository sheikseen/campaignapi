/*package com.shiel.campaignapi.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;

public class PaymentService {

    private RazorpayClient client;

    public PaymentService() throws Exception {
        this.client = new RazorpayClient("YOUR_API_KEY", "YOUR_SECRET_KEY");
    }

    public String createOrder(int amount, String currency) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Amount in paise (e.g., ₹500 -> 50000 paise)
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "txn_123456");
        orderRequest.put("payment_capture", true);

        Order order = client.orders.create(orderRequest);
        return order.toString();
    }
}

*/