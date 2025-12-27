import 'package:flutter/material.dart';
import '../../models/order.dart';

class OrderDetailScreen extends StatelessWidget {
  final Order order;

  const OrderDetailScreen({Key? key, required this.order}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Order Details')),
      body: SingleChildScrollView(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Order #${order.orderNumber}', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
            SizedBox(height: 10),
            Text('Date: ${order.orderDate}'),
            Text('Status: ${order.status}'),
            Text('Total: \$${order.total.toStringAsFixed(2)}'),
            SizedBox(height: 20),
            Text('Shipping Address:', style: TextStyle(fontWeight: FontWeight.bold)),
            Text(order.shippingAddress),
            SizedBox(height: 20),
            Text('Items:', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            Divider(),
            ...order.items.map((item) => Padding(
              padding: const EdgeInsets.symmetric(vertical: 8.0),
              child: Row(
                children: [
                  Container(
                    width: 50, height: 50,
                    color: Colors.grey[200],
                    child: Image.network(item.product.imageUrl, fit: BoxFit.cover, errorBuilder: (_,__,___) => Icon(Icons.image)),
                  ),
                  SizedBox(width: 10),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(item.product.name, style: TextStyle(fontWeight: FontWeight.bold)),
                        Text('${item.quantity} x \$${item.price}'),
                      ],
                    ),
                  ),
                  Text('\$${(item.quantity * item.price).toStringAsFixed(2)}'),
                ],
              ),
            )).toList(),
          ],
        ),
      ),
    );
  }
}
