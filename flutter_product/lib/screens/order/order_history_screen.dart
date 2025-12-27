import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../providers/order_provider.dart';
import '../../providers/auth_provider.dart';
import 'order_detail_screen.dart';

class OrderHistoryScreen extends StatefulWidget {
  @override
  _OrderHistoryScreenState createState() => _OrderHistoryScreenState();
}

class _OrderHistoryScreenState extends State<OrderHistoryScreen> {
  @override
  void initState() {
    super.initState();
    final user = Provider.of<AuthProvider>(context, listen: false).user;
    if (user != null) {
      Provider.of<OrderProvider>(context, listen: false).fetchOrders(user.id);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Your Orders'), leading: IconButton(icon: Icon(Icons.arrow_back), onPressed: () => Navigator.of(context).pushReplacementNamed('/home'),),),
      body: Consumer<OrderProvider>(
        builder: (ctx, orderData, _) {
          if (orderData.isLoading) {
            return Center(child: CircularProgressIndicator());
          }
          if (orderData.orders.isEmpty) {
            return Center(child: Text('No orders yet!'));
          }
          return ListView.builder(
            itemCount: orderData.orders.length,
            itemBuilder: (ctx, i) {
              final order = orderData.orders[i];
              return Card(
                margin: EdgeInsets.all(10),
                child: ListTile(
                  title: Text('Order #${order.orderNumber}'),
                  subtitle: Text(DateFormat('dd/MM/yyyy HH:mm').format(DateTime.parse(order.orderDate))),
                  trailing: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                       Text('\$${order.total.toStringAsFixed(2)}', style: TextStyle(fontWeight: FontWeight.bold)),
                       Text(order.status, style: TextStyle(color: _getStatusColor(order.status))),
                    ],
                  ),
                  onTap: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(
                        builder: (ctx) => OrderDetailScreen(order: order),
                      ),
                    );
                  },
                ),
              );
            },
          );
        },
      ),
    );
  }

  Color _getStatusColor(String status) {
    switch (status.toLowerCase()) {
      case 'pending': return Colors.orange;
      case 'confirmed': return Colors.blue;
      case 'shipped': return Colors.indigo;
      case 'delivered': return Colors.green;
      case 'cancelled': return Colors.red;
      default: return Colors.grey;
    }
  }
}
