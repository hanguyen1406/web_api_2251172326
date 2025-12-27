import 'package:flutter/material.dart';
import '../models/product.dart';

class CartItem {
  final String id;
  final String title;
  final int quantity;
  final double price;
  final Product product; // Reference for image/etc

  CartItem({
    required this.id,
    required this.title,
    required this.quantity,
    required this.price,
    required this.product,
  });
}

class CartProvider with ChangeNotifier {
  Map<String, CartItem> _items = {};

  Map<String, CartItem> get items {
    return {..._items};
  }

  int get itemCount {
    return _items.length;
  }

  double get totalAmount {
    var total = 0.0;
    _items.forEach((key, cartItem) {
      total += cartItem.price * cartItem.quantity;
    });
    return total;
  }

  void addItem(Product product) {
    if (_items.containsKey(product.id.toString())) {
      // update quantity
      _items.update(
        product.id.toString(),
        (existingCartItem) => CartItem(
          id: existingCartItem.id,
          title: existingCartItem.title,
          price: existingCartItem.price,
          quantity: existingCartItem.quantity + 1,
          product: existingCartItem.product,
        ),
      );
    } else {
      // add new
      _items.putIfAbsent(
        product.id.toString(),
        () => CartItem(
          id: DateTime.now().toString(),
          title: product.name,
          price: product.price,
          quantity: 1,
          product: product,
        ),
      );
    }
    notifyListeners();
  }

  void removeItem(String productId) {
    _items.remove(productId);
    notifyListeners();
  }
  
  void removeSingleItem(String productId) {
    if (!_items.containsKey(productId)) {
      return;
    }
    if (_items[productId]!.quantity > 1) {
      _items.update(
          productId,
          (existingCartItem) => CartItem(
                id: existingCartItem.id,
                title: existingCartItem.title,
                price: existingCartItem.price,
                quantity: existingCartItem.quantity - 1,
                product: existingCartItem.product,
              ));
    } else {
      _items.remove(productId);
    }
    notifyListeners();
  }

  void clear() {
    _items = {};
    notifyListeners();
  }
}
