# v2Eleven contribution rules

- Preserve the existing v2rayNG networking engine and protocol support unless a requested feature requires a reviewed change.
- Keep user-facing branding, subscription onboarding, server categorization, and the three primary destinations (Subscription, Turkey Gaming, Web Browsing) separate from core connection logic when practical.
- Default to Persian UI with an English option; verify right-to-left layout and readable contrast.
- Categorize a server as gaming when the server display name contains the Turkey flag or a supported Turkey country name/code. Do not assume every provider uses the same naming convention; make detection resilient and test it.
- Treat subscription URLs as credentials: never log them, commit them, or include real customer links in tests.
- Expiry and remaining traffic must only be shown when reliable subscription metadata is available. Do not fabricate values.
- Keep renewal and support destinations configurable in one place. Current public destinations: https://t.me/ElevenPing and https://t.me/ElevenJT.
- Never copy upstream release-signing workflows or secrets into this repository without review. Do not request or commit keystores, signing keys, passwords, or tokens.
- Make incremental changes on feature branches, run relevant tests/builds where possible, and do not merge to main without review.
- Preserve all applicable GPL-3.0 notices and source-distribution requirements.
