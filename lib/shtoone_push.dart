
import 'shtoone_push_platform_interface.dart';

class ShtoonePush {
  Future<String?> getPlatformVersion() {
    return ShtoonePushPlatform.instance.getPlatformVersion();
  }
}
