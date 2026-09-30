# V2 Eleven

V2 Eleven is an open-source Android VPN client built around Xray-core, with dedicated interfaces for phones and Android TV.

## Highlights

- VLESS, VMess, Shadowsocks, Trojan and Hysteria2 profiles
- Subscription URL support with automatic updates
- QR code import and local phone-to-TV configuration transfer
- Android TV / Leanback interface with remote-friendly navigation
- Routing presets and per-app split tunneling
- Connection testing and IP checking
- Local configuration server
- Room-based local profile storage
- Automatic geo database updates

## Requirements

- Android 7.0+ (API 24)
- Android Studio with a recent Android Gradle Plugin
- Xray core is bundled through `libv2ray.aar`

## Build

    git clone https://github.com/ArmanEleven/V2Eleven.git
    cd V2Eleven
    ./gradlew assembleDebug

The debug APK is generated at:

`app/build/outputs/apk/debug/app-debug.apk`

## Project Identity

- **Application:** V2 ELEVEN
- **Application ID:** `ir.armaneleven.v2eleven`
- **Repository:** https://github.com/ArmanEleven/V2Eleven

## Architecture

The Android application is organized into core VPN services, configuration import/parsing, local data storage, routing, and separate mobile/TV UI layers.

The application package is:

`ir.armaneleven.v2eleven`

## Credits & Licensing

V2 Eleven contains open-source components and third-party software. Original copyright notices, licenses and required attribution are preserved in the repository.

Important upstream components include Xray-core, AndroidLibXrayLite, v2rayNG, v2fly geo databases and the HEV SOCKS5 tunnel.

See [LICENSE](LICENSE) for the project license and the individual third-party directories for their respective licenses.

## Disclaimer

This project is provided for educational and research purposes. Users are responsible for complying with the laws and regulations applicable to their use of the software.
