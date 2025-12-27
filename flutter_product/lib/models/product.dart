class Product {
  final int id;
  final String name;
  final String description;
  final double price;
  final String category;
  final String brand;
  final int stock;
  final String imageUrl;
  final double rating;
  final int reviewCount;
  final bool isAvailable;

  Product({
    required this.id,
    required this.name,
    required this.description,
    required this.price,
    required this.category,
    required this.brand,
    required this.stock,
    required this.imageUrl,
    required this.rating,
    required this.reviewCount,
    required this.isAvailable,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id: json['id'],
      name: json['name'],
      description: json['description'] ?? '',
      price: (json['price'] as num).toDouble(),
      category: json['category'] ?? '',
      brand: json['brand'] ?? '',
      stock: json['stock'] ?? 0,
      imageUrl: json['imageUrl'] ?? 'https://via.placeholder.com/150',
      rating: (json['rating'] as num?)?.toDouble() ?? 0.0,
      reviewCount: json['reviewCount'] ?? 0,
      isAvailable: json['available'] ?? false,
    );
  }
}
