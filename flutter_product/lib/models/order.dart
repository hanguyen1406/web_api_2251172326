import '../models/product.dart';

class OrderItem {
  final int id;
  final Product product;
  final int quantity;
  final double price;

  OrderItem({required this.id, required this.product, required this.quantity, required this.price});

  factory OrderItem.fromJson(Map<String, dynamic> json) {
    return OrderItem(
      id: json['id'],
      product: Product.fromJson(json['product']),
      quantity: json['quantity'],
      price: (json['price'] as num).toDouble(),
    );
  }
}

class Order {
  final int id;
  final String orderNumber;
  final double total;
  final String status;
  final String orderDate;
  final List<OrderItem> items;
  final String shippingAddress;
  final String paymentMethod;
  final String paymentStatus;

  Order({
    required this.id,
    required this.orderNumber,
    required this.total,
    required this.status,
    required this.orderDate,
    required this.items,
    required this.shippingAddress,
    required this.paymentMethod,
    required this.paymentStatus,
  });

  factory Order.fromJson(Map<String, dynamic> json) {
    return Order(
      id: json['id'],
      orderNumber: json['orderNumber'],
      total: (json['total'] as num).toDouble(),
      status: json['status'],
      orderDate: json['orderDate'], // You might want to parse Date
      items: (json['items'] as List).map((i) => OrderItem.fromJson(i)).toList(),
      shippingAddress: json['shippingAddress'] ?? '',
      paymentMethod: json['paymentMethod'] ?? '',
      paymentStatus: json['paymentStatus'] ?? '',
    );
  }
}
