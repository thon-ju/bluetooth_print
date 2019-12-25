#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
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
  s.author           = { 'thon-ju' => 'thon.ju@gmail.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.static_framework = true
  s.dependency 'Flutter'

  # 引入Classes文件夹下所有的*.a库
  s.frameworks = ["SystemConfiguration", "CoreTelephony","WebKit"]
  s.vendored_libraries = '**/*.a'

  #s.ios.deployment_target = '8.0'
end

