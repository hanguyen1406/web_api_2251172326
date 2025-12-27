import 'package:flutter/material.dart';
import '../../models/product.dart';
import 'package:provider/provider.dart';
import '../../providers/cart_provider.dart';

class ProductDetailScreen extends StatelessWidget {
  final Product product;

  const ProductDetailScreen({Key? key, required this.product}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(product.name)),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              height: 300,
              width: double.infinity,
              child: Image.network(
                product.imageUrl,
                fit: BoxFit.cover,
                errorBuilder: (ctx, _, __) => Container(color: Colors.grey),
              ),
            ),
            SizedBox(height: 10),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        '\$${product.price.toStringAsFixed(0)}',
                        style: TextStyle(color: Colors.green, fontSize: 20, fontWeight: FontWeight.bold),
                      ),
                      Row(
                        children: [
                          Icon(Icons.star, color: Colors.amber),
                          Text('${product.rating} (${product.reviewCount})'),
                        ],
                      ),
                    ],
                  ),
                  SizedBox(height: 10),
                  Text(
                    product.description,
                    style: TextStyle(fontSize: 16),
                  ),
                  SizedBox(height: 20),
                  Text('Brand: ${product.brand}'),
                  Text('Category: ${product.category}'),
                  Text('Stock: ${product.stock}'),
                  SizedBox(height: 30),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      icon: Icon(Icons.add_shopping_cart),
                      label: Text('Add to Cart'),
                      onPressed: product.stock > 0 ? () {
                         Provider.of<CartProvider>(context, listen: false).addItem(product);
                         ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text('Added to cart!')),
                        );
                      } : null, // Disable if out of stock
                      style: ElevatedButton.styleFrom(
                        padding: EdgeInsets.symmetric(vertical: 15),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
