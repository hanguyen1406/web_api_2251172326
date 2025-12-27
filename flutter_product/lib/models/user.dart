class User {
  final int id;
  final String email;
  final String fullName;
  final List<String> roles;
  final String studentId;
  final String address;
  final String phoneNumber;
  final String city;
  final String postalCode;

  User({
    required this.id,
    required this.email,
    required this.fullName,
    required this.roles,
    required this.studentId,
    required this.address,
    required this.phoneNumber,
    required this.city,
    required this.postalCode,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      email: json['email'],
      fullName: json['fullName'] ?? '',
      roles: List<String>.from(json['roles'] ?? []),
      studentId: json['student_id'] ?? '',
      address: json['address'] ?? '',
      phoneNumber: json['phoneNumber'] ?? '',
      city: json['city'] ?? '',
      postalCode: json['postalCode'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'fullName': fullName,
      'roles': roles,
      'student_id': studentId,
      'address': address,
      'phoneNumber': phoneNumber,
      'city': city,
      'postalCode': postalCode,
    };
  }
}
