#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint bluetooth_print.podspec' to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'bluetooth_print'
  s.version          = '0.0.1'
  s.summary          = 'A new flutter plugin for bluetooth printer.'
  s.description      = <<-DESC
A new flutter plugin project.
                       DESC
  s.homepage         = 'https://github.com/thon-ju/bluetooth_print'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'thon.ju@gmail.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.static_framework = true
  s.dependency 'Flutter'
  s.platform = :ios, '8.0'

  # 引入Classes文件夹下所有的*.a库
  s.frameworks = ["SystemConfiguration", "CoreTelephony","WebKit"]
  s.vendored_libraries = '**/*.a'

  # Flutter.framework does not contain a i386 slice. Only x86_64 simulators are supported.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'VALID_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
end
