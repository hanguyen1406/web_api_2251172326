import 'package:flutter/material.dart';
import '../models/product.dart';
import '../services/api_service.dart';

class ProductProvider with ChangeNotifier {
  final ApiService _apiService = ApiService();
  List<Product> _products = [];
  bool _isLoading = false;

  List<Product> get products => _products;
  bool get isLoading => _isLoading;

  Future<void> fetchProducts({String? search, String? category}) async {
    _setLoading(true);
    try {
      String endpoint = '/products?page=0&size=100'; // Fetch enough for demo
      if (search != null && search.isNotEmpty) {
        endpoint += '&search=$search';
      }
      if (category != null && category.isNotEmpty) {
        endpoint += '&category=$category';
      }

      final response = await _apiService.get(endpoint);
      if (response != null && response['content'] != null) {
        final List<dynamic> content = response['content'];
        _products = content.map((item) => Product.fromJson(item)).toList();
      } else {
        _products = [];
      }
      notifyListeners();
    } catch (e) {
      print('Fetch products error: $e');
      rethrow;
    } finally {
      _setLoading(false);
    }
  }

  Future<Product?> getProductById(int id) async {
    try {
      final response = await _apiService.get('/products/$id');
      if (response != null) {
        return Product.fromJson(response);
      }
    } catch (e) {
      print('Get product error: $e');
    }
    return null;
  }

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }
}
