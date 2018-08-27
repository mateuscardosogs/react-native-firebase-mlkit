package = JSON.parse(File.read(File.join(__dir__, '../package.json')))

Pod::Spec.new do |s|
  s.name           = "RNMlKit"
  s.version        = package['version']
  s.summary        = package['summary']
  s.description    = package['description']
  s.license        = package['license']
  s.author         = package['author']
  s.homepage       = package['homepage']
  s.platform       = :ios, "7.0"
  s.source         = { :git => "https://github.com/mateusc42/react-native-firebase-mlkit.git", :tag => "master" }
  s.source_files   = "*.{h,m}"
  s.requires_arc   = true


  s.dependency     "React"
  s.dependency     "Firebase"
end