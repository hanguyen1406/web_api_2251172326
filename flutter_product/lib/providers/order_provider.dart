import 'package:flutter/material.dart';
import '../models/order.dart';
import '../models/product.dart';
import '../providers/cart_provider.dart';
import '../services/api_service.dart';

class OrderProvider with ChangeNotifier {
  final ApiService _apiService = ApiService();
  List<Order> _orders = [];
  bool _isLoading = false;

  List<Order> get orders => _orders;
  bool get isLoading => _isLoading;

  Future<void> fetchOrders(int userId) async {
    _setLoading(true);
    try {
      final response = await _apiService.get('/customers/$userId/orders?page=0&size=20');
      // The API /customers/{id}/orders returns Page<Order> or List<Order>?
      // Let's assume the API structure from the backend code: /customers/{id}/orders by CustomerController
      // Actually checking CustomerController code:
      // @GetMapping("/{id}/orders") ... return orderService.getOrdersByCustomer(...) which returns Page<Order>
      
      if (response != null && response['content'] != null) {
          final List<dynamic> content = response['content'];
          _orders = content.map((item) => Order.fromJson(item)).toList();
          // Sort by date desc
          // _orders.sort((a, b) => b.orderDate.compareTo(a.orderDate));
      } else {
        _orders = [];
      }
      notifyListeners();
    } catch (e) {
      print('Fetch orders error: $e');
      rethrow;
    } finally {
      _setLoading(false);
    }
  }

  Future<void> addOrder(List<CartItem> cartItems, double total, String address) async {
    try {
      final List<Map<String, dynamic>> items = cartItems.map((cp) => {
        'product': {'id': cp.product.id},
        'quantity': cp.quantity,
      }).toList();

      await _apiService.post('/orders', {
        'items': items,
        'paymentMethod': 'cod', // Default for now
        'shippingAddress': address,
        'notes': 'Placed from Flutter App',
      });
      
      // Refresh orders
      // await fetchOrders(); 
    } catch (e) {
      print('Add order error: $e');
      rethrow;
    }
  }

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }
}
