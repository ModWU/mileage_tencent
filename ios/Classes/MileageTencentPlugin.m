#import "MileageTencentPlugin.h"
#if __has_include(<mileage_tencent/mileage_tencent-Swift.h>)
#import <mileage_tencent/mileage_tencent-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "mileage_tencent-Swift.h"
#endif

@implementation MileageTencentPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMileageTencentPlugin registerWithRegistrar:registrar];
}
@end
