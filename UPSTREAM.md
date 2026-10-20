# Upstream source snapshot

This branch imports the Android client source from https://github.com/2dust/v2rayNG.

- Upstream commit: 118cf5bc7d0b290947b0827f5ad03e73018d8e5d
- Upstream tree: 0a7fd924087149e876158bb21cc961c4608a9364
- The upstream LICENSE file and source notices are retained.
- The upstream README was replaced by this repository's v2Eleven README.
- Upstream GitHub Actions workflows were intentionally not copied because the release workflow expects signing secrets and write permissions. A reviewed v2Eleven CI workflow should be added separately.
- The two upstream submodules are retained through .gitmodules.

This repository is not a GitHub fork relationship. To synchronize future upstream changes, compare and merge upstream changes deliberately while preserving v2Eleven-specific changes.
