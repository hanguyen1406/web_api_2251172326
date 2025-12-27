import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/product_provider.dart';
import '../../widgets/product_item.dart';
import '../../widgets/app_drawer.dart';
import '../cart/cart_screen.dart';

class ProductListScreen extends StatefulWidget {
  @override
  _ProductListScreenState createState() => _ProductListScreenState();
}

class _ProductListScreenState extends State<ProductListScreen> {
  final _searchController = TextEditingController();
  
  @override
  void initState() {
    super.initState();
    // Fetch products initially
    Future.microtask(() => 
      Provider.of<ProductProvider>(context, listen: false).fetchProducts()
    );
  }

  void _search() {
    Provider.of<ProductProvider>(context, listen: false).fetchProducts(
      search: _searchController.text
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Travel App Store'),
        actions: [
          IconButton(
            icon: Icon(Icons.shopping_cart),
            onPressed: () {
              Navigator.of(context).push(
                MaterialPageRoute(builder: (ctx) => CartScreen()),
              );
            },
          ),
        ],
      ),
      drawer: AppDrawer(),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                labelText: 'Search Products',
                suffixIcon: IconButton(
                  icon: Icon(Icons.search),
                  onPressed: _search,
                ),
                border: OutlineInputBorder(),
              ),
              onSubmitted: (_) => _search(),
            ),
          ),
          Expanded(
            child: Consumer<ProductProvider>(
              builder: (ctx, productData, _) {
                if (productData.isLoading) {
                  return Center(child: CircularProgressIndicator());
                }
                if (productData.products.isEmpty) {
                  return Center(child: Text('No products found!'));
                }
                return GridView.builder(
                  padding: const EdgeInsets.all(10),
                  itemCount: productData.products.length,
                  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    childAspectRatio: 3 / 4,
                    crossAxisSpacing: 10,
                    mainAxisSpacing: 10,
                  ),
                  itemBuilder: (ctx, i) => ProductItem(product: productData.products[i]),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
