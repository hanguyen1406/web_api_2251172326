import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/cart_provider.dart';
import '../../providers/order_provider.dart'; 
import '../../providers/auth_provider.dart'; // import auth provider to check address
import '../../screens/order/order_history_screen.dart'; // Navigate after checkout

class CartScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final cart = Provider.of<CartProvider>(context);
    return Scaffold(
      appBar: AppBar(title: Text('Your Cart')),
      body: Column(
        children: [
          Card(
            margin: EdgeInsets.all(15),
            child: Padding(
              padding: EdgeInsets.all(8),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text('Total', style: TextStyle(fontSize: 20)),
                  Spacer(),
                  Chip(
                    label: Text(
                      '\$${cart.totalAmount.toStringAsFixed(2)}',
                      style: TextStyle(color: Colors.white),
                    ),
                    backgroundColor: Theme.of(context).primaryColor,
                  ),
                  SizedBox(width: 10), // spacing
                  OrderButton(cart: cart),
                ],
              ),
            ),
          ),
          SizedBox(height: 10),
          Expanded(
            child: ListView.builder(
              itemCount: cart.items.length,
              itemBuilder: (ctx, i) => CartItemWidget(
                cart.items.values.toList()[i],
                cart.items.keys.toList()[i],
              ),
            ),
          )
        ],
      ),
    );
  }
}

class OrderButton extends StatefulWidget {
  final CartProvider cart;
  const OrderButton({required this.cart});

  @override
  _OrderButtonState createState() => _OrderButtonState();
}

class _OrderButtonState extends State<OrderButton> {
  var _isLoading = false;

  @override
  Widget build(BuildContext context) {
    return TextButton(
      child: _isLoading ? CircularProgressIndicator() : Text('ORDER NOW'),
      onPressed: (widget.cart.totalAmount <= 0 || _isLoading)
          ? null
          : () async {
              setState(() {
                _isLoading = true;
              });
              try {
                // Get user info for default address (could be improved with a checkout form)
                final user = Provider.of<AuthProvider>(context, listen: false).user;
                
                await Provider.of<OrderProvider>(context, listen: false).addOrder(
                  widget.cart.items.values.toList(),
                  widget.cart.totalAmount,
                  user?.address ?? 'Default Address',
                );
                
                widget.cart.clear();
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Order placed successfully!')),
                );
                Navigator.of(context).pushReplacement(
                   MaterialPageRoute(builder: (context) => OrderHistoryScreen()),
                );
              } catch (e) {
                 ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Order failed: $e')),
                );
              } finally {
                setState(() {
                  _isLoading = false;
                });
              }
            },
    );
  }
}


class CartItemWidget extends StatelessWidget {
  final CartItem item;
  final String productId;

  CartItemWidget(this.item, this.productId);

  @override
  Widget build(BuildContext context) {
    return Dismissible(
      key: ValueKey(item.id),
      background: Container(
        color: Theme.of(context).colorScheme.error,
        child: Icon(Icons.delete, color: Colors.white, size: 40),
        alignment: Alignment.centerRight,
        padding: EdgeInsets.only(right: 20),
        margin: EdgeInsets.symmetric(horizontal: 15, vertical: 4),
      ),
      direction: DismissDirection.endToStart,
      confirmDismiss: (direction) {
        return showDialog(
          context: context,
          builder: (ctx) => AlertDialog(
            title: Text('Are you sure?'),
            content: Text('Do you want to remove the item from the cart?'),
            actions: [
              TextButton(child: Text('No'), onPressed: () => Navigator.of(ctx).pop(false)),
              TextButton(child: Text('Yes'), onPressed: () => Navigator.of(ctx).pop(true)),
            ],
          ),
        );
      },
      onDismissed: (direction) {
        Provider.of<CartProvider>(context, listen: false).removeItem(productId);
      },
      child: Card(
        margin: EdgeInsets.symmetric(horizontal: 15, vertical: 4),
        child: Padding(
          padding: EdgeInsets.all(8),
          child: ListTile(
            leading: CircleAvatar(
               backgroundImage: NetworkImage(item.product.imageUrl),
            ),
            title: Text(item.title),
            subtitle: Text('Total: \$${(item.price * item.quantity).toStringAsFixed(2)}'),
            trailing: Text('${item.quantity} x'),
          ),
        ),
      ),
    );
  }
}
