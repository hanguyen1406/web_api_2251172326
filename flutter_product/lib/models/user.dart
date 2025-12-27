class User {
  final int id;
  final String email;
  final String fullName;
  final List<String> roles;
  final String studentId;

  User({
    required this.id,
    required this.email,
    required this.fullName,
    required this.roles,
    required this.studentId,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      email: json['email'],
      fullName: json['fullName'] ?? '',
      roles: List<String>.from(json['roles'] ?? []),
      studentId: json['student_id'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'fullName': fullName,
      'roles': roles,
      'student_id': studentId,
    };
  }
}
