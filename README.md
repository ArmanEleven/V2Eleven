# v2Eleven

A custom Android client based on the open-source [v2rayNG](https://github.com/2dust/v2rayNG) project.

## Product direction

- Preserve the existing networking engine and protocol support.
- Provide three focused destinations: Subscription, Turkey Gaming, and Web Browsing.
- Import one subscription URL and categorize servers by the Turkey flag or country name.
- Include a clear “I don't have a subscription link” path to customer onboarding.
- Show subscription usage and expiry information when supplied by the subscription provider.
- Provide renewal and support links:
  - Renewal: https://t.me/ElevenPing
  - Support: https://t.me/ElevenJT
- Design direction: dark, cinematic, Stranger Things-inspired atmosphere with restrained red lighting and carefully applied Liquid Glass styling.

## Development status

This repository was newly created and is being initialized. The upstream source and submodule requirements must be brought in before implementation and build validation. Development changes will be kept on a dedicated feature branch; the default branch is reserved as the stable baseline.

## Upstream and licensing

The upstream project is maintained at [2dust/v2rayNG](https://github.com/2dust/v2rayNG). Its source is distributed under GPL-3.0. Preserve upstream copyright notices, license files, and applicable source-distribution obligations when modifying or redistributing the application.

The final application name and package identifier are planned as `v2Eleven` and `ir.armaneleven.v2eleven`.
