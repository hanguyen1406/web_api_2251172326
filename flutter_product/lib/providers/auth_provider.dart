import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user.dart';
import '../services/api_service.dart';

class AuthProvider with ChangeNotifier {
  final ApiService _apiService = ApiService();
  User? _user;
  bool _isLoading = false;

  User? get user => _user;
  bool get isLoading => _isLoading;
  bool get isAuthenticated => _user != null;

  Future<bool> login(String email, String password) async {
    _setLoading(true);
    try {
      final response = await _apiService.post('/auth/login', {
        'email': email,
        'password': password,
      });

      if (response != null && response['token'] != null) {
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString('token', response['token']);
        
        // Save user info locally or fetch profile
        _user = User.fromJson(response);
        await prefs.setString('user_data', jsonEncode(response)); // Save simple user data including ID
        
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      print('Login error: $e');
      rethrow;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> register(String email, String password, String fullName, String phone, String address, String city, String postalCode) async {
    _setLoading(true);
    try {
      await _apiService.post('/auth/register', {
        'email': email,
        'password': password,
        'fullName': fullName,
        'phoneNumber': phone,
        'address': address,
        'city': city,
        'postalCode': postalCode,
      });
      return true; // Success implies 200/201
    } catch (e) {
      print('Register error: $e');
      rethrow;
    } finally {
      _setLoading(false);
    }
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('token');
    await prefs.remove('user_data');
    _user = null;
    notifyListeners();
  }

  Future<void> tryAutoLogin() async {
    final prefs = await SharedPreferences.getInstance();
    if (!prefs.containsKey('token')) return;

    final userData = prefs.getString('user_data');
    if (userData != null) {
      _user = User.fromJson(jsonDecode(userData));
      notifyListeners();
    }
    
    // Optionally verify token with /auth/me
  }

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }
}
